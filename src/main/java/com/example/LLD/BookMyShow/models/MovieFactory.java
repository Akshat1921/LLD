package com.example.LLD.BookMyShow.models;

import java.util.HashMap;
import java.util.Map;

public class MovieFactory {
    private static final Map<String, Movie> movieCache = new HashMap<>();

    public static Movie createMovie(int movieId, String name, int duration ){
        return movieCache.computeIfAbsent(name, k->{
            return new Movie(movieId, name, duration);
        });
    }

}
