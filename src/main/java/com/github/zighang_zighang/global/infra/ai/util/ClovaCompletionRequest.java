package com.github.zighang_zighang.global.infra.ai.util;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public record ClovaCompletionRequest(
        @Singular List<Message> messages,
        Thinking thinking,
        float topP,
        float temperature,
        int maxCompletionTokens,
        boolean includeAiFilters
) {

    public record Message(
            Role role,
            List<Content> content
    ) {

        public enum Role {
            system,
            user,
            assistant
        }

        public record Content(
                Type type,
                String text
        ) {

            public enum Type {
                text
            }
        }

        public static Message of(Role role, String text) {
            return new Message(role, List.of(new Content(Content.Type.text, text)));
        }
    }

    public record Thinking(
            Effort effort
    ) {

        public enum Effort {
            none,
            low,
            medium,
            high
        }
    }
}
