package com.chatApp.webSocketAPI.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.chatApp.webSocketAPI.Config.JwtConstant.SECRET_KEY;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.String.valueOf;

@Service
public class TokenProvider {

    SecretKey key = hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(Authentication authentication){
        String jwt = Jwts.builder().setIssuer("YipYap").setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime() + 600000))
                .claim("Email", authentication.getName())
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    public String getEmailFromToken(String token){
        String Token = token.substring(7);
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Token).getBody();

        return valueOf(claims.get("Email"));
    }
}
