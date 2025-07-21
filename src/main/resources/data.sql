INSERT INTO event_templates (duration_minutes,start_time,end_time,buffer_before_minutes,buffer_after_minutes)
VALUES (43, '2025-07-19 07:00','2025-07-22 23:00',15,30);

INSERT INTO availability_rules (weekday,range)
VALUES
(1,'[09:00,13:00]'),(1,'[14:00,18:00]'),
(2,'[09:00,13:00]'),(2,'[14:00,18:00]'),
(3,'[09:00,12:00]'),(3,'[14:00,15:00]'),(3,'[16:00,19:00]'),
(4,'[09:00,14:00]'),(4,'[15:00,17:00]'),
(5,'[10:00,14:00]');
