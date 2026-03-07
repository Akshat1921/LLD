package com.example.LLD.BookMyShow.controllers;

import java.util.*;

import com.example.LLD.BookMyShow.enums.City;
import com.example.LLD.BookMyShow.models.Movie;

public class MovieController {
    Map<City, List<Movie>> cityVsMovieMap;
    List<Movie> allMovies;

    public MovieController(){
        cityVsMovieMap = new HashMap<>();
        allMovies = new ArrayList<>();
    }

    public void addMovie( Movie movie, City city){
        allMovies.add(movie);

        List<Movie> movies = cityVsMovieMap.getOrDefault(city, new ArrayList<>());
        movies.add(movie);
        cityVsMovieMap.put(city, movies);
    }

    public Movie getMovieByName(String name){
        Optional<Movie> movie =  allMovies.stream().filter(e->e.getMovieName().equals(name)).findAny();
        if(movie.isPresent()){
            return movie.get();
        }else{
            System.out.println("movie not found with name : " + name);
            return null;
        }
    }

    public List<Movie> getMoviesByCity(City city){
        return cityVsMovieMap.get(city);
    }

}
