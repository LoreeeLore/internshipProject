package com.studlabs.quiz.util;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;

import java.util.*;

public class TestAuthenticationProvider {

    public static void configureTestAuthentication(String username, String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new User(username, "", authorities), "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
