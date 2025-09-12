package com.github.zighang_zighang.global.infra.ai.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sliding {

    public static List<String> slide(String text, int maxLength, int overlap) {

        List<String> segments = new ArrayList<>();
        int start = 0;

        char[] chars = text.toCharArray();

        while (start < chars.length) {
            int end = Math.min(start + maxLength, chars.length);
            segments.add(new String(Arrays.copyOfRange(chars, start, end)));
            if (end == chars.length) break;
            start += (maxLength - overlap);
        }

        return segments;
    }
}
