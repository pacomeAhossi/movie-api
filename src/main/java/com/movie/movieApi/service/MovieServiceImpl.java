package com.movie.movieApi.service;

import com.movie.movieApi.dto.MovieDto;
import com.movie.movieApi.exceptions.FileExistsException;
import com.movie.movieApi.exceptions.MovieNotFoundException;
import com.movie.movieApi.model.Movie;
import com.movie.movieApi.repository.MovieRepository;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class MovieServiceImpl implements MovieService{

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private FileService fileService;

    @Value("${base.url}")
    private String baseUrl;

    // Injection du chemin du répertoire des images à partir du fichier application.properties
    @Value("${movie.images.directory}")
    private String imageDirectory;

    // Chemin où les fichiers seront stockés
    private Path filePath;

    /*@Autowired
    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }*/

    @Override
    public List<MovieDto> getAllMovies() {
        // Récupérer tous les movies de la DB
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();
        // Utérer sur la liste, générer un imageUrl pour chaque objet movie
        // et le mapper à l'objet MovieDto pour la réponse
        for (Movie movie: movies){
            String imageUrl = baseUrl + "/file/" + movie.getImageName();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getImageName(),
                    imageUrl,
                    movie.getCreatedAt(),
                    movie.getUpdatedAt()
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDto getDetailsMovie(Long id) {
        // Vérification de l'id du movie dans la base de données
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id " + id) );

        // Générer l'url de l'image pour la propriété imageUrl de l'objet movieDto
        String imageUrl = baseUrl + "/file/" + movie.getImageName();

        // Faire un map de l'objet movie à l'objet movieDto pour la réponse
        MovieDto movieDtoToReturn = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getImageName(),
                imageUrl,
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
        return movieDtoToReturn;
    }

    @Override
    public Boolean deleteMovie(Long id) throws IOException {
        // Vérification de l'existence de l'objet movie dans la DB
        Optional<Movie> movie = movieRepository.findById(id);
        /*Movie  = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + id) );*/

        if (movie.isPresent()){
            Movie deleteMovie = movie.get();
            // On supprime le fichier image stocké associée à cet objet movie
            fileService.deleteImage(deleteMovie.getImageName());
            // Suppression de l'objet movie
            movieRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MovieDto createMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // On stocke l'image en appelant FileService
        // Vérification si l'image existe
        filePath = Paths.get(imageDirectory);
        if (Files.exists(filePath.resolve(Objects.requireNonNull(file.getOriginalFilename())))){
            throw new FileExistsException("Le fichier existe déjà, veuillez fournir un nouveau!");
        }
        String uploadedFileName = fileService.uploadFile(file);

        // On modifie la valeur de la propriété imageName
        movieDto.setImageName(uploadedFileName);

        // On fait le map de l'objet MovieDto à l'oblet Movie
        Movie movie = new Movie(
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getImageName()
        );


        // On enregistre l'objet Movie dans la DB
        Movie savedMovie = movieRepository.save(movie);

        // On génère l'url de l'image pour la propriété imageUrl de l'objet movieDto
        String imageUrl = baseUrl + "/file/" + uploadedFileName;

        // On fait le map de l'objet movie à l'objet movieDto pour la réponse
        MovieDto movieToReturn = new MovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getImageName(),
                imageUrl,
                savedMovie.getCreatedAt(),
                savedMovie.getUpdatedAt()
        );

        return movieToReturn;
    }

    @Override
    public MovieDto updateMovie(Long id, MovieDto movieDto, MultipartFile file) throws IOException {
        // On vérifie si l'objet movie existe dans la DB
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id " + id) );

        // Si le paramètre file est null, l'image n'est pas fourni donc on ne fait rien
        // Si file n'est pas null, on supprime l'ancienne image associé au movie
        // et on stocke le nouveau fichier
        if(file != null && !file.isEmpty()){
            try {
                if (existingMovie.getImageName() != null){
                    fileService.deleteImage(existingMovie.getImageName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fileName = fileService.uploadFile(file);

            // On modifie la valeur de la propriété imageName par le nom du nouveau fichier fourni
            movieDto.setImageName(fileName);
        }

        // On map le dto à l'objet movie
        String title = movieDto.getTitle();
        if (title != null){
            existingMovie.setTitle(title);
        }
        String director = movieDto.getDirector();
        if (director != null){
            existingMovie.setDirector(director);
        }
        String studio = movieDto.getStudio();
        if (studio != null){
            existingMovie.setStudio(studio);
        }
        Set<String> movieCast = movieDto.getMovieCast();
        if (movieCast != null) {
            existingMovie.setMovieCast(movieCast);
        }
        Integer releaseYear = movieDto.getReleaseYear();
        if (releaseYear != null) {
            existingMovie.setReleaseYear(releaseYear);
        }
        String imageName = movieDto.getImageName();
        if (imageName != null) {
            existingMovie.setImageName(imageName);
        }

        // On fait le persist du movie modifié
        Movie updatedMovie = movieRepository.save(existingMovie);

        // On génère le imageUrl
        String imageUrl = baseUrl + "/file/" + updatedMovie.getImageName();

        // On fait le map de l'objet movie à l'objet movieDto et le retourne pour la réponse
        MovieDto movieDtoToReturn = new MovieDto(
                updatedMovie.getId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getImageName(),
                imageUrl,
                updatedMovie.getCreatedAt(),
                updatedMovie.getUpdatedAt()
        );

        return movieDtoToReturn;
    }
}
