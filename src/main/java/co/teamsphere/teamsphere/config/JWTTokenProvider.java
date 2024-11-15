package co.teamsphere.teamsphere.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTTokenProvider {

    SecretKey key = Keys.hmacShaKeyFor(JWTTokenConst.JWT_KEY.getBytes());

    public String generateJwtToken(Authentication authentication) {
        String jwt=Jwts.builder().setIssuer("YipYapTime")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("email", authentication.getName())
                .claim("authorities", "ROLE_USER")
                .signWith(key)
                .compact();

        return jwt;
    }

    public String getEmailFromToken(String token) {
        log.info("before claims ----------- ");

        token=token.substring(7);

        Claims claims= Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        return String.valueOf(claims.get("email"));
    }

    public String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritieSet=new HashSet<>();
        for(GrantedAuthority authority:collection) {
            authoritieSet.add(authority.getAuthority());
        }
        return String.join(",", authoritieSet);
    }
}
