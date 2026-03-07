package com.example.LLD.BookMyShow.theater;

import com.example.LLD.BookMyShow.enums.SeatCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    private int seatId;
    private int row;
    private SeatCategory seatCategory;
}
