package com.github.zighang_zighang.global.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class RedirectValidator {

    @Value("#{'${app.oauth2.authorized-redirect-uris}'.split(',')}")
    private List<String> authorizedUris;

    public boolean isAuthorized(String uri) {
        try {
            URI target = URI.create(uri);
            return authorizedUris.stream()
                    .map(String::trim)
                    .map(URI::create)
                    .anyMatch(allowed -> allowed.getHost().equalsIgnoreCase(target.getHost()));
        } catch (Exception e) {
            return false;
        }
    }
}

