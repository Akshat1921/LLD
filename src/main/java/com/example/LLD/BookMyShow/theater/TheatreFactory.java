package com.example.LLD.BookMyShow.theater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.LLD.BookMyShow.enums.City;

public class TheatreFactory {
    public static Theatre createTheatre(int theatreId, String name, City city, List<Show> shows){
        Theatre theatre = new Theatre();
        theatre.setTheatreId(theatreId);
        theatre.setTheatreName(name);
        theatre.setScreens(createScreens());
        theatre.setCity(city);
        theatre.setShows(shows);
        return theatre;
    }

    public static List<Screen> createScreens(){
        Screen screen = new Screen();
        screen.setScreenId(1);
        screen.setSeats(createSeats());
        return Arrays.asList(screen);
    }

    public static List<Seat> createSeats(){
        List<Seat> seats = new ArrayList<>();
        for(int i=0; i<=100; i++){
            seats.add(new Seat());
        }
        return seats;
    }

}
