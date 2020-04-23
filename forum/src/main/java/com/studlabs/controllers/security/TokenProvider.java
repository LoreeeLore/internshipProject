package com.studlabs.controllers.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TokenProvider {

    public Claims getClaimsFromToken(String token) {
        //it is not best practice to use the token without verification, but the
        //token is verified in the interceptor(call to backend)
        String withoutSignature = token.substring(0, token.lastIndexOf('.') + 1);
        return Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
    }

    public String getUserNameFromClaim(Claims claims) {
        return (String) claims.get(JwtTokenField.NAME_ID.toString());
    }

    public String getFullNameFromClaim(Claims claims) {
        return (String) claims.get(JwtTokenField.UNIQUE_NAME.toString());
    }

    public List<AccessRole> getRolesFromClaim(Claims claims) {
        List<AccessRole> result = new ArrayList<>();

        Object roles = claims.get(JwtTokenField.ROLE.toString());
        if (roles instanceof String) {
            result.add(AccessRole.valueOf(((String) roles).toUpperCase()));
        } else if (roles instanceof List) {
            for (String role : ((List<String>) roles)) {
                result.add(AccessRole.valueOf((role.toUpperCase())));
            }
        }
        return result;
    }

}
