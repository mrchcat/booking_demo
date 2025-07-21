package slot_booking_demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import slot_booking_demo.domen.AvailableTime;
import slot_booking_demo.domen.Booking;
import slot_booking_demo.domen.Event;
import slot_booking_demo.dto.BookingRequest;
import slot_booking_demo.repository.AvailableTimeRepositoryImpl;
import slot_booking_demo.repository.BookingRepository;
import slot_booking_demo.repository.EventRepository;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class GeneralController {
    private final AvailableTimeRepositoryImpl availableTimeRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    //добавить событие
    @PostMapping("/event")
    @ResponseStatus(HttpStatus.CREATED)
    void createEvent(@RequestBody Event event) {
        eventRepository.save(event);
    }

    //получить доступное время по конкретному событию
    @GetMapping("/available/{eventId}")
    List<AvailableTime> getAvailableTime(@PathVariable("eventId") long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("no event id=" + eventId));
        return availableTimeRepository.getAvailableTimeForEvent(event);
    }


    @PostMapping("/book")
    @Transactional
    Booking bookEvent(@RequestBody BookingRequest bookingRequest) {
        long eventId = bookingRequest.eventId();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("no event id=" + eventId));
        if (!availableTimeRepository.isAvailableTime(bookingRequest, event)) {
            throw new NoSuchElementException("no available time for " + bookingRequest);
        }
        Booking booking = availableTimeRepository.makeTimeForBooking(bookingRequest, event);
        bookingRepository.save(booking);
        return booking;
    }
}
