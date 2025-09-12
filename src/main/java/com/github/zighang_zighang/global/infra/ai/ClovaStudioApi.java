package com.github.zighang_zighang.global.infra.ai;

import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import com.github.zighang_zighang.global.exception.GlobalExceptionCode;
import com.github.zighang_zighang.global.infra.ai.util.ClovaCompletionRequest;
import com.github.zighang_zighang.global.infra.ai.util.Sliding;
import com.github.zighang_zighang.global.property.ClovaProperty;
import jakarta.annotation.PostConstruct;
import kong.unirest.core.*;
import kong.unirest.core.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ClovaStudioApi {

    private final ClovaProperty clovaProperty;

    private static final UnirestInstance instance = Unirest.spawnInstance();

    @PostConstruct
    public void initializeInstance() {

        instance.config()
                .defaultBaseUrl("https://clovastudio.stream.ntruss.com")
                .addDefaultHeader("Authorization", "Bearer " + clovaProperty.getApiKey())
                .addDefaultHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
    }

    public String completion(ClovaCompletionRequest request) {

        JSONObject node = instance.post("/v3/chat-completions/HCX-007")
                .body(request)
                .asJsonAsync()
                .thenApply(HttpResponse::getBody)
                .thenApply(JsonNode::getObject)
                .join();

        assertApiSuccess(node);

        return node.getJSONObject("result")
                .getJSONObject("message")
                .getString("content");
    }

    public List<List<Double>> embed(String text) {

        //noinspection unchecked
        return Sliding.slide(text, 8192, 256)
                .stream()
                .map(content ->
                        instance.post("/v1/api-tools/embedding/v2")
                                .body(Map.ofEntries(Map.entry("text", content)))
                                .asJsonAsync()
                                .thenApply(HttpResponse::getBody)
                                .thenApply(JsonNode::getObject)
                )
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .peek(this::assertApiSuccess)
                .map(node -> node.getJSONObject("result"))
                .map(node -> node.getJSONArray("embedding"))
                .map(node -> (List<Double>) node.toList())
                .toList();
    }

    private void assertApiSuccess(JSONObject node) {

        if (!node.getJSONObject("status").getString("code").equals("20000")) {

            throw new ApiException(new ApiExceptionCode() {
                @Override
                public String getCode() {
                    return GlobalExceptionCode.EXTERNAL_API_ERROR.getCode();
                }

                @Override
                public String getMessage() {
                    return GlobalExceptionCode.EXTERNAL_API_ERROR.getMessage() + " : " + node.getJSONObject("status").getString("message");
                }
            });
        }
    }
}
