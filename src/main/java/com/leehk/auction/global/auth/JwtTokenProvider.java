package com.leehk.auction.global.auth;

import com.leehk.auction.domain.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.access-token-validity-seconds}")
    private Long accessTokenValiditySeconds;

    @Value("${spring.jwt.refresh-token-validity-seconds}")
    private Long refreshTokenValiditySeconds;

    /**
     * 주어진 사용자에 대한 새로운 액세스 토큰을 생성합니다.
     * 이메일과 닉네임과 같은 사용자 관련 클레임을 포함하며
     * 설정된 속성을 기반으로 정의된 유효 기간을 가집니다.
     *
     * @param user 액세스 토큰을 생성할 사용자
     * @return 서명된 JWT 액세스 토큰 문자열
     */
    public String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("nickname", user.getNickname());

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValiditySeconds * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 주어진 사용자에 대한 새로운 리프레시 토큰을 생성합니다.
     * 토큰은 사용자의 고유 식별자와 연결되어 있으며
     * 설정된 속성을 기반으로 정의된 만료 기간을 가집니다.
     *
     * @param user 리프레시 토큰을 생성할 사용자
     * @return 서명된 JWT 리프레시 토큰 문자열
     */
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValiditySeconds * 1000);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 주어진 JWT 토큰에서 사용자 ID를 추출합니다.
     * 토큰을 파싱하여 클레임을 검색하고 주체 필드를 사용자 ID로 해석합니다.
     *
     * @param token 사용자 ID를 추출할 JWT 토큰
     * @return Long 값으로 표현된 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    /**
     * 주어진 JWT 토큰에서 이메일 클레임을 추출하여 반환합니다.
     *
     * @param token 이메일을 추출할 JWT 토큰
     * @return 이메일 클레임을 String으로 반환하며, 클레임이 존재하지 않거나 유효하지 않은 경우 null을 반환
     */
    public String getEmail(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("email");
    }

    /**
     * 제공된 JWT 토큰에서 "닉네임" 클레임을 추출하여 반환합니다.
     *
     * @param token 닉네임을 추출할 JWT 토큰
     * @return 닉네임 클레임을 String으로 반환하며, 클레임이 존재하지 않거나 유효하지 않은 경우 null을 반환
     */
    public String getNickname(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("nickname");
    }

    /**
     * 설정된 비밀 키를 사용하여 주어진 JWT 토큰을 검증합니다.
     * 토큰이 유효하고 만료되지 않았다면 true를 반환합니다.
     * 예외가 발생하거나 유효하지 않은 경우 false를 반환합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}