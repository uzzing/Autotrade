package com.project.autotrade.trade;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class Client {

    String accessKey = "8X1Qudp7NYxlsLHwS1Tj1jC1Kqjz3TY1SpkznKix";
    String secretKey = "oQtMqvQxHr7xZJe8wFXcqbMboGcXxxjLDbYrnPkT";
    String serverUrl = "https://api.upbit.com";

    // change the name of method
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

    public HttpEntity postEntity(HashMap<String, String> params) throws IOException, NoSuchAlgorithmException {

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(serverUrl + "/v1/orders");
        request.setHeader("Content-Type", "application/json");
        request.addHeader("Authorization", authenticationToken);
        request.setEntity(new StringEntity(new Gson().toJson(params)));

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }

    public HttpEntity getAllCoins() throws IOException, NoSuchAlgorithmException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(serverUrl + "/v1/market/all?isDetails=false");
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }

    public HttpEntity getTickerData(String coinNm) throws IOException, NoSuchAlgorithmException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(serverUrl + "/v1/ticker?markets=" + coinNm);
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }

    public HttpEntity getCandleData(String coinNm, int minute) throws IOException, NoSuchAlgorithmException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(serverUrl + "/v1/candles/minutes/" + minute + "?count=1&market=" + coinNm);
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }

    public HttpEntity deleteOrder(HashMap<String, String> params) throws IOException, NoSuchAlgorithmException {

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete request = new HttpDelete(serverUrl + "/v1/order?" + queryString);
        request.setHeader("Content-Type", "application/json");
        request.addHeader("Authorization", authenticationToken);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        return entity;
    }
}
