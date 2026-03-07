package com.example.LLD.BookMyShow.theater;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Screen {
    private int screenId;
    private List<Seat> seats;
}
