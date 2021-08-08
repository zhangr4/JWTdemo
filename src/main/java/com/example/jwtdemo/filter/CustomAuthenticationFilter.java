package com.example.jwtdemo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


//the filter extends the username and password filter, which normally has attempt authentication and success authentication, fail authentication, etc.
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // the filter depend on authentication manager
    private final AuthenticationManager authenticationManager;

    //have a constructor to involve DI
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
       String username = request.getParameter("username");
       String password = request.getParameter("password");
       log.info("username is {}", username);
       log.info("password is: {}", password);
       UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
       return authenticationManager.authenticate(authenticationToken);
    }

    //once login succeed, it generates and provides the token to the client
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException{
        /*
        * authentication getprincipal() method returns the current client user information it normally can be transferred as
        * as userdetails instance
        * the user class casted below refer from userdetails class
        */
        User user = (User)authentication.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        /*
        creat jwt, subject is username, expire time is 10 minutes,
        issuer is the request url,
        claim is the entity represent client status and other meta data, which use
        here is the authority of different roles  */
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        /*response.setHeader("access_token",access_token);
        response.setHeader("refresh-token",refresh_token);*/
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token",refresh_token);

        //application json value from media type of package org.springframework.http
        response.setContentType(APPLICATION_JSON_VALUE);

        //objectmapper is from jackson, it intends to map the json to object or reverse
        //in this case, it maps the object(hashmap) to json
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);











    }
}
