DROP DATABASE IF EXISTS publictransport_17573;
CREATE DATABASE publictransport_17573;
\c publictransport_17573

BEGIN TRANSACTION;

CREATE TYPE VehicleTypes AS ENUM('city_bus', 'bus', 'train', 'funicular');

CREATE TABLE City (
    name VARCHAR(60) PRIMARY KEY,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL
);

CREATE TABLE Company (
    name VARCHAR(60) PRIMARY KEY,
    headquarterCity VARCHAR(60) NOT NULL
);

CREATE TABLE Line (
    shortCode VARCHAR(5) NOT NULL,
    departingCity VARCHAR(60) NOT NULL,
    color CHAR(6) NOT NULL,
    vehicleType VehicleTypes NOT NULL,
    description VARCHAR(500) NOT NULL,

    PRIMARY KEY(shortCode, departingCity)
);

CREATE TABLE Trip (
    code INT PRIMARY KEY,
    calendar INT NOT NULL,
    company VARCHAR(60) NOT NULL,
    lineCode VARCHAR(5) NOT NULL,
    lineCity VARCHAR(60) NOT NULL
);

CREATE TABLE Calendar (
    ID SERIAL PRIMARY KEY,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    monday BOOLEAN NOT NULL DEFAULT False,
    tuesday BOOLEAN NOT NULL DEFAULT False,
    wednesday BOOLEAN NOT NULL DEFAULT False,
    thursday BOOLEAN NOT NULL DEFAULT False,
    friday BOOLEAN NOT NULL DEFAULT False,
    saturday BOOLEAN NOT NULL DEFAULT False,
    sunday BOOLEAN NOT NULL DEFAULT False,

    CHECK(endDate > startDate)
);

CREATE TABLE Stop (
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    city VARCHAR(60) NOT NULL,
    platformName VARCHAR(5) DEFAULT NULL,

    PRIMARY KEY(latitude, longitude)
);

CREATE TABLE StopsAt (
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    trip INT NOT NULL,
    "order" SERIAL NOT NULL,
    arrivalTime TIME DEFAULT NULL,
    departureTime TIME DEFAULT NULL,

    CHECK(("order"=1 AND arrivalTime IS NULL AND departureTime IS NOT NULL) OR ("order"<>1 AND ((departureTime IS NULL AND arrivalTime IS NOT NULL) OR (departureTime IS NOT NULL AND arrivalTime IS NOT NULL AND departureTime >= arrivalTime)))),

    PRIMARY KEY(latitude, longitude, trip)
);

CREATE TABLE StopHasName (
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    language CHAR(3) NOT NULL,
    "value" VARCHAR(40) NOT NULL,

    PRIMARY KEY(latitude, longitude, language)
);

CREATE TABLE HasExceptionOn (
    date DATE NOT NULL,
    calendar INT NOT NULL,
    suppressed BOOLEAN NOT NULL,

    PRIMARY KEY(date, calendar)
);


ALTER TABLE Company
    ADD FOREIGN KEY(headquarterCity) REFERENCES City(name);

ALTER TABLE Line
     ADD FOREIGN KEY(departingCity) REFERENCES City(name);

ALTER TABLE Trip
    ADD FOREIGN KEY(calendar) REFERENCES Calendar(ID),
    ADD FOREIGN KEY(lineCode, lineCity) REFERENCES Line(shortCode, departingCity),
    ADD FOREIGN KEY(company) REFERENCES Company(name);

ALTER TABLE Stop
    ADD FOREIGN KEY(city) REFERENCES City(name);

ALTER TABLE StopsAt
    ADD FOREIGN KEY (latitude,longitude) REFERENCES Stop(latitude,longitude),
    ADD FOREIGN KEY(trip) REFERENCES Trip(code);

ALTER TABLE StopHasName
    ADD FOREIGN KEY(latitude,longitude) REFERENCES Stop(latitude,longitude);

ALTER TABLE HasExceptionOn
    ADD FOREIGN KEY(calendar) REFERENCES Calendar(ID);



CREATE OR REPLACE FUNCTION CalendarIncludeTripFun() RETURNS trigger AS $$
  DECLARE r RECORD;
  BEGIN
    SELECT calendar INTO r FROM Trip WHERE (calendar=NEW.ID);
    IF NOT found THEN RAISE 'No trip includes this calendar';
    ELSE RETURN NEW;
    END IF;
  END;
$$ LANGUAGE plpgsql;
CREATE CONSTRAINT TRIGGER CalendarIncludeTrip
  AFTER INSERT OR UPDATE ON Calendar DEFERRABLE INITIALLY DEFERRED
  FOR EACH ROW EXECUTE PROCEDURE CalendarIncludeTripFun();

CREATE OR REPLACE FUNCTION StopIncludeHasNameFun() RETURNS trigger AS $$
  DECLARE r RECORD;
  BEGIN
    SELECT latitude,longitude INTO r FROM StopHasName WHERE (latitude=NEW.latitude AND longitude=NEW.longitude);
    IF NOT found THEN RAISE 'No translation found for the current stop';
    ELSE RETURN NEW;
    END IF;
  END;
$$ LANGUAGE plpgsql;
CREATE CONSTRAINT TRIGGER StopIncludeHasName
  AFTER INSERT OR UPDATE ON Stop DEFERRABLE INITIALLY DEFERRED
  FOR EACH ROW EXECUTE PROCEDURE StopIncludeHasNameFun();

CREATE OR REPLACE FUNCTION TripIncludeStopsAtFun() RETURNS trigger AS $$
  DECLARE r INT;
  BEGIN
    SELECT COUNT(*) INTO r FROM StopsAt WHERE trip=NEW.code;
    IF r<2 THEN RAISE 'StopsAt constraint violated: not enough stops for the newely-insterted trip';
    ELSE RETURN NEW;
    END IF;
  END;
$$ LANGUAGE plpgsql;
CREATE CONSTRAINT TRIGGER TripIncludeStopsAt
  AFTER INSERT OR UPDATE ON Trip DEFERRABLE INITIALLY DEFERRED
  FOR EACH ROW EXECUTE PROCEDURE TripIncludeStopsAtFun();

COMMIT;