package com.example.demo.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        try {
            if (token != null) {
                log.info("Token received " + token);

                if (jwtTokenProvider.validateToken(token)) {
                    log.info("Valid token found");

                    Authentication auth = jwtTokenProvider.getAuthentication(token);

                    if (auth != null) {
                        log.info("Authentication for " + auth.getPrincipal());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }
        catch (InvalidJwtAuthenticationException ex) {
            log.info("Token invalid ("+ ex.getMessage()+")");
        }
        filterChain.doFilter(req, res);
    }

}
