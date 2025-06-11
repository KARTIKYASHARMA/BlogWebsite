package net.sample.wordpress.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sample.wordpress.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String path = request.getServletPath();
//        if (path.startsWith("/register/") || path.startsWith("/login/")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String jwtToken = null;
        String username = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        String header = request.getHeader("Authorization");
        if (jwtToken == null && header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7);
        }

        if (jwtToken != null)
        {
            username=jwtService.extractUserName(jwtToken);
        }

        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
            UserDetails userDetails = applicationContext.getBean(UserDetailsService.class).loadUserByUsername(username);

            if (jwtService.validateToken(jwtToken, userDetails))
            {
                UsernamePasswordAuthenticationToken token=
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        System.out.println("Token: " + jwtToken);
        System.out.println("Username from token: " + username);
        System.out.println("Authenticated: " + SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
