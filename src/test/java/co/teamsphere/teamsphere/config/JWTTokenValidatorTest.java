package co.teamsphere.teamsphere.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTTokenValidatorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;
    @Mock
    private Claims claims;

    @InjectMocks
    private JWTTokenValidator jwtTokenValidator;
    private JWTTokenProvider jwtTokenProvider;
    private final String VALID_TOKEN = "Bearer validToken";
    private final String EXPIRED_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJZaXBZYXBUaW1lIiwiaWF0IjoxNzMxNjQ2ODU0LCJleHAiOjE3MzE2NDY4NTQsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImVtYWlsIjoidGVzdHVzZXJAdGVzdHVzZXIuY29tIn0.94f7s6Sn1TGCyCFFmWO4QoSCsJVWDLQpyLELihJV2C1EQFfh8yFOnXi7MBTWD4QC1jkC8HfPfBC05QABwkDJQw";
    private final String INVALID_TOKEN = "Bearer invalidToken";

    @Test
    void testValidJwtToken() throws ServletException, IOException {

        SecretKey key = Keys.hmacShaKeyFor(JWTTokenConst.JWT_KEY.getBytes());

        String token = Jwts.builder().setIssuer("YipYapTime")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", "user")
                .claim("authorities", "ROLE_USER, ROLE_ADMIN")
                .claim("email", "user@user.com")
                .signWith(key)
                .compact();

        // Setup
        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn("Bearer "+token);

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user@user.com", auth.getName());
        assertTrue(auth.getAuthorities().containsAll(AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN")));
    }
    @Test
    public void testValidToken() throws IOException, ServletException {

        SecretKey key = Keys.hmacShaKeyFor(JWTTokenConst.JWT_KEY.getBytes());

        String token = Jwts.builder().setIssuer("YipYapTime")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", "testuser")
                .claim("authorities", "ROLE_USER")
                .claim("email","testuser@testuser.com")
                .signWith(key)
                .compact();

        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn("Bearer "+token);


        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(authentication.getAuthorities().toArray()[0].toString(), "ROLE_USER");
        assertTrue(AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_USER"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testExpiredJwtToken() throws ServletException, IOException {
        // Setup
        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn(EXPIRED_TOKEN);

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired");
        verify(filterChain, never()).doFilter(request, response);
    }
    @Test
    void testInvalidJwtToken() throws ServletException, IOException {
        // Setup
        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn(INVALID_TOKEN);

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        verify(filterChain, never()).doFilter(request, response);
    }
}
