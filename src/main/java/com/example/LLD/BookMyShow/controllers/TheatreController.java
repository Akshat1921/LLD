package com.example.LLD.BookMyShow.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.LLD.BookMyShow.enums.City;
import com.example.LLD.BookMyShow.models.Movie;
import com.example.LLD.BookMyShow.theater.Show;
import com.example.LLD.BookMyShow.theater.Theatre;

public class TheatreController {
    Map<City, List<Theatre>> cityVsTheatreMap;
    List<Theatre> allTheatres;

    public TheatreController(){
        cityVsTheatreMap = new HashMap<>();
        allTheatres = new ArrayList<>();
    }

    public void addTheatre( Theatre theatre, City city){
        allTheatres.add(theatre);

        List<Theatre> Theatres = cityVsTheatreMap.getOrDefault(city, new ArrayList<>());
        Theatres.add(theatre);
        cityVsTheatreMap.put(city, Theatres);
    }

    public Map<Theatre, List<Show>> getAllShows(Movie movie, City city){
        List<Theatre> theatres = cityVsTheatreMap.get(city);
        Map<Theatre, List<Show>> res = new HashMap<>();
                for(Theatre theatre : theatres) {

            List<Show> givenMovieShows = new ArrayList<>();
            List<Show> shows = theatre.getShows();
            // shows = [morning, evening]

            for(Show show : shows) {
                if(show.getMovie().getMovieId() == movie.getMovieId()) {
                    givenMovieShows.add(show);
                }
            }
            // givenMovieShows = [morning, evening]
            if(!givenMovieShows.isEmpty()) {
                res.put(theatre, givenMovieShows);
            }
        }
        return res;
    }
}
