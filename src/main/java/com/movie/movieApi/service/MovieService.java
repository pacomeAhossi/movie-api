package com.movie.movieApi.service;

import com.movie.movieApi.dto.MovieDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    public List<MovieDto> getAllMovies();

    public MovieDto getDetailsMovie(Long id);

    public Boolean deleteMovie(Long id) throws IOException;

    public MovieDto createMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    public MovieDto updateMovie(Long id, MovieDto movieDto, MultipartFile file) throws IOException;


}
