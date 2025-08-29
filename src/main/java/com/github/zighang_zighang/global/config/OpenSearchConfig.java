package com.github.zighang_zighang.global.config;

import com.github.zighang_zighang.global.property.OpenSearchProperty;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

    private final OpenSearchProperty openSearchProperty;

    @Bean
    public OpenSearchClient openSearchClient() {

        return new OpenSearchClient(httpClient5Transport());
    }

    @Bean(destroyMethod = "close")
    public ApacheHttpClient5Transport httpClient5Transport() {

        HttpHost host = new HttpHost(
                openSearchProperty.getScheme().getId(),
                openSearchProperty.getHost(),
                openSearchProperty.getPort()
        );

        return ApacheHttpClient5TransportBuilder.builder(host)
                .setHttpClientConfigCallback(builder -> builder
                        .setDefaultCredentialsProvider(basicCredentialsProvider())
                        .setConnectionManager(
                                PoolingAsyncClientConnectionManagerBuilder.create()
                                        .setTlsStrategy(
                                                ClientTlsStrategyBuilder.create()
                                                        .setSslContext(sslContext())
                                                        .buildAsync()
                                        )
                                        .build()
                        )
                )
                .build();
    }

    @Bean
    public BasicCredentialsProvider basicCredentialsProvider() {

        HttpHost host = new HttpHost(
                openSearchProperty.getScheme().getId(),
                openSearchProperty.getHost(),
                openSearchProperty.getPort()
        );

        BasicCredentialsProvider credentials = new BasicCredentialsProvider();

        credentials.setCredentials(
                new AuthScope(host),
                new UsernamePasswordCredentials(
                        openSearchProperty.getUsername(),
                        openSearchProperty.getPassword().toCharArray()
                )
        );

        return credentials;
    }

    @Bean
    @SneakyThrows({NoSuchAlgorithmException.class, KeyStoreException.class, KeyManagementException.class})
    public SSLContext sslContext() {

        return SSLContextBuilder
                .create()
                .loadTrustMaterial(null, (chains, authType) -> true)
                .build();
    }
}
