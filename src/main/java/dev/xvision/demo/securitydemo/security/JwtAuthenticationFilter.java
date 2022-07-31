package dev.xvision.demo.securitydemo.security;

import dev.xvision.demo.securitydemo.bean.UserInfo;
import io.jsonwebtoken.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils utils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeadere = request.getHeader(HttpHeaders.AUTHORIZATION);
        //First get JwtToken
        if(!checkHeader(authHeadere)){
            filterChain.doFilter(request,response);
            return;
        }

        UserInfo info = null;
        //Second JwtToken Validation & Parse
        try {
            info = utils.parseToken(authHeadere);
        }catch(Exception ex){
            filterChain.doFilter(request,response);
            return;
        }

        //Third Authentication Created
        authenticated(info);

        filterChain.doFilter(request,response);
    }

    private boolean checkHeader(String authHeadere){

        //AuthTokenがない場合
        if(StringUtils.hasText(authHeadere) && !authHeadere.startsWith("Bearer ")){
            return false;
        }
        return true;
    }

    private void authenticated(UserInfo info){
        SecurityContextHolder.getContext().setAuthentication(convert(info));
    }

    private UsernamePasswordAuthenticationToken convert(UserInfo info){
        return UsernamePasswordAuthenticationToken
                .authenticated(info.getUserName(),null,convert(info.getRoles()));
    }

    private List<GrantedAuthority> convert(String roles){
        return Arrays.asList(roles.split(",")).stream()
                .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    }

}
