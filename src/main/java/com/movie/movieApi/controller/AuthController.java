package com.movie.movieApi.controller;

import com.movie.movieApi.auth.model.RefreshToken;
import com.movie.movieApi.auth.model.User;
import com.movie.movieApi.auth.repository.UserRepository;
import com.movie.movieApi.auth.service.AuthService;
import com.movie.movieApi.auth.service.JwtService;
import com.movie.movieApi.auth.service.RefreshTokenService;
import com.movie.movieApi.auth.utils.AuthResponse;
import com.movie.movieApi.auth.utils.LoginRequest;
import com.movie.movieApi.auth.utils.RefreshTokenRequest;
import com.movie.movieApi.auth.utils.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService, UserRepository userRepository) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
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

    @GetMapping("/profile")
    public ResponseEntity<Object> profile(Authentication authentication){
        var response = new HashMap<String, Object>();
        response.put("Username", authentication.getName());
        response.put("Authorities", authentication.getAuthorities());

        var user = userRepository.findByEmail(authentication.getName());

        var userToReturn = RegisterRequest.builder()
                .name(user.get().getName())
                .username(user.get().getUsername())
                .email(user.get().getEmail())
                .build();

        response.put("User", userToReturn);

        return ResponseEntity.ok(response);
    }
}
