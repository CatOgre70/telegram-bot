-- liquibase formatted sql

-- changeset vasilydemin:1
CREATE TABLE notification_task(
    id serial primary key,
    chat_id integer,
    notification text,
    date_time timestamp
);
CREATE INDEX date_time_index ON notification_task (date_time);