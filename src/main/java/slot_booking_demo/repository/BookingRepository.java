package slot_booking_demo.repository;

import org.springframework.data.repository.CrudRepository;
import slot_booking_demo.domen.Booking;

public interface BookingRepository extends CrudRepository<Booking, Long> {
}
