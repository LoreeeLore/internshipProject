package com.studlabs.controllers.security;

import io.jsonwebtoken.*;
import org.apache.logging.log4j.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);

    private static final String AUTHORITIES_KEY = "role";
    private static final String USERNAME_CLAIM = "username";


    String getUniqueNameFromToken(final String token) {
        final Claims claims = getClaims(token);
        Object uniqueNameClaim = claims.get(USERNAME_CLAIM);

        return uniqueNameClaim == null ? "" : uniqueNameClaim.toString();
    }

    String getUserIdFromToken(final String token) {
        final Claims claims = getClaims(token);
        Object nameIdClaim = claims.getSubject();

        return nameIdClaim == null ? "" : nameIdClaim.toString();
    }

    boolean isTokenValid(final String token) {
        try {
            String newToken = token.substring(0, token.lastIndexOf(".") + 1);
            Jwts.parser().parseClaimsJwt(newToken);
        } catch (Exception ex) {
            LOGGER.error("error validating jwt token", ex);
            return false;
        }
        return true;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final String token) {
        Claims claims = getClaims(token);
        Object roleClaim = claims.get(AUTHORITIES_KEY);
        Collection<GrantedAuthority> authorities = Collections.emptyList();

        if (roleClaim != null) {
            authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        String username = getUniqueNameFromToken(token);
        return new UsernamePasswordAuthenticationToken(new User(username, "", authorities), "", authorities);
    }

    private Claims getClaims(String token) {
        token = token.substring(0, token.lastIndexOf(".") + 1);
        final Jwt<?, Claims> claimsJwt = Jwts.parser().parseClaimsJwt(token);

        return claimsJwt.getBody();
    }
}
