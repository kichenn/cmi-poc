package com.emotibot.cmiparser.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(1)
@WebFilter(filterName = "piceaFilter", urlPatterns = "/*")
@Slf4j
public class PiceaFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String requestURL = request.getRequestURL().toString();
        log.info(requestURL);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}