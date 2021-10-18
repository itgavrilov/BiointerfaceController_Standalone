CREATE TABLE patientRecord (
id INTEGER PRIMARY KEY NOT NULL,
secondName VARCHAR(35)  NOT NULL,
firstName VARCHAR(35)  NOT NULL,
middleName VARCHAR(35)  NULL,
birthday DATE  NOT NULL,
icd_id INTEGER NULL,
comment TEXT  NULL,
FOREIGN KEY (icd_id) REFERENCES icd (id)
);

CREATE TABLE icd (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
icd VARCHAR(35) NOT NULL,
version INTEGER NOT NULL,
comment TEXT NULL
);

CREATE TABLE device (
id INTEGER PRIMARY KEY NOT NULL,
amountChannels INTEGER NOT NULL,
comment TEXT NULL
);

CREATE TABLE examination (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
dateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
patientRecord_id INTEGER NOT NULL,
device_id INTEGER NOT NULL,
comment TEXT NULL,
FOREIGN KEY (patientRecord_id) REFERENCES patientRecord(id) ON DELETE CASCADE,
FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE
);

CREATE TABLE channel (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name VARCHAR(35) UNIQUE NOT NULL,
comment TEXT NULL
);

CREATE TABLE graph (
numberOfChannel INTEGER NOT NULL,
examination_id INTEGER NOT NULL,
channel_id INTEGER  NULL,
PRIMARY KEY (numberOfChannel, examination_id)
FOREIGN KEY (examination_id) REFERENCES examination(id) ON DELETE CASCADE,
FOREIGN KEY (channel_id) REFERENCES channel(id)
);

CREATE TABLE sample (
id INTEGER NOT NULL,
numberOfChannel INTEGER NOT NULL,
examination_id INTEGER NOT NULL,
value INTEGER NOT NULL,
PRIMARY KEY (id, numberOfChannel, examination_id),
FOREIGN KEY (numberOfChannel, examination_id) REFERENCES graph(numberOfChannel, examination_id) ON DELETE CASCADE
);