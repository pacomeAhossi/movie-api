package com.movie.movieApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.movieApi.dto.MovieDto;
import com.movie.movieApi.dto.MoviePageResponse;
import com.movie.movieApi.exceptions.EmptyFileException;
import com.movie.movieApi.service.MovieService;
import com.movie.movieApi.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;


    /**
     * Read - Get all movies
     * @return - Une liste de movies entièrement rempli
     */
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }


    /**
     * Read - Récupère un movie
     * @param id L'id du movie
     * @return Un objet movie entièrement rempli
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getDetailsMovie(@PathVariable final Long id){
        return ResponseEntity.ok(movieService.getDetailsMovie(id));
    }


    /**
     * Delete - Delete un movie
     * @param id - L'id du movie à supprimer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable final Long id) throws IOException {
        Boolean isDeleted = movieService.deleteMovie(id);

        if (isDeleted){
            return ResponseEntity.ok("Le cours avec l'id " + id + " a été supprimé avec succès");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le cours avec l'id " + id + "n'existe pas");
        }
    }




    /**
     * Create - Créer un nouveau movie
     * @param movieDto Un objet movie
     * @param file - L'image du movie
     * @return L'objet movie créé
     */
    @PostMapping
    public ResponseEntity<MovieDto> createMovie(
            @Valid @ModelAttribute MovieDto movieDto,
            @RequestPart MultipartFile file) throws IOException, EmptyFileException {

        if (file.isEmpty()){
            throw new EmptyFileException("Fichier non trouvé! Veuillez en fournir un nouveau");
        }
        /*MovieDto movieDtoObject = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.createMovie(movieDtoObject, file), HttpStatus.CREATED);*/

        return new ResponseEntity<>(movieService.createMovie(movieDto, file), HttpStatus.CREATED);

    }

    private MovieDto convertToMovieDto(String movieDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDto, MovieDto.class);
    }




    /**
     * Update - Modification d'un movie existant
     *
     * @param id        - L'id du movie à modifier
     * @param movieDto     - L'objet movie à modifier
     * @param file - L'image du movie (optionnel)
     * @return L'objet movie modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable final Long id,
            @RequestPart(required = false) MultipartFile file,
            @ModelAttribute MovieDto movieDto) throws IOException {

        return ResponseEntity.ok(movieService.updateMovie(id, movieDto, file));
    }


    @GetMapping("/all-movies-pagination")
    public ResponseEntity<MoviePageResponse> gatAllMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }


    @GetMapping("/all-movies-paginationAndSorting")
    public ResponseEntity<MoviePageResponse> gatAllMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String direction
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, direction));
    }
}
