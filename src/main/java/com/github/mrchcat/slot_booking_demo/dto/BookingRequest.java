package com.github.mrchcat.slot_booking_demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookingRequest(long eventId,
                             @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                             LocalDateTime start) {
}
