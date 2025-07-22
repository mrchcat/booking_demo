package com.github.mrchcat.slot_booking_demo.repository;

import org.springframework.data.repository.CrudRepository;
import com.github.mrchcat.slot_booking_demo.domen.Booking;

public interface BookingRepository extends CrudRepository<Booking, Long> {
}
