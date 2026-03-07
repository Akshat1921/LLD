package com.example.LLD.BookMyShow.theater;

import java.util.List;

import com.example.LLD.BookMyShow.models.Movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Show {
    private Movie movie;
    private int showStartTime;
    private Screen screen;
    private List<Integer> bookedSeatIds;
    private int showId;

}
