package com.github.mrchcat.slot_booking_demo.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.github.mrchcat.slot_booking_demo.domen.AvailabilityRule;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RuleRepositoryImpl {
    private final JdbcTemplate jdbc;

    public List<AvailabilityRule> getAvailabilityRulesForDay(int day) {
        String query = """
                SELECT id, weekday, lower(range) AS l, upper(range) AS u
                FROM availability_rules
                WHERE weekday=?
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(query, day);
        if(rows.isEmpty()){
            return Collections.emptyList();
        }
        List<AvailabilityRule> rules = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            AvailabilityRule rule = new AvailabilityRule(
                    (long) row.get("id"),
                    (int) row.get("weekday"),
                    ((Time) row.get("l")).toLocalTime(),
                    ((Time) row.get("u")).toLocalTime()
            );
            rules.add(rule);
        }
        return rules;
    }
}
