package com.project.shopapp.filters;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // filterChain.doFilter(request,response); enable bypass -- cho đi qua hết
        try {
            if(isBypassToken(request)){
                filterChain.doFilter(request,response);
            }
            final String authHeader = request.getHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                final String token = authHeader.substring(7);
                String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
                if(phoneNumber != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null){
                    User userDetails =(User) userDetailsService.loadUserByUsername(phoneNumber);
                    if(jwtTokenUtils.validateToken(token,userDetails)){
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
            else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
                return;
            }
            filterChain.doFilter(request,response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        }
    }
    private boolean isBypassToken(@NonNull HttpServletRequest request){
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Method: " + request.getMethod());
        final List<Pair<String,String>> bypassTokens = Arrays.asList(
//                Pair.of(apiPrefix+"/products","GET"),
//                Pair.of(apiPrefix+"/categories","GET"),
                Pair.of(apiPrefix+"/users/login","POST"),
                Pair.of(apiPrefix+"/users/register","POST")
        );
        for(Pair<String,String> bypassToken : bypassTokens){
            if(request.getServletPath().contains(bypassToken.getLeft())
            && request.getMethod().contains(bypassToken.getRight())
            ){
                return true;
            }
        }
        return false;
    }
}
