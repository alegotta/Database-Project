BEGIN TRANSACTION;

INSERT INTO City(Name,Latitude,Longitude) VALUES ('City A', 12.3456, 23.4567), ('City B', 34.5678, 45.6789);

INSERT INTO Company(Name,HeadquarterCity) VALUES ('Bus Company', 'City A'), ('Train Company', 'City B');

INSERT INTO Line(ShortCode,DepartingCity,Color,VehicleType,Description) VALUES ('10', 'City B', 'green', 'bus', 'Local bus of City A'), ('100', 'City A', 'blue', 'train', 'Regional trains between City A and B');

INSERT INTO Stop(latitude, longitude,city,platformName) VALUES (12.3456, 23.4567, 'City A',NULL),(12.3356, 23.4467, 'City A',NULL),(12.3336, 23.4447, 'City A',NULL),(12.3457, 23.4568, 'City A','1'),(12.3459, 23.4567, 'City A','2'),(34.5678, 45.6789, 'City B',NULL);

INSERT INTO StopHasName(latitude, longitude, language, "value") VALUES (12.3456, 23.4567, 'it', 'Stazione Centrale'),(12.3456, 23.4567, 'de', 'Hauptbahnof'), (12.3457, 23.4568, 'it', 'Stazione Centrale - Binario 1'),(12.3457, 23.4568, 'de', 'Hauptbahnof - Gleis 1'), (12.3459, 23.4567, 'it', 'Stazione Centrale - Binario 2'),(12.3459, 23.4567, 'de', 'Hauptbahnof - Gleis 2'), (34.5678, 45.6789, 'it', 'Stazione Centrale'),(34.5678, 45.6789, 'de', 'Hauptbahnof'), (12.3356, 23.4467, 'it', 'Centro Città'),(12.3356, 23.4467, 'de', 'Stadtmitte'), (12.3336, 23.4447, 'it', 'Ospedale'),(12.3336, 23.4447, 'de', 'Krankenhaus');

INSERT INTO Calendar(ID,startDate,endDate,monday,tuesday,wednesday,thursday,friday,saturday,sunday) VALUES (1,'2021-01-01','2021-06-01',True,True,True,True,True,True,True),(2,'2021-01-01','2021-06-01',True,True,True,True,True,False,False), (3,'2021-01-01','2021-06-01',False,False,False,False,False,True,True);

INSERT INTO HasExceptionOn(calendar,date,suppressed) VALUES (2, '2021-01-01', True), (3, '2021-01-01', False), (2, '2021-01-06', True), (3, '2021-01-06', False);

INSERT INTO Trip(code,calendar,lineCode,lineCity,company) VALUES (123,2,'10','City B','Bus Company'), (321,2,'10','City B','Bus Company'), (12345,1,'100','City A','Train Company'), (54321,2,'100','City A','Train Company'), (55321,3,'100','City A','Train Company');

INSERT INTO StopsAt(latitude, longitude, trip, "order", arrivalTime, departureTime) VALUES (12.3456,23.4567,12345,1,NULL,'12:00'), (34.5678,45.6789,12345,2,'12:15',NULL), (34.5678,45.6789,54321,1,NULL,'12:20'), (12.3456,23.4567,54321,2,'12:35',NULL), (34.5678,45.6789,55321,1,NULL,'12:30'), (12.3456,23.4567,55321,2,'12:45',NULL), (12.3456,23.4567,123,1,NULL,'12:03'),(12.3356, 23.4467,123,2,'12:08','12:10'), (12.3336, 23.4447,123,3,'12:13',NULL), (12.3336, 23.4447,321,1,NULL,'12:17'),(12.3356, 23.4467,321,2,'12:22','12:24'), (12.3456,23.4567,321,3,'12:30',NULL);

COMMIT;
