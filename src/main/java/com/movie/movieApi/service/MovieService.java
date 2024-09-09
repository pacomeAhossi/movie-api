package com.movie.movieApi.service;

import com.movie.movieApi.dto.MovieDto;
import com.movie.movieApi.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    List<MovieDto> getAllMovies();

    MovieDto getDetailsMovie(Long id);

    Boolean deleteMovie(Long id) throws IOException;

    MovieDto createMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto updateMovie(Long id, MovieDto movieDto, MultipartFile file) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction);
}
