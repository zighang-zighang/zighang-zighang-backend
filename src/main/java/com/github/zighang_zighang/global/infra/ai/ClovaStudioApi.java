package com.github.zighang_zighang.global.infra.ai;

import com.github.zighang_zighang.global.property.ClovaProperty;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ClovaStudioApi {

    private static final String BASE_URL = "https://clovastudio.stream.ntruss.com/v1/api-tools";
    private static final int MAX_LENGTH = 8192;
    private static final int OVERLAP = 256;

    private final ClovaProperty clovaProperty;

    public List<List<Double>> embed(String text) {

        List<CompletableFuture<HttpResponse<JsonNode>>> futures = sliding(text).stream()
                .map(content ->
                        Unirest.post(BASE_URL + "/embedding/v2")
                                .header("Authorization", "Bearer " + clovaProperty.getApiKey())
                                .header("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .body(Map.ofEntries(Map.entry("text", content)))
                                .asJsonAsync()
                )
                .toList();

        //noinspection unchecked
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(HttpResponse::isSuccess)
                .map(HttpResponse::getBody)
                .map(JsonNode::getObject)
                .filter(node -> node.getJSONObject("status").getString("code").equals("20000"))
                .map(node -> node.getJSONObject("result"))
                .map(node -> node.getJSONArray("embedding"))
                .map(node -> (List<Double>) node.toList())
                .toList();
    }

    private List<String> sliding(String text) {

        List<String> segments = new ArrayList<>();
        int start = 0;

        char[] chars = text.toCharArray();

        while (start < chars.length) {
            int end = Math.min(start + MAX_LENGTH, chars.length);
            segments.add(new String(Arrays.copyOfRange(chars, start, end)));
            if (end == chars.length) break;
            start += (MAX_LENGTH - OVERLAP);
        }

        return segments;
    }
}
