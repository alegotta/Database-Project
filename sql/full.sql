--
-- PostgreSQL database dump
--

-- Dumped from database version 13.1
-- Dumped by pg_dump version 13.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: vehicletypes; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.vehicletypes AS ENUM (
    'city_bus',
    'bus',
    'train',
    'funicular'
);


ALTER TYPE public.vehicletypes OWNER TO postgres;

--
-- Name: calendarincludetripfun(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.calendarincludetripfun() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
  DECLARE r RECORD;
  BEGIN
    SELECT calendar INTO r FROM Trip WHERE (calendar=NEW.ID);
    IF NOT found THEN RAISE 'No trip includes this calendar';
    ELSE RETURN NEW;
    END IF;
  END;
$$;


ALTER FUNCTION public.calendarincludetripfun() OWNER TO postgres;

--
-- Name: stopincludehasnamefun(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.stopincludehasnamefun() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
  DECLARE r RECORD;
  BEGIN
    SELECT latitude,longitude INTO r FROM StopHasName WHERE (latitude=NEW.latitude AND longitude=NEW.longitude);
    IF NOT found THEN RAISE 'No translation found for the current stop';
    ELSE RETURN NEW;
    END IF;
  END;
$$;


ALTER FUNCTION public.stopincludehasnamefun() OWNER TO postgres;

--
-- Name: tripincludestopsatfun(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.tripincludestopsatfun() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
  DECLARE r INT;
  BEGIN
    SELECT COUNT(*) INTO r FROM StopsAt WHERE trip=NEW.code;
    IF r<2 THEN RAISE 'StopsAt constraint violated: not enough stops for the newely-insterted trip';
    ELSE RETURN NEW;
    END IF;
  END;
$$;


ALTER FUNCTION public.tripincludestopsatfun() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: calendar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.calendar (
    id integer NOT NULL,
    startdate date NOT NULL,
    enddate date NOT NULL,
    monday boolean DEFAULT false NOT NULL,
    tuesday boolean DEFAULT false NOT NULL,
    wednesday boolean DEFAULT false NOT NULL,
    thursday boolean DEFAULT false NOT NULL,
    friday boolean DEFAULT false NOT NULL,
    saturday boolean DEFAULT false NOT NULL,
    sunday boolean DEFAULT false NOT NULL,
    CONSTRAINT calendar_check CHECK ((enddate > startdate))
);


ALTER TABLE public.calendar OWNER TO postgres;

--
-- Name: calendar_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calendar_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calendar_id_seq OWNER TO postgres;

--
-- Name: calendar_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.calendar_id_seq OWNED BY public.calendar.id;


--
-- Name: city; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.city (
    name character varying(60) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL
);


ALTER TABLE public.city OWNER TO postgres;

--
-- Name: company; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.company (
    name character varying(60) NOT NULL,
    headquartercity character varying(60) NOT NULL
);


ALTER TABLE public.company OWNER TO postgres;

--
-- Name: hasexceptionon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.hasexceptionon (
    date date NOT NULL,
    calendar integer NOT NULL,
    suppressed boolean NOT NULL
);


ALTER TABLE public.hasexceptionon OWNER TO postgres;

--
-- Name: line; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.line (
    shortcode character varying(5) NOT NULL,
    departingcity character varying(60) NOT NULL,
    color character(6) NOT NULL,
    vehicletype public.vehicletypes NOT NULL,
    description character varying(500) NOT NULL
);


ALTER TABLE public.line OWNER TO postgres;

--
-- Name: stop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stop (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    city character varying(60) NOT NULL,
    platformname character varying(5) DEFAULT NULL::character varying
);


ALTER TABLE public.stop OWNER TO postgres;

--
-- Name: stophasname; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stophasname (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    language character(3) NOT NULL,
    value character varying(40) NOT NULL
);


ALTER TABLE public.stophasname OWNER TO postgres;

--
-- Name: stopsat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stopsat (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    trip integer NOT NULL,
    "order" integer NOT NULL,
    arrivaltime time without time zone,
    departuretime time without time zone,
    CONSTRAINT stopsat_check CHECK (((("order" = 1) AND (arrivaltime IS NULL) AND (departuretime IS NOT NULL)) OR (("order" <> 1) AND (((departuretime IS NULL) AND (arrivaltime IS NOT NULL)) OR ((departuretime IS NOT NULL) AND (arrivaltime IS NOT NULL) AND (departuretime >= arrivaltime))))))
);


ALTER TABLE public.stopsat OWNER TO postgres;

--
-- Name: stopsat_order_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.stopsat_order_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stopsat_order_seq OWNER TO postgres;

--
-- Name: stopsat_order_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.stopsat_order_seq OWNED BY public.stopsat."order";


--
-- Name: trip; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trip (
    code integer NOT NULL,
    calendar integer NOT NULL,
    company character varying(60) NOT NULL,
    linecode character varying(5) NOT NULL,
    linecity character varying(60) NOT NULL
);


ALTER TABLE public.trip OWNER TO postgres;

--
-- Name: calendar id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calendar ALTER COLUMN id SET DEFAULT nextval('public.calendar_id_seq'::regclass);


--
-- Name: stopsat order; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopsat ALTER COLUMN "order" SET DEFAULT nextval('public.stopsat_order_seq'::regclass);


--
-- Data for Name: calendar; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.calendar (id, startdate, enddate, monday, tuesday, wednesday, thursday, friday, saturday, sunday) FROM stdin;
1	2021-01-01	2021-06-01	t	t	t	t	t	t	t
2	2021-01-01	2021-06-01	t	t	t	t	t	f	f
3	2021-01-01	2021-06-01	f	f	f	f	f	t	t
\.


--
-- Data for Name: city; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.city (name, latitude, longitude) FROM stdin;
City A	12.3456	23.4567
City B	34.5678	45.6789
\.


--
-- Data for Name: company; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.company (name, headquartercity) FROM stdin;
Bus Company	City A
Train Company	City B
\.


--
-- Data for Name: hasexceptionon; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.hasexceptionon (date, calendar, suppressed) FROM stdin;
2021-01-01	2	t
2021-01-01	3	f
2021-01-06	2	t
2021-01-06	3	f
\.


--
-- Data for Name: line; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.line (shortcode, departingcity, color, vehicletype, description) FROM stdin;
10	City B	green 	bus	Local bus of City A
100	City A	blue  	train	Regional trains between City A and B
\.


--
-- Data for Name: stop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.stop (latitude, longitude, city, platformname) FROM stdin;
12.3456	23.4567	City A	\N
12.3356	23.4467	City A	\N
12.3336	23.4447	City A	\N
12.3457	23.4568	City A	1
12.3459	23.4567	City A	2
34.5678	45.6789	City B	\N
\.


--
-- Data for Name: stophasname; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.stophasname (latitude, longitude, language, value) FROM stdin;
12.3456	23.4567	it 	Stazione Centrale
12.3456	23.4567	de 	Hauptbahnof
12.3457	23.4568	it 	Stazione Centrale - Binario 1
12.3457	23.4568	de 	Hauptbahnof - Gleis 1
12.3459	23.4567	it 	Stazione Centrale - Binario 2
12.3459	23.4567	de 	Hauptbahnof - Gleis 2
34.5678	45.6789	it 	Stazione Centrale
34.5678	45.6789	de 	Hauptbahnof
12.3356	23.4467	it 	Centro Città
12.3356	23.4467	de 	Stadtmitte
12.3336	23.4447	it 	Ospedale
12.3336	23.4447	de 	Krankenhaus
\.


--
-- Data for Name: stopsat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.stopsat (latitude, longitude, trip, "order", arrivaltime, departuretime) FROM stdin;
12.3456	23.4567	12345	1	\N	12:00:00
34.5678	45.6789	12345	2	12:15:00	\N
34.5678	45.6789	54321	1	\N	12:20:00
12.3456	23.4567	54321	2	12:35:00	\N
34.5678	45.6789	55321	1	\N	12:30:00
12.3456	23.4567	55321	2	12:45:00	\N
12.3456	23.4567	123	1	\N	12:03:00
12.3356	23.4467	123	2	12:08:00	12:10:00
12.3336	23.4447	123	3	12:13:00	\N
12.3336	23.4447	321	1	\N	12:17:00
12.3356	23.4467	321	2	12:22:00	12:24:00
12.3456	23.4567	321	3	12:30:00	\N
\.


--
-- Data for Name: trip; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trip (code, calendar, company, linecode, linecity) FROM stdin;
123	2	Bus Company	10	City B
321	2	Bus Company	10	City B
12345	1	Train Company	100	City A
54321	2	Train Company	100	City A
55321	3	Train Company	100	City A
\.


--
-- Name: calendar_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.calendar_id_seq', 1, false);


--
-- Name: stopsat_order_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.stopsat_order_seq', 1, false);


--
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (id);


--
-- Name: city city_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (name);


--
-- Name: company company_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.company
    ADD CONSTRAINT company_pkey PRIMARY KEY (name);


--
-- Name: hasexceptionon hasexceptionon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hasexceptionon
    ADD CONSTRAINT hasexceptionon_pkey PRIMARY KEY (date, calendar);


--
-- Name: line line_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (shortcode, departingcity);


--
-- Name: stop stop_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop
    ADD CONSTRAINT stop_pkey PRIMARY KEY (latitude, longitude);


--
-- Name: stophasname stophasname_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stophasname
    ADD CONSTRAINT stophasname_pkey PRIMARY KEY (latitude, longitude, language);


--
-- Name: stopsat stopsat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopsat
    ADD CONSTRAINT stopsat_pkey PRIMARY KEY (latitude, longitude, trip);


--
-- Name: trip trip_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT trip_pkey PRIMARY KEY (code);


--
-- Name: calendar calendarincludetrip; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE CONSTRAINT TRIGGER calendarincludetrip AFTER INSERT OR UPDATE ON public.calendar DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION public.calendarincludetripfun();


--
-- Name: stop stopincludehasname; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE CONSTRAINT TRIGGER stopincludehasname AFTER INSERT OR UPDATE ON public.stop DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION public.stopincludehasnamefun();


--
-- Name: trip tripincludestopsat; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE CONSTRAINT TRIGGER tripincludestopsat AFTER INSERT OR UPDATE ON public.trip DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION public.tripincludestopsatfun();


--
-- Name: company company_headquartercity_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.company
    ADD CONSTRAINT company_headquartercity_fkey FOREIGN KEY (headquartercity) REFERENCES public.city(name);


--
-- Name: hasexceptionon hasexceptionon_calendar_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hasexceptionon
    ADD CONSTRAINT hasexceptionon_calendar_fkey FOREIGN KEY (calendar) REFERENCES public.calendar(id);


--
-- Name: line line_departingcity_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line
    ADD CONSTRAINT line_departingcity_fkey FOREIGN KEY (departingcity) REFERENCES public.city(name);


--
-- Name: stop stop_city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop
    ADD CONSTRAINT stop_city_fkey FOREIGN KEY (city) REFERENCES public.city(name);


--
-- Name: stophasname stophasname_latitude_longitude_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stophasname
    ADD CONSTRAINT stophasname_latitude_longitude_fkey FOREIGN KEY (latitude, longitude) REFERENCES public.stop(latitude, longitude);


--
-- Name: stopsat stopsat_latitude_longitude_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopsat
    ADD CONSTRAINT stopsat_latitude_longitude_fkey FOREIGN KEY (latitude, longitude) REFERENCES public.stop(latitude, longitude);


--
-- Name: stopsat stopsat_trip_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopsat
    ADD CONSTRAINT stopsat_trip_fkey FOREIGN KEY (trip) REFERENCES public.trip(code);


--
-- Name: trip trip_calendar_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT trip_calendar_fkey FOREIGN KEY (calendar) REFERENCES public.calendar(id);


--
-- Name: trip trip_company_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT trip_company_fkey FOREIGN KEY (company) REFERENCES public.company(name);


--
-- Name: trip trip_linecode_linecity_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT trip_linecode_linecity_fkey FOREIGN KEY (linecode, linecity) REFERENCES public.line(shortcode, departingcity);


--
-- PostgreSQL database dump complete
--

