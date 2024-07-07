package com.starhealth.login.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.starhealth.login.exceptions.AuthenticationAPIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app-jwt-expiration-milliseconds}")
    private long jwtExpirationDate;


    // generate JWT token
    public String generateToken(Authentication authentication){

        String username = authentication.getName();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();

        logger.info(token);

        return token;
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // get username from JWT token
    public String getUsername(String token){

        return Jwts.parser()
               .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        }catch (MalformedJwtException malformedJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        }catch (ExpiredJwtException expiredJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        }catch (UnsupportedJwtException unsupportedJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        }catch (IllegalArgumentException illegalArgumentException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Jwt claims string is null or empty");
        }
    }
}
