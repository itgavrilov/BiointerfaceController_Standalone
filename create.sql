CREATE TABLE icd (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name VARCHAR(35) NOT NULL,
version INTEGER NOT NULL,
comment TEXT
);

CREATE TABLE device (
id INTEGER PRIMARY KEY NOT NULL,
amountChannels INTEGER NOT NULL,
comment TEXT
);

CREATE TABLE channelName (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
name VARCHAR(35) UNIQUE NOT NULL,
comment TEXT
);

CREATE TABLE patientRecord (
id INTEGER PRIMARY KEY NOT NULL,
secondName VARCHAR(35) NOT NULL,
firstName VARCHAR(35) NOT NULL,
middleName VARCHAR(35),
birthday DATE  NOT NULL,
icd_id INTEGER,
comment TEXT,
FOREIGN KEY (icd_id) REFERENCES icd(id)
);

CREATE TABLE examination (
id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
startTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
patientRecord_id INTEGER NOT NULL,
device_id INTEGER NOT NULL,
comment TEXT NULL,
FOREIGN KEY (patientRecord_id) REFERENCES patientRecord(id) ON DELETE CASCADE,
FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE
);

CREATE TABLE channel (
number INTEGER NOT NULL,
examination_id INTEGER NOT NULL,
channelName_id INTEGER,
PRIMARY KEY (number, examination_id),
FOREIGN KEY (examination_id) REFERENCES examination(id) ON DELETE CASCADE,
FOREIGN KEY (channelName_id) REFERENCES channelName(id)
);

CREATE TABLE sample (
id INTEGER NOT NULL,
number INTEGER NOT NULL,
examination_id INTEGER NOT NULL,
value INTEGER NOT NULL,
PRIMARY KEY (id, number, examination_id),
FOREIGN KEY (number, examination_id) REFERENCES channel(number, examination_id) ON DELETE CASCADE
);