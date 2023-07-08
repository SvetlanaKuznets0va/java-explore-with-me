CREATE TABLE IF NOT EXISTS users
(
    id    INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(254) UNIQUE                  NOT NULL,
    name  VARCHAR(20)                          NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);