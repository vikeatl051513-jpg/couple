package com.example.couplebackend.service;

import com.example.couplebackend.common.BusinessException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeChatSessionClient {
    private final String appId;
    private final String appSecret;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private volatile String accessToken = "";
    private volatile Instant accessTokenExpireAt = Instant.EPOCH;

    public WeChatSessionClient(
            @Value("${wechat.mini-program.app-id}") String appId,
            @Value("${wechat.mini-program.app-secret}") String appSecret
    ) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public Session code2Session(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException("缺少微信登录 code");
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + encode(appId)
                + "&secret=" + encode(appSecret)
                + "&js_code=" + encode(code)
                + "&grant_type=authorization_code";
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = response.body();
            int errcode = jsonInt(body, "errcode", 0);
            if (errcode != 0) {
                throw new BusinessException("微信登录失败：" + jsonString(body, "errmsg", "unknown"));
            }
            String openid = jsonString(body, "openid", "");
            if (openid.isBlank()) {
                throw new BusinessException("微信登录失败：未返回 openid");
            }
            return new Session(openid, jsonString(body, "unionid", ""));
        } catch (IOException e) {
            throw new BusinessException("微信登录失败：网络异常");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("微信登录失败：请求被中断");
        }
    }

    public PhoneNumber getPhoneNumber(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException("缺少手机号授权 code");
        }
        try {
            return requestPhoneNumber(code, miniProgramAccessToken(), true);
        } catch (IOException e) {
            throw new BusinessException("手机号授权失败：网络异常");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("手机号授权失败：请求被中断");
        }
    }

    private PhoneNumber requestPhoneNumber(String code, String token, boolean retryOnExpiredToken) throws IOException, InterruptedException {
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + encode(token);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"code\":\"" + jsonEscape(code) + "\"}", StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String body = response.body();
        int errcode = jsonInt(body, "errcode", 0);
        if ((errcode == 40001 || errcode == 42001) && retryOnExpiredToken) {
            clearAccessToken();
            return requestPhoneNumber(code, miniProgramAccessToken(), false);
        }
        if (errcode != 0) {
            throw new BusinessException("手机号授权失败：" + jsonString(body, "errmsg", "unknown"));
        }
        String phoneNumber = jsonString(body, "phoneNumber", "");
        if (phoneNumber.isBlank()) {
            throw new BusinessException("手机号授权失败：未返回手机号");
        }
        return new PhoneNumber(phoneNumber, jsonString(body, "purePhoneNumber", phoneNumber), jsonString(body, "countryCode", ""));
    }

    private synchronized String miniProgramAccessToken() throws IOException, InterruptedException {
        Instant now = Instant.now();
        if (!accessToken.isBlank() && accessTokenExpireAt.isAfter(now.plusSeconds(30))) {
            return accessToken;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token"
                + "?grant_type=client_credential"
                + "&appid=" + encode(appId)
                + "&secret=" + encode(appSecret);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String body = response.body();
        int errcode = jsonInt(body, "errcode", 0);
        if (errcode != 0) {
            throw new BusinessException("微信 access_token 获取失败：" + jsonString(body, "errmsg", "unknown"));
        }
        String nextToken = jsonString(body, "access_token", "");
        if (nextToken.isBlank()) {
            throw new BusinessException("微信 access_token 获取失败：未返回 access_token");
        }
        int expiresIn = Math.max(60, jsonInt(body, "expires_in", 7200));
        accessToken = nextToken;
        accessTokenExpireAt = now.plusSeconds(Math.max(30, expiresIn - 120L));
        return accessToken;
    }

    private synchronized void clearAccessToken() {
        accessToken = "";
        accessTokenExpireAt = Instant.EPOCH;
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String jsonEscape(String value) {
        return (value == null ? "" : value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String jsonString(String json, String key, String fallback) {
        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);
        if (keyIndex < 0) {
            return fallback;
        }
        int colonIndex = json.indexOf(':', keyIndex + pattern.length());
        int startQuote = json.indexOf('"', colonIndex + 1);
        if (colonIndex < 0 || startQuote < 0) {
            return fallback;
        }
        int endQuote = json.indexOf('"', startQuote + 1);
        if (endQuote < 0) {
            return fallback;
        }
        return json.substring(startQuote + 1, endQuote);
    }

    private int jsonInt(String json, String key, int fallback) {
        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);
        if (keyIndex < 0) {
            return fallback;
        }
        int colonIndex = json.indexOf(':', keyIndex + pattern.length());
        if (colonIndex < 0) {
            return fallback;
        }
        int cursor = colonIndex + 1;
        while (cursor < json.length() && Character.isWhitespace(json.charAt(cursor))) {
            cursor++;
        }
        int start = cursor;
        while (cursor < json.length() && (Character.isDigit(json.charAt(cursor)) || json.charAt(cursor) == '-')) {
            cursor++;
        }
        if (start == cursor) {
            return fallback;
        }
        return Integer.parseInt(json.substring(start, cursor));
    }

    public record Session(String openid, String unionid) {
    }

    public record PhoneNumber(String phoneNumber, String purePhoneNumber, String countryCode) {
    }
}
