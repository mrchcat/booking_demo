package slot_booking_demo.repository;

import org.springframework.data.repository.CrudRepository;
import slot_booking_demo.domen.Event;

public interface EventRepository extends CrudRepository<Event, Long> {
}
