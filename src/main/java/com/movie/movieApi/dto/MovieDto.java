package com.movie.movieApi.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Veuillez indiquer le titre du film")
    private String title;

    @NotBlank(message = "Veuillez indiquer le réalisateur du film")
    private String director;

    @NotBlank(message = "Veuillez indiquer le studio qui a produit le film")
    private String studio;

    private Set<String> movieCast;

    private String releaseYear;

    @NotBlank(message = "Veuillez fournir l'image du film")
    private String imageName;

    private String imageUrl;
}
