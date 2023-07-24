INSERT INTO users VALUES (1, 'masha@mail.ru', 'Маша Петрова') ON CONFLICT DO NOTHING;
INSERT INTO locations VALUES (1, 48.8611, -44.3287) ON CONFLICT DO NOTHING;
INSERT INTO categories VALUES (1, 'Producer67') ON CONFLICT DO NOTHING;
INSERT INTO events VALUES (1,
                           'Hic quidem hic minima',
                           current_date,
                           'Praesentium nihil necessitatibus.',
                           current_date + interval '5 day',
                           true,
                           10,
                           current_date + interval '5 minutes',
                           true,
                           'PUBLISHED',
                           'Quos minus dolor',
                           1,
                           1,
                           1)
ON CONFLICT DO NOTHING;