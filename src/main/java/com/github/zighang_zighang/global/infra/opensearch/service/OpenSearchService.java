package com.github.zighang_zighang.global.infra.opensearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchClient client;
    private final ObjectMapper objectMapper;

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
            throw new RuntimeException("OpenSearch 인덱싱 실패", e);
        }
    }
}
