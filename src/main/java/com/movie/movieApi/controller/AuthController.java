package com.movie.movieApi.controller;

import com.movie.movieApi.auth.model.RefreshToken;
import com.movie.movieApi.auth.model.User;
import com.movie.movieApi.auth.service.AuthService;
import com.movie.movieApi.auth.service.JwtService;
import com.movie.movieApi.auth.service.RefreshTokenService;
import com.movie.movieApi.auth.utils.AuthResponse;
import com.movie.movieApi.auth.utils.LoginRequest;
import com.movie.movieApi.auth.utils.RefreshTokenRequest;
import com.movie.movieApi.auth.utils.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        // On vérifie que l'objet refreshToken envoyé est valide et existe dans la DB
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());

        // On récupère le user associé à ce refresh token
        User user = refreshToken.getUser();

        // On génère un nouveau accessToken
        var accessToken = jwtService.generateToken(user);

        // On retourne le nouveau accessToken ainsi que le refreshToken trouvé

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
