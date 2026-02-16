package demago.khjv2.global.security.jwt;

import demago.khjv2.global.security.userdetails.CustomUserDetails;
import demago.khjv2.global.security.userdetails.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtProperties properties;
    private final SecretKey key;

    public static final String CLAIM_TYPE = "type";
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String CLAIM_AUTHORITIES = "authorities";

    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService,
                            JwtProperties properties) {
        this.customUserDetailsService = customUserDetailsService;
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getAccessTokenExpiration());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim(CLAIM_TYPE, TOKEN_TYPE_ACCESS)
                .claim("username", username)
                .claim(CLAIM_AUTHORITIES, java.util.List.of("ROLE_USER"))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
//        Long memberId = Long.parseLong(claims.getSubject());

        Long memberId;
        try {
            memberId = Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new JwtException("잘못된 토큰 형식입니다", e);
        }

        CustomUserDetails userDetails = customUserDetailsService.loadUserById(memberId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                userDetails.getAuthorities()
        );
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
