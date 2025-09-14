package com.github.zighang_zighang.global.infra.opensearch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecommendedRecruitmentResponse;
import com.github.zighang_zighang.domain.resume.__embedding.repository.ResumeEmbeddingRepository;
import com.github.zighang_zighang.domain.resume.exception.ResumeException;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.opensearch.exception.OpenSearchException;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchClient client;
    private final WebClient.Builder webClientBuilder;
    private final ResumeEmbeddingRepository resumeEmbeddingRepository;

    @Value("${spring.opensearch.scheme}")
    private String scheme;

    @Value("${spring.opensearch.host}")
    private String host;

    @Value("${spring.opensearch.port}")
    private int port;

    @Value("${spring.opensearch.username}")
    private String username;

    @Value("${spring.opensearch.password}")
    private String password;

    public void indexResumeEmbedding(UUID resumeId, Object embedding) {
        try {
            Map<String, Object> source = Map.of(
                    "resumeId", resumeId.toString(),
                    "embedding", embedding
            );

            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index("resume-embeddings")
                    .id(resumeId.toString())
                    .document(source)
            );

            IndexResponse response = client.index(request);

        } catch (IOException e) {
//            e.printStackTrace();
            throw OpenSearchException.EXTRACT_FAILED.toException();
        }
    }

    public List<RecommendedRecruitmentResponse> searchSimilarRecruitmentsWithSource(List<Double> resumeEmbedding, int topK) {
        try {
            String query = """
        {
          "size": %d,
          "query": {
            "knn": {
              "vector": {
                "vector": %s,
                "k": %d
              }
            }
          }
        }
        """.formatted(topK, resumeEmbedding.toString(), topK);

            String url = String.format("%s://%s:%d/recruitments/_search", scheme, host, port);

            String basicAuth = username + ":" + password;

            String responseJson = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(basicAuth.getBytes()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

//            System.out.println(responseJson);

            // Jackson으로 _source 리스트 파싱
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            JsonNode root = mapper.readTree(responseJson);
            JsonNode hits = root.path("hits").path("hits");

            List<RecommendedRecruitmentResponse> results = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                RecommendedRecruitmentResponse dto = mapper.treeToValue(source, RecommendedRecruitmentResponse.class);
                results.add(dto);
            }

            return results;

        } catch (Exception e) {
//            e.printStackTrace();
            throw OpenSearchException.EXTRACT_FAILED.toException();
        }
    }

    // TODO: 추후 MySQL 임베딩이 아닌 오픈서치에서 바로 가져오는 방향으로 수정해볼 예정
    // TODO: 최신 1개의 임베딩이 아니라 사용자가 가진 자기소개서 전체의 종합(평균) 임베딩 값으로 수정 필요
    // TODO: +기록, 북마크, 메모 기반 추천도 가능하게 수정
    public float[] getLatestResumeEmbedding(User user) {
        String embeddingStr = resumeEmbeddingRepository.findLatestEmbeddingByUser(user)
                .orElseThrow(ResumeException.NOT_FOUND::toException);

        return parseEmbedding(embeddingStr);
    }

    private float[] parseEmbedding(String embeddingStr) {
        String[] parts = embeddingStr.replace("[", "").replace("]", "").split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

}
