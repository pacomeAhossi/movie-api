package com.movie.movieApi.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private Long expirationTime;

    // Extraction du username du JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // Récupération des infos du JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


    // Méthode pour décoder la clé secretKey
    private Key getSignInKey() {
        // On décode en bytes la clé seretKey
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }


    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // Méthode pour vérifier si le token est valide
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Méthode pour vérifier si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractDateExpiration(token).before(new Date());
    }

    // Méthode pour récupérer la date d'expiration du token
    private Date extractDateExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
}
