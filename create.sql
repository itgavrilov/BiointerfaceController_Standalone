CREATE TABLE icd
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name    VARCHAR(35)                       NOT NULL,
    version INTEGER                           NOT NULL,
    comment TEXT
);

CREATE TABLE device
(
    id              INTEGER PRIMARY KEY NOT NULL,
    amount_channels INTEGER             NOT NULL,
    comment         TEXT
);

CREATE TABLE channel_name
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name    VARCHAR(35) UNIQUE                NOT NULL,
    comment TEXT
);

CREATE TABLE patient
(
    id          INTEGER PRIMARY KEY NOT NULL,
    second_name VARCHAR(20)         NOT NULL,
    first_name  VARCHAR(20)         NOT NULL,
    patronymic  VARCHAR(20)         NOT NULL,
    birthday    DATE                NOT NULL,
    icd_id      INTEGER,
    comment     TEXT,
    FOREIGN KEY (icd_id) REFERENCES icd (id)
);

CREATE TABLE examination
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    starttime  TIMESTAMP                         NOT NULL,
    patient_id INTEGER                           NOT NULL,
    device_id  INTEGER                           NOT NULL,
    comment    TEXT                              NULL,
    FOREIGN KEY (patient_id) REFERENCES patient (id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES device (id) ON DELETE CASCADE
);

CREATE TABLE channel
(
    number          INTEGER NOT NULL,
    examination_id  INTEGER NOT NULL,
    channel_name_id INTEGER,
    PRIMARY KEY (number, examination_id),
    FOREIGN KEY (examination_id) REFERENCES examination (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_name_id) REFERENCES channel_name (id)
);

CREATE TABLE sample
(
    id             INTEGER NOT NULL,
    channel_number INTEGER NOT NULL,
    examination_id INTEGER NOT NULL,
    value          INTEGER NOT NULL,
    PRIMARY KEY (id, channel_number, examination_id),
    FOREIGN KEY (channel_number, examination_id) REFERENCES channel (number, examination_id) ON DELETE CASCADE
);