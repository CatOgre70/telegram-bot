-- liquibase formatted sql

-- changeset vasilydemin:1
CREATE TABLE notification_task(
    id serial primary key,
    chat_id integer,
    notification text,
    date_time timestamp
);
CREATE INDEX date_time_index ON notification_task (date_time);

-- changeset vasilydemin:2
ALTER TABLE notification_task ADD column sent bool default false;

-- changeset vasilydemin:3
CREATE TABLE answers(
    question text primary key,
    answer text
);
CREATE INDEX question_index ON answers (question);
INSERT INTO answers (question, answer) VALUES ('/start', E'Привет! Это самый тупой бот на свете! Автор - Василий Демин\n/help - подсказка');
INSERT INTO answers (question, answer) VALUES ('/help', E'Использование:\n/start - начало работы с ботом\n/help - вызов подсказки\ndd.MM.yy HH:mm <Текст напоминания> - создать напоминание');
