package com.example.LLD.BookMyShow.theater;

import java.util.List;

import com.example.LLD.BookMyShow.enums.City;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Theatre {
    private int theatreId;
    private String address;
    private String theatreName;
    private List<Screen> screens;
    private List<Show> shows;
    private City city;
}
