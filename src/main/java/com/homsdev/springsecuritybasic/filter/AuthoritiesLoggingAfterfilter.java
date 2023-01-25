package com.homsdev.springsecuritybasic.filter;

import jakarta.servlet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class AuthoritiesLoggingAfterfilter implements Filter {

    private final Logger LOG = LogManager.getLogger(AuthoritiesLoggingAfterfilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            LOG.info("User {} is succesfully authenticated and has authorities {}",
                    authentication.getName(),
                    authentication.getAuthorities().toString());
        }
        chain.doFilter(request,response);
    }
}
