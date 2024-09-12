package com.movie.movieApi.exceptions;

public class UserWithUsernameNotFoundException extends RuntimeException {
    public UserWithUsernameNotFoundException(String message) {
        super(message);
    }
}
