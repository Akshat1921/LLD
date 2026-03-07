package com.example.LLD.BookMyShow.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private int movieId;
    private String movieName;
    private int movieDuration;
}
