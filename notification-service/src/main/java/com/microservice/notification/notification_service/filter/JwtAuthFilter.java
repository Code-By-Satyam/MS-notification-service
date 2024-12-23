package com.microservice.notification.notification_service.filter;

import com.microservice.notification.notification_service.services.JwtService;
import com.microservice.notification.notification_service.services.UserInfoUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter intercepts HTTP requests to validate JWT tokens in the "Authorization" header.
 * If the token is valid, it authenticates the user and sets the user details in the Spring Security context.
 * <p>
 * The filter runs once per request, ensuring that any authenticated user can be verified
 * and their permissions can be checked during subsequent requests.
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    /**
     * This method processes the incoming HTTP request and checks if a valid JWT token exists in the "Authorization" header.
     * If the token is valid, it authenticates the user and sets the user details in the Spring Security context.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain that should be invoked after processing the request.
     * @throws ServletException If an error occurs during the request processing.
     * @throws IOException      If an I/O error occurs during the request processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if the Authorization header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Extract the token
            username = jwtService.extractUsername(token);  // Extract the username from the token
        }

        // If a valid username is found and the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // Load user details
            if (jwtService.validateToken(token, userDetails)) {  // Validate the JWT token
                // If token is valid, authenticate the user
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Set details for the authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);  // Set authentication in the security context
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
