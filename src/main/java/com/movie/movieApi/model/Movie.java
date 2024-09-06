package com.movie.movieApi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Veuillez indiquer le titre du film")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Veuillez indiquer le réalisateur du film")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Veuillez indiquer le studio qui a produit le film")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private String releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Veuillez fournir l'image du film")
    private String imageName;

}
