package com.studlabs.quiz.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.client.*;
import org.springframework.web.servlet.*;

import javax.net.ssl.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;

@Component
public class SessionManager implements HandlerInterceptor {

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final int STATUS_CODE_OK = 200;
    private String authenticationUrl;

    @Autowired
    private JwtTokenProvider jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    public SessionManager() {
        authenticationUrl = getAuthenticationServiceUrl();
        disableSslVerification();
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        String token = request.getHeader(HEADER_STRING);

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if (!authenticate(request, token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }

    private boolean authenticate(HttpServletRequest request, String token) {
        if (!StringUtils.isEmpty(authenticationUrl)) {
            if (authenticationLoginCheck(token).getStatusCodeValue() != STATUS_CODE_OK) {
                return false;
            }
        }

        token = token.replace(TOKEN_PREFIX, "");

        if (!jwtTokenUtil.isTokenValid(token)) {
            return false;
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username.isEmpty()) {
            return false;
        }

        String userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId.isEmpty()) {
            return false;
        }

        UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(token);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return true;
    }

    private ResponseEntity<String> authenticationLoginCheck(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_STRING, token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(authenticationUrl, HttpMethod.POST, entity, String.class);
    }

    private String getAuthenticationServiceUrl() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream("authentication.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (String) properties.get("URL");
    }

    private void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
