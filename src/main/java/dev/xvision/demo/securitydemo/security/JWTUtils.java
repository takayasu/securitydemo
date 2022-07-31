package dev.xvision.demo.securitydemo.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateJWT(String userName,String roles){
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .claim("roles",roles)
                .signWith(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"))
                .compact()
                ;
    }

}
