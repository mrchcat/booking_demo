package slot_booking_demo.domen;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AvailableTime {
    long id;
    LocalDateTime start;
    LocalDateTime end;
}
