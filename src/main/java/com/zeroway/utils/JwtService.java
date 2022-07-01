package com.zeroway.utils;

import com.zeroway.common.BaseException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;

import static com.zeroway.common.BaseResponseStatus.*;

@Service
public class JwtService {

    private final int accessTokenMs = 1000 * 60 * 60;   // 1시간
    private final int refreshTokenMs = 1000 * 60 * 60 * 24 * 14;    // 2주

    private final static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private final static Key key = Keys.secretKeyFor(signatureAlgorithm);

    /**
     * JWT 생성
     */
    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx", userId)
                .setSubject(userId.toString())
                .setExpiration(new Date(now.getTime() + accessTokenMs))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Key key = Keys.secretKeyFor(signatureAlgorithm);
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx", userId)
                .setExpiration(new Date(now.getTime() + refreshTokenMs))
                .signWith(key)
                .compact();
    }


    /**
     * Header에서 X-ACCESS-TOKEN으로 JWT 추출
     */
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /**
     * JWT에서 userIdx 추출
     */
    public Long getUserIdx() throws BaseException {
        // 1. JWT 추출
        String token = getJwt();
        if (token == null || token.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key.getEncoded())
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Long.class);
    }
}