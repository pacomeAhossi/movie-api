package com.movie.movieApi.auth.service;

import com.movie.movieApi.auth.model.RefreshToken;
import com.movie.movieApi.auth.model.User;
import com.movie.movieApi.auth.repository.RefreshTokenRepository;
import com.movie.movieApi.auth.repository.UserRepository;
import com.movie.movieApi.exceptions.RefreshTokenExpiredException;
import com.movie.movieApi.exceptions.RefreshTokenNotFoundException;
import com.movie.movieApi.exceptions.UserWithUsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String username){
        // On vérifie si cet utilisateur existe dans la DB
        User user = userRepository.findByEmail(username).
            orElseThrow(() -> new UserWithUsernameNotFoundException("User not found with email : " + username));

        // On récupère le refreshToken du user trouvé
        RefreshToken refreshToken = user.getRefreshToken();

        // Vérification de la présence d'un refreshToken
        // Si le refreshToken est null, on lui délivre un refreshToken
        // Sinon on retourne juste son refreshToken
        if (refreshToken == null){
            long refreshTokenValidity = 30*60*1000;
             RefreshToken newRefreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                     .user(user)
                    .build();

             return refreshTokenRepository.save(newRefreshToken);
        }

        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        // On recherche le refreshToken par le champ "refreshToken" pour vérifier sa présence dans la DB
        RefreshToken existingRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).
                orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found !"));

        // On vérifie si le refreshToken est expiré ou pas
        // s'il est expiré on le supprime de la DB
        if (existingRefreshToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(existingRefreshToken);
            throw new RefreshTokenExpiredException("Refresh token expired !");
        }

        return existingRefreshToken;
    }
}
