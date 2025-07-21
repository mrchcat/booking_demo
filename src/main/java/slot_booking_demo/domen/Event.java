package slot_booking_demo.domen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("event_templates")
public class Event {
    @Id
    long id;
    @Column("duration_minutes")
    int durationMinutes;
    @Column("start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime startTime;
    @Column("end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime endTime;
    @Column("buffer_before_minutes")
    int bufferBefore;
    @Column("buffer_after_minutes")
    int bufferAfter;
}
