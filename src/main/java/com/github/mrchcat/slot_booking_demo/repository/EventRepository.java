package com.github.mrchcat.slot_booking_demo.repository;

import org.springframework.data.repository.CrudRepository;
import com.github.mrchcat.slot_booking_demo.domen.Event;

public interface EventRepository extends CrudRepository<Event, Long> {
}
