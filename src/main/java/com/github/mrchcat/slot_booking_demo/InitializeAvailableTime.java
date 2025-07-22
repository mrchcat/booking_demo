package com.github.mrchcat.slot_booking_demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.github.mrchcat.slot_booking_demo.domen.AvailableTime;
import com.github.mrchcat.slot_booking_demo.repository.AvailableTimeRepositoryImpl;
import com.github.mrchcat.slot_booking_demo.repository.RuleRepositoryImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitializeAvailableTime implements ApplicationRunner {
    private final RuleRepositoryImpl ruleRepository;
    private final AvailableTimeRepositoryImpl availableTimeRepository;

    @Value("${horizon}")
    private int horizon;

    @Override
    public void run(ApplicationArguments args) {
        LocalDate now = LocalDate.now();
        for (int i = 0; i < horizon; i++) {
            LocalDate day = now.plusDays(i);
            int weekday = day.getDayOfWeek().getValue();
            var rules = ruleRepository.getAvailabilityRulesForDay(weekday); // и так сойдет...
            if (rules.isEmpty()) {
                continue;
            }
            rules.forEach(ir -> {
                LocalDateTime start = LocalDateTime.of(day, ir.getStart());
                LocalDateTime end = LocalDateTime.of(day, ir.getEnd());
                AvailableTime availableTime = new AvailableTime(0, start, end);
                availableTimeRepository.save(availableTime);
            });
        }
    }
}
