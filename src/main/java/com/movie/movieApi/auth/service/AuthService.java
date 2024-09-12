package com.movie.movieApi.auth.service;

import com.movie.movieApi.auth.model.User;
import com.movie.movieApi.auth.model.UserRole;
import com.movie.movieApi.auth.repository.UserRepository;
import com.movie.movieApi.auth.utils.AuthResponse;
import com.movie.movieApi.auth.utils.LoginRequest;
import com.movie.movieApi.auth.utils.RegisterRequest;
import com.movie.movieApi.exceptions.UserWithUsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest){
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        // On génère le token et le refresh token
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest){
        // On authentifie l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // On vérifie que cet user existe dans la DB, sinon on soulève une exception
        var user = userRepository.findByEmail(loginRequest.getEmail()).
                orElseThrow(() -> new UserWithUsernameNotFoundException(" User not found!"));

        // On génère le jeton d'accès et le refresh token
        String accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
