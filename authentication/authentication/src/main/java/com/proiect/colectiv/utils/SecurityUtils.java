package com.proiect.colectiv.utils;

import com.proiect.colectiv.model.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;

public class SecurityUtils {
    public static long getCurrentUserID() {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) throw new EntityNotFoundException("User context not found.");

        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser user = (CurrentUser) authentication.getPrincipal();
            return user.getId();
        }
        throw new EntityNotFoundException("User context not found.");
    }
}
