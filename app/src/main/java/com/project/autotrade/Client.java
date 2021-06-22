package com.project.autotrade;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Client {

    String accessKey = "64Jwg3zgt4lBjxxtJz0xrXmz3KoHV27IpwP6syFm";
    String secretKey = "pUa1F1QQyXoPE5ZGcx87VVVbCnwCGv1QbYGFOZ1D";
    String serverUrl = "https://api.upbit.com";

    public HttpEntity getEntity() throws IOException, NoSuchAlgorithmException {

        String jwtToken = Jwts.builder()
                .claim("access_key", accessKey)
                .claim("nonce", UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        String authenticationToken = "Bearer " + jwtToken;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(serverUrl + "/v1/accounts");
        request.setHeader("Content-Type", "application/json");
        request.addHeader("Authorization", authenticationToken);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }
}
