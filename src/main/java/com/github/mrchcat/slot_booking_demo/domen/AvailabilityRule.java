package com.github.mrchcat.slot_booking_demo.domen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Setter
public class AvailabilityRule {
    long id;
    int weekday;
    LocalTime start;
    LocalTime end;
}
