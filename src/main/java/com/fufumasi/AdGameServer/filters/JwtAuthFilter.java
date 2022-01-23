package com.fufumasi.AdGameServer.filters;

import com.fufumasi.AdGameServer.services.TokenHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Inject
    private TokenHandler tokenhandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!request.getRequestURI().equals("/login") && !request.getRequestURI().equals("/test")) { // login 페이지는 token 필터에서 제외
            try {
                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                // System.out.println(authorizationHeader);
                Claims claims = tokenhandler.parseJwtToken(authorizationHeader);
                // System.out.println("token name: " + claims.get("name"));
                // request.setAttribute("claims", claims);
            } catch (IllegalArgumentException e) {
                System.out.println(request.getRequestURI() + " IllegalArgumentException ");
                response.sendError(401);
                return;
            } catch (ExpiredJwtException e) {
                System.out.println(request.getRequestURI() + " ExpiredJwt");
                response.sendError(401);
                return;
            } catch (Exception e) {
                System.out.println(request.getRequestURI() + " Exception");
                response.sendError(401);
                return;
            }

        }

        filterChain.doFilter(request, response);
    }
}
