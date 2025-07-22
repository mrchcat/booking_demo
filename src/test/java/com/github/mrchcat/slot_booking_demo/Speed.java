package com.github.mrchcat.slot_booking_demo;

import com.github.mrchcat.slot_booking_demo.domen.Booking;
import com.github.mrchcat.slot_booking_demo.domen.Event;
import com.github.mrchcat.slot_booking_demo.dto.BookingRequest;
import com.github.mrchcat.slot_booking_demo.repository.AvailableTimeRepositoryImpl;
import com.github.mrchcat.slot_booking_demo.repository.BookingRepository;
import com.github.mrchcat.slot_booking_demo.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class Speed {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AvailableTimeRepositoryImpl availableTimeRepository;

    @Autowired
    private JdbcTemplate jdbc;

    private Event event1;
    private Event event2;

    private final SecureRandom rnd = new SecureRandom();

    private final long ATTEMPTS = 100;
    private final long BOOKING_SAMPLE_SIZE = 1000;
    private final int EVENTS_SAMPLE_SIZE = 2;


    @BeforeEach
    void initEvents() {
        clearDB();
        createEvents();
    }

    void clearDB() {
        String deleteEvents = """
                DELETE FROM event_templates
                """;
        jdbc.update(deleteEvents);
        String deleteBookings = """
                DELETE FROM event_templates
                """;
        jdbc.update(deleteBookings);
    }

    void createEvents() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusYears(300);
        Event event;
        event = Event.builder()
                .durationMinutes(97)
                .startTime(start)
                .endTime(end)
                .bufferBefore(0)
                .bufferAfter(0)
                .build();
        event1 = eventRepository.save(event);
        event = Event.builder()
                .durationMinutes(27)
                .startTime(start)
                .endTime(end)
                .bufferBefore(0)
                .bufferAfter(0)
                .build();
        event2 = eventRepository.save(event);
    }


    @Test
    void searchWhenNotEnoughTime() throws InterruptedException {
        Event event = event1;

        LocalDateTime now = LocalDateTime.now();
        long sumOfSpentTime = 0;
        for (int i = 0; i < ATTEMPTS; i++) {
            LocalDateTime endOfTime = searchWhenNotEnoughTime(event);
            if (endOfTime.isAfter(event.getEndTime())) {
                throw new IllegalArgumentException("событие слишком рано заканчивается. End= " + endOfTime);
            }
            Duration totalDuration = Duration.between(now, endOfTime);
            long randomDurationInSec = (long) (rnd.nextDouble(1) * totalDuration.getSeconds());
            LocalDateTime start = now.plusSeconds(randomDurationInSec);
            BookingRequest testRequest = new BookingRequest(event2.getId(), start);
            long startTime = System.currentTimeMillis();
            test(testRequest, event);
            long finishTIme = System.currentTimeMillis();

            sumOfSpentTime = sumOfSpentTime + (finishTIme - startTime);
        }
        double averageTime = (0.0 + sumOfSpentTime) / ATTEMPTS;
        System.out.println("Среднее время поиска существующей позиции=" + averageTime);
    }

    private LocalDateTime searchWhenNotEnoughTime(Event event) {
        List<Booking> bookingList = new ArrayList<>();
        long totalBookings = BOOKING_SAMPLE_SIZE * EVENTS_SAMPLE_SIZE;

        LocalDateTime time = LocalDateTime.now();
        int eventLength = event.getDurationMinutes();

        int bookings = 0;

        while (bookings < totalBookings) {
            int gapLength = (int) (eventLength * rnd.nextDouble(1) * 0.9);
            time = time.plusMinutes(gapLength);

            Event currentEvent = (rnd.nextBoolean()) ? event1 : event2;
            Booking booking = Booking.builder()
                    .eventId(currentEvent.getId())
                    .start(time)
                    .end(time.plusMinutes(currentEvent.getDurationMinutes()))
                    .build();
            bookingList.add(booking);
            bookings++;
        }
        bookingRepository.saveAll(bookingList);
        return time;
    }

    private void test(BookingRequest bookingRequest, Event event) throws InterruptedException {
        if (availableTimeRepository.isAvailableTime(bookingRequest, event)) {
            throw new IllegalArgumentException("нашел букинг, хотя был не должен. request=" + bookingRequest);
        }
    }


//    private Booking searchWhenNotEnoughTime(long eventId) {
//        List<Booking> bookingList = new ArrayList<>();
//        long bookingSampleSize = 100;
//        int eventsSampleSize = 2;
//        long totalBookings = bookingSampleSize * eventsSampleSize;
//
//        var rnd = new SecureRandom();
//
//        long chosenBookingNumber = rnd.nextLong(bookingSampleSize) + 1;
//        Booking bookingToSearch=null;
//
//        LocalDateTime time = LocalDateTime.now();
//        Duration averageGapLengthInMinutes = Duration.ofMinutes(20);
//
//        int bookings = 0;
//
//        while (bookings < totalBookings) {
//            if (rnd.nextBoolean()) {
//                time = time.plus(averageGapLengthInMinutes);
//                continue;
//            }
//            Event currentEvent = (rnd.nextBoolean()) ? event1 : event2;
//            Booking booking = Booking.builder()
//                    .eventId(currentEvent.getId())
//                    .start(time)
//                    .end(time.plusMinutes(currentEvent.getDurationMinutes()))
//                    .build();
//            bookingList.add(booking);
//            bookings++;
//            if (bookings == chosenBookingNumber) {
//                bookingToSearch = booking;
//            }
//        }
//        bookingRepository.saveAll(bookingList);
//        return bookingToSearch;
//    }


}
