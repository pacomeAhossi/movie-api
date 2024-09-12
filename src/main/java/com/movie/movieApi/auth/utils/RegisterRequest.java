package com.movie.movieApi.auth.utils;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    @NotBlank(message = "Ce username ne peut pas être vide")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "Entrez un format valide d'email")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe  ne peut pas être vide")
    @Size(min = 5, message = "Le mot de passe doit être au moins de 5 caractères")
    private String password;
}
