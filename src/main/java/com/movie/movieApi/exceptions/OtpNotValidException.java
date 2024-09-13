package com.movie.movieApi.exceptions;

public class OtpNotValidException extends RuntimeException {
    public OtpNotValidException(String message) {
        super(message);
    }
}
