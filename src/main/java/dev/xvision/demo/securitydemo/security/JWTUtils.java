package dev.xvision.demo.securitydemo.security;

import dev.xvision.demo.securitydemo.bean.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.secret.expire.millisecond}")
    private long expireTerm;
    private static SignatureAlgorithm ALG = SignatureAlgorithm.HS512;

    private static String CLAME_ROLES_NAME = "roles";

    private static String PREFIX = "Bearer ";
    public String generateJWT(String userName,String roles){
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTerm))
                .claim(CLAME_ROLES_NAME,roles)
//                .signWith(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALG))
                .signWith(key,ALG)
                .compact()
                ;
    }

    public UserInfo parseToken(String token){
        if(token.startsWith(PREFIX)){
            token = token.substring(PREFIX.length()).trim();
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> parsedJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

        UserInfo info = new UserInfo();
        info.setUserName(parsedJws.getBody().getSubject());
        info.setRoles(parsedJws.getBody().get(CLAME_ROLES_NAME).toString());

        return info;
    }

}
