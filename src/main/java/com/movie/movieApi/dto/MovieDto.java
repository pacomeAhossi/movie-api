package com.movie.movieApi.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    @NotBlank(message = "Veuillez indiquer le titre du film")
    private String title;

    @NotBlank(message = "Veuillez indiquer le réalisateur du film")
    private String director;

    @NotBlank(message = "Veuillez indiquer le studio qui a produit le film")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Veuillez fournir l'image du film")
    private String imageName = "default.png";

    private String imageUrl;

    private Date createdAt;

    private Date updatedAt;


    public MovieDto(Long id, String title, String director, String studio, Set<String> movieCast, Integer releaseYear, String imageName, String imageUrl) {
        this.title = title;
        this.director = director;
        this.studio = studio;
        this.movieCast = movieCast;
        this.releaseYear = releaseYear;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }
}
