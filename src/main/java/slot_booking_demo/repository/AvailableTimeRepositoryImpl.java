package slot_booking_demo.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import slot_booking_demo.domen.AvailableTime;
import slot_booking_demo.domen.Booking;
import slot_booking_demo.domen.Event;
import slot_booking_demo.dto.BookingRequest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AvailableTimeRepositoryImpl {
    private final JdbcTemplate jdbc;

    public void save(AvailableTime at) {
        String query = """
                INSERT INTO available_time(range)
                VALUES  (CAST(? AS TSRANGE));
                """;
        jdbc.update(query, ps -> {
            String range = String.format("[%s,%s]", at.getStart(), at.getEnd());
            ps.setString(1, range);
        });
    }

    public List<AvailableTime> getAvailableTimeForEvent(Event event) {
        String query = """
                SELECT id,lower(range2) AS l, upper(range2) AS u
                FROM (
                    SELECT id, range*CAST(? AS TSRANGE) AS range2
                    FROM available_time
                )
                WHERE range2<>'empty'
                """;
        String requiredRange = String.format("[%s,%s]", event.getStartTime(), event.getEndTime());
        List<Map<String, Object>> rows = jdbc.queryForList(query, requiredRange);
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<AvailableTime> availableTimes = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            AvailableTime time = new AvailableTime(
                    (long) row.get("id"),
                    ((Timestamp) row.get("l")).toLocalDateTime(),
                    ((Timestamp) row.get("u")).toLocalDateTime()
            );
            availableTimes.add(time);
        }
        return availableTimes;
    }

    public Booking makeTimeForBooking(BookingRequest bookingRequest, Event event) {
        LocalDateTime eventStart = bookingRequest.start();
        LocalDateTime eventFinish = eventStart.plusMinutes(event.getDurationMinutes());
        LocalDateTime bufferedEventStart = eventStart.minusMinutes(event.getBufferBefore());
        LocalDateTime bufferedEventFinish = eventFinish.plusMinutes(event.getBufferAfter());

        long availableRangeId = getAvailableRangeId(bufferedEventStart, bufferedEventFinish, event);
        AvailableTime availableRange = findById(availableRangeId);

        delete(availableRangeId);

        LocalDateTime mostLeft = availableRange.getStart();
        LocalDateTime firstRight = bufferedEventStart;
        if (mostLeft.isBefore(firstRight)) {
            AvailableTime first = AvailableTime.builder()
                    .start(mostLeft)
                    .end(firstRight)
                    .build();
            save(first);
        }
        LocalDateTime nextLeft = bufferedEventFinish;
        LocalDateTime mostRight = availableRange.getEnd();
        if (nextLeft.isBefore(mostRight)) {
            AvailableTime second = AvailableTime.builder()
                    .start(nextLeft)
                    .end(mostRight)
                    .build();
            save(second);
        }
        return Booking.builder()
                .eventId(event.getId())
                .start(eventStart)
                .end(eventFinish)
                .build();
    }

    void delete(long timeId) {
        String query = """
                DELETE FROM available_time
                WHERE id=?
                """;
        jdbc.update(query, timeId);
    }

    private AvailableTime findById(long timeId) {
        String query = """
                SELECT id, lower(range) AS l, upper(range) AS u
                FROM available_time
                WHERE id=?
                """;
        RowMapper<AvailableTime> rowMapper = (rs, rowNum) -> {
            return AvailableTime.builder()
                    .id(rs.getLong("id"))
                    .start(rs.getTimestamp("l").toLocalDateTime())
                    .end(rs.getTimestamp("u").toLocalDateTime())
                    .build();
        };
        AvailableTime range = jdbc.queryForObject(query, rowMapper, timeId);
        return range;
    }

    private Long getAvailableRangeId(LocalDateTime bufferedEventStart,
                                     LocalDateTime bufferedEventFinish,
                                     Event event) {
        String query = """
                	SELECT id
                	FROM (
                			SELECT id, range@>CAST(? AS TSRANGE) AS range
                			FROM (
                				    SELECT *
                					FROM (
                						SELECT id, range*CAST(? AS TSRANGE) AS range
                						FROM available_time
                					)
                   					WHERE range<>'empty'
                			)
                   )
                   WHERE range<>false
                """;
        String eventdRange = String.format("[%s,%s]", event.getStartTime(), event.getEndTime());
        String bookingRange = String.format("[%s,%s]", bufferedEventStart, bufferedEventFinish);
        Long id = jdbc.queryForObject(query, Long.class, bookingRange, eventdRange);
        return id;
    }

    public boolean isAvailableTime(BookingRequest bookingRequest, Event event) {
        String query = """
                SELECT EXISTS (
                	SELECT id, range
                	FROM (
                			SELECT id, range@>CAST(? AS TSRANGE) AS range
                			FROM (
                				    SELECT *
                					FROM (
                						SELECT id, range*CAST(? AS TSRANGE) AS range
                						FROM available_time
                					)
                   					WHERE range<>'empty'
                			)
                   )
                   WHERE range<>false
                )
                """;
        String eventdRange = String.format("[%s,%s]", event.getStartTime(), event.getEndTime());

        LocalDateTime eventStart = bookingRequest.start();
        LocalDateTime eventFinish = eventStart.plusMinutes(event.getDurationMinutes());
        LocalDateTime bufferedEventStart = eventStart.minusMinutes(event.getBufferBefore());
        LocalDateTime bufferedEventFinish = eventFinish.plusMinutes(event.getBufferAfter());
        String bookingRange = String.format("[%s,%s]", bufferedEventStart, bufferedEventFinish);
        return Boolean.TRUE.equals(jdbc.queryForObject(query, Boolean.class, bookingRange, eventdRange));
    }
}
