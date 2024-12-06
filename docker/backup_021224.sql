--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.4

-- Started on 2024-12-02 19:12:08

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
-- TOC entry 260 (class 1255 OID 25436)
-- Name: add_to_master_good(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.add_to_master_good() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    product_id integer;
BEGIN
    -- Проходим по массиву productid
    FOREACH product_id IN ARRAY NEW.productid
    LOOP
        -- Вставляем запись в таблицу master_good
        INSERT INTO master_good (master_id_of_employee, good_id_of_good, materials, techtask)
        VALUES (NEW.id_of_master, product_id, 
                (SELECT material FROM item WHERE id = product_id), 
                'Default task'); -- Здесь можно установить любое значение для техзадания
    END LOOP;
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.add_to_master_good() OWNER TO postgres;

--
-- TOC entry 244 (class 1255 OID 25278)
-- Name: after_good_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.after_good_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM public.master_good WHERE good_id_of_good = OLD.id;
    RETURN NULL;
END;
$$;


ALTER FUNCTION public.after_good_delete() OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 25434)
-- Name: assign_courier_and_manager(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.assign_courier_and_manager() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    random_courier integer;
    random_manager integer;
BEGIN
    -- Выбираем случайного курьера
    SELECT id_of_emplyee
    INTO random_courier
    FROM courier
    ORDER BY random()
    LIMIT 1;

    -- Выбираем случайного менеджера
    SELECT id_of_employee
    INTO random_manager
    FROM manager
    ORDER BY random()
    LIMIT 1;

    -- Заполняем поля id_of_courier и id_of_manager
    NEW.id_of_courier := random_courier;
    NEW.id_of_manager := random_manager;

    -- Возвращаем новую строку для вставки
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.assign_courier_and_manager() OWNER TO postgres;

--
-- TOC entry 261 (class 1255 OID 25431)
-- Name: assign_master(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.assign_master(complexity_level integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE 
    selected_master integer;
BEGIN
    -- Выбираем мастера с минимальным уровнем опыта, соответствующим уровню сложности заказа
    SELECT id_of_employee 
    INTO selected_master
    FROM master
    WHERE exp >= complexity_level*3
    ORDER BY exp
    LIMIT 1;
    
    -- Если подходящий мастер найден, возвращаем его ID
    IF selected_master IS NOT NULL THEN
        RETURN selected_master;
    ELSE
        -- Если мастера не удалось найти, возвращаем NULL (или можно обрабатывать по-другому)
        RETURN NULL;
    END IF;
END;
$$;


ALTER FUNCTION public.assign_master(complexity_level integer) OWNER TO postgres;

--
-- TOC entry 243 (class 1255 OID 25272)
-- Name: count_clients_with_multiple_orders(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.count_clients_with_multiple_orders() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE 
    total_clients integer;
BEGIN
    SELECT COUNT(DISTINCT order_table.user_id) INTO total_clients
    FROM public.order_table
    JOIN public.order_goods ON order_table.id_of_order = order_goods.id_of_order
    GROUP BY order_table.user_id
    HAVING COUNT(order_goods.id_of_good) > 1;
 
    RETURN total_clients;
END;
$$;


ALTER FUNCTION public.count_clients_with_multiple_orders() OWNER TO postgres;

--
-- TOC entry 241 (class 1255 OID 25270)
-- Name: count_materials_for_master(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.count_materials_for_master(master_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE 
    total_materials integer;
BEGIN
    SELECT COUNT(*) INTO total_materials
    FROM public.master_good
    JOIN public."materials" m ON master_good.good_id_of_good = m.id
    WHERE master_good.master_id_of_employee = master_id;

    RETURN total_materials;
END;
$$;


ALTER FUNCTION public.count_materials_for_master(master_id integer) OWNER TO postgres;

--
-- TOC entry 245 (class 1255 OID 25268)
-- Name: get_client_orders(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_client_orders(client_id integer) RETURNS TABLE(id_of_order integer, name_of_client character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT o.id_of_order, c.name
    FROM public.order_table o
    JOIN public.customers c ON c.id = o.user_id
    WHERE o.user_id = id;
END;
$$;


ALTER FUNCTION public.get_client_orders(client_id integer) OWNER TO postgres;

--
-- TOC entry 242 (class 1255 OID 25276)
-- Name: log_before_order_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_before_order_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO order_deletion_log (id_of_order, deleted_at)
    VALUES (OLD.id_of_order, NOW());

    RETURN OLD;
END;
$$;


ALTER FUNCTION public.log_before_order_delete() OWNER TO postgres;

--
-- TOC entry 237 (class 1255 OID 25233)
-- Name: lvl_of_complexity(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.lvl_of_complexity(integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
DECLARE res INTEGER;prc INTEGER;
BEGIN

SELECT price INTO prc FROM item WHERE id = $1;

IF prc < 501 THEN
SELECT 1 INTO res;
ELSE IF prc BETWEEN 501 AND 5000 THEN
SELECT 2 INTO res;
ELSE SELECT 3 INTO res;
END IF; END IF;
RETURN res;
END;
$_$;


ALTER FUNCTION public.lvl_of_complexity(integer) OWNER TO postgres;

--
-- TOC entry 240 (class 1255 OID 25262)
-- Name: order_mm(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.order_mm() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    random_id INTEGER;
BEGIN
    -- Если id_of_manager равен NULL, выбираем случайный id из таблицы manager
    IF NEW.id_of_manager IS NULL THEN
        SELECT id_of_employee
        INTO random_id
        FROM manager
        ORDER BY RANDOM()
        LIMIT 1;
        
        -- Присваиваем случайный id_of_employee в поле id_of_manager
        NEW.id_of_manager = random_id;
    END IF;
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.order_mm() OWNER TO postgres;

--
-- TOC entry 258 (class 1255 OID 25432)
-- Name: set_master_on_order_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.set_master_on_order_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    complexity_level integer;
    product_id integer;
BEGIN
    -- Перебираем все продукты в массиве productid
    FOREACH product_id IN ARRAY NEW.productid
    LOOP
        -- Определяем уровень сложности для текущего продукта
        complexity_level := lvl_of_complexity(product_id);

        -- Назначаем мастера в зависимости от сложности заказа
        NEW.id_of_master := assign_master(complexity_level);

        -- Как только мастер назначен, выходим из цикла
        EXIT WHEN NEW.id_of_master IS NOT NULL;
    END LOOP;

    -- Возвращаем новую строку для вставки
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.set_master_on_order_insert() OWNER TO postgres;

--
-- TOC entry 246 (class 1255 OID 25251)
-- Name: summ_of_goods(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.summ_of_goods() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE total_prc integer; prc integer;

DECLARE good_cursor CURSOR FOR SELECT price FROM "item";
BEGIN
total_prc:=0;
OPEN good_cursor;

LOOP

FETCH good_cursor INTO prc;
IF NOT FOUND THEN EXIT;END IF;
total_prc = total_prc + prc;

END LOOP;

CLOSE good_cursor;

RETURN total_prc;

END; 

$$;


ALTER FUNCTION public.summ_of_goods() OWNER TO postgres;

--
-- TOC entry 238 (class 1255 OID 25241)
-- Name: trigger_results_insert(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.trigger_results_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
DECLARE experience INTEGER;
BEGIN
SELECT exp INTO experience FROM master WHERE id_of_employee = NEW."master_id_of_employee";

IF Lvl_of_complexity(NEW."good_id_of_good")*3>experience
THEN
-- генерация исключительной ситуации
RAISE EXCEPTION 'Слишком маленький опыт';
END IF;
RETURN NEW;
END;
$$;


ALTER FUNCTION public.trigger_results_insert() OWNER TO postgres;

--
-- TOC entry 239 (class 1255 OID 25243)
-- Name: trigger_results_insert_after(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.trigger_results_insert_after() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ 
DECLARE prc integer;
BEGIN

SELECT price INTO prc FROM item WHERE id = NEW."id_of_good";

IF EXISTS(SELECT * FROM "order_table" WHERE "id_of_order"=NEW."id_of_order") THEN
UPDATE "order_table" SET "total_price"="total_price"+prc
WHERE "id_of_order"=NEW."id_of_order";

END IF;
RETURN NEW;
END;
$$;


ALTER FUNCTION public.trigger_results_insert_after() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 25117)
-- Name: courier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.courier (
    id_of_emplyee integer NOT NULL
);


ALTER TABLE public.courier OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 25077)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    id integer NOT NULL,
    email character varying(100),
    name character varying(100),
    phone character varying(100),
    address character varying(100),
    price integer
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 25362)
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.customers_id_seq OWNER TO postgres;

--
-- TOC entry 4939 (class 0 OID 0)
-- Dependencies: 232
-- Name: customers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customers_id_seq OWNED BY public.customers.id;


--
-- TOC entry 223 (class 1259 OID 25112)
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employee (
    id_of_employee integer NOT NULL,
    name character varying(20) NOT NULL,
    email character varying(20),
    number character varying(15),
    CONSTRAINT "Employee_number_check" CHECK (((number)::text ~ similar_to_escape('(8|8)[9][0-9][0-9]-[0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'::text)))
);


ALTER TABLE public.employee OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 25441)
-- Name: individual_order_table; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.individual_order_table (
    id_of_order integer NOT NULL,
    id_of_master integer,
    orderdate timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    user_id integer NOT NULL,
    requirements text,
    budget integer,
    color character varying(255),
    materials character varying(255),
    deadline timestamp(6) without time zone
);


ALTER TABLE public.individual_order_table OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 25440)
-- Name: individual_order_table_id_of_order_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.individual_order_table_id_of_order_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.individual_order_table_id_of_order_seq OWNER TO postgres;

--
-- TOC entry 4940 (class 0 OID 0)
-- Dependencies: 233
-- Name: individual_order_table_id_of_order_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.individual_order_table_id_of_order_seq OWNED BY public.individual_order_table.id_of_order;


--
-- TOC entry 219 (class 1259 OID 25092)
-- Name: item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    size character varying(255) NOT NULL,
    color character varying(20) NOT NULL,
    material character varying(20) NOT NULL,
    price integer NOT NULL,
    quantity integer NOT NULL,
    image character varying(255)
);


ALTER TABLE public.item OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 25346)
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.item_id_seq OWNER TO postgres;

--
-- TOC entry 4941 (class 0 OID 0)
-- Dependencies: 230
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_id_seq OWNED BY public.item.id;


--
-- TOC entry 215 (class 1259 OID 24897)
-- Name: manager; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.manager (
    id_of_employee integer NOT NULL
);


ALTER TABLE public.manager OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 25087)
-- Name: master; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.master (
    id_of_employee integer NOT NULL,
    exp integer NOT NULL,
    name_of_master character varying(20)
);


ALTER TABLE public.master OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 25097)
-- Name: master_good; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.master_good (
    master_id_of_employee integer NOT NULL,
    good_id_of_good integer NOT NULL,
    materials character varying(18) NOT NULL,
    techtask character varying(20) NOT NULL
);


ALTER TABLE public.master_good OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 25102)
-- Name: materials; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.materials (
    id integer NOT NULL,
    id_of_provider integer NOT NULL,
    type character varying(20) NOT NULL,
    quantity integer NOT NULL,
    id_of_employee integer NOT NULL
);


ALTER TABLE public.materials OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 25304)
-- Name: myuser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.myuser (
    id integer NOT NULL,
    email character varying(255),
    name character varying(255),
    password character varying(255),
    role character varying(255)
);


ALTER TABLE public.myuser OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 25344)
-- Name: myuser_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.myuser_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.myuser_id_seq OWNER TO postgres;

--
-- TOC entry 4942 (class 0 OID 0)
-- Dependencies: 229
-- Name: myuser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.myuser_id_seq OWNED BY public.myuser.id;


--
-- TOC entry 225 (class 1259 OID 25273)
-- Name: order_deletion_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_deletion_log (
    id_of_order integer,
    deleted_at timestamp without time zone
);


ALTER TABLE public.order_deletion_log OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 25456)
-- Name: order_files; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_files (
    id bigint NOT NULL,
    file_name character varying(255) NOT NULL,
    order_id integer NOT NULL
);


ALTER TABLE public.order_files OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 25455)
-- Name: order_files_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_files_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_files_id_seq OWNER TO postgres;

--
-- TOC entry 4943 (class 0 OID 0)
-- Dependencies: 235
-- Name: order_files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_files_id_seq OWNED BY public.order_files.id;


--
-- TOC entry 217 (class 1259 OID 25082)
-- Name: order_table; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_table (
    id_of_order integer NOT NULL,
    id_of_master integer NOT NULL,
    id_of_courier integer NOT NULL,
    id_of_manager integer NOT NULL,
    total_price integer,
    orderdate timestamp(6) without time zone,
    productid integer[],
    user_id integer
);


ALTER TABLE public.order_table OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 25354)
-- Name: order_table_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_table_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_table_id_seq OWNER TO postgres;

--
-- TOC entry 4944 (class 0 OID 0)
-- Dependencies: 231
-- Name: order_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_table_id_seq OWNED BY public.order_table.id_of_order;


--
-- TOC entry 228 (class 1259 OID 25330)
-- Name: persistentsession; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.persistentsession (
    id integer NOT NULL,
    last_used timestamp(6) without time zone,
    series character varying(255),
    token character varying(255),
    username character varying(255)
);


ALTER TABLE public.persistentsession OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 25329)
-- Name: persistentsession_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.persistentsession_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.persistentsession_id_seq OWNER TO postgres;

--
-- TOC entry 4945 (class 0 OID 0)
-- Dependencies: 227
-- Name: persistentsession_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.persistentsession_id_seq OWNED BY public.persistentsession.id;


--
-- TOC entry 222 (class 1259 OID 25107)
-- Name: provider; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.provider (
    id_of_provider integer NOT NULL,
    name character varying(20) NOT NULL,
    email character varying(20),
    number integer NOT NULL
);


ALTER TABLE public.provider OWNER TO postgres;

--
-- TOC entry 4710 (class 2604 OID 25363)
-- Name: customers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers ALTER COLUMN id SET DEFAULT nextval('public.customers_id_seq'::regclass);


--
-- TOC entry 4715 (class 2604 OID 25444)
-- Name: individual_order_table id_of_order; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.individual_order_table ALTER COLUMN id_of_order SET DEFAULT nextval('public.individual_order_table_id_of_order_seq'::regclass);


--
-- TOC entry 4712 (class 2604 OID 25347)
-- Name: item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item ALTER COLUMN id SET DEFAULT nextval('public.item_id_seq'::regclass);


--
-- TOC entry 4713 (class 2604 OID 25345)
-- Name: myuser id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.myuser ALTER COLUMN id SET DEFAULT nextval('public.myuser_id_seq'::regclass);


--
-- TOC entry 4717 (class 2604 OID 25475)
-- Name: order_files id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files ALTER COLUMN id SET DEFAULT nextval('public.order_files_id_seq'::regclass);


--
-- TOC entry 4711 (class 2604 OID 25355)
-- Name: order_table id_of_order; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table ALTER COLUMN id_of_order SET DEFAULT nextval('public.order_table_id_seq'::regclass);


--
-- TOC entry 4714 (class 2604 OID 25333)
-- Name: persistentsession id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.persistentsession ALTER COLUMN id SET DEFAULT nextval('public.persistentsession_id_seq'::regclass);


--
-- TOC entry 4921 (class 0 OID 25117)
-- Dependencies: 224
-- Data for Name: courier; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.courier (id_of_emplyee) FROM stdin;
3
0
\.


--
-- TOC entry 4913 (class 0 OID 25077)
-- Dependencies: 216
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customers (id, email, name, phone, address, price) FROM stdin;
2	asdasd@gmail.com	qwdqwdqwd	213123123	asdadasd	5600
3	qwdasd@gmail.com	asdasd	123213	adasdasd	5600
4	qwdasd@gmail.com	asdasd	123213	adasdasd	5600
5	qwdasd@gmail.com	asdasd	123213	adasdasd	5600
6	ywhebcw@gmail.com	FIO	88888883333	adddddadda	3300
7	ffff@gmail.com	qqqq	9999999	asjhdajsd	6400
8	gdbfuhasbcuh@gmail.com	ффффф	11213	asdasdasdasdasd	900
9	gdbfuhasbcuh@gmail.com	ффффф	11213	asdasdasdasdasd	900
10	gdbfuhasbcuh@gmail.com	asdasdasd	1112123213	asdasdasdasdasd	900
11	gdbfuhasbcuh@gmail.com	asdasdasd	1112123213	asdasdasdasdasd	900
12	qqq@gmail.com	asdasdasd	123123123	qqq	900
13	qqq@gmail.com	asdasda	123123123	asdasdasd	3200
14	dfsadaa@gmail.com	Zack	123456789	msk dom 2	13900
15	dfsadaa@gmail.com	Zack	123456789	msk dom 2	13900
16	dfsadaa@gmail.com	Zack	123456789	msk dom 2	13900
17	test@gmail.com	test	111	\N	0
18	test@gmail.com	test	11	\N	0
19	qqq@gmail.com	qqqasd	1	\N	0
20	qqq@gmail.com	Boba	89340327218	Moscow 1 2 3	0
21	s@gmail.com	asd	1	sdsd	10200
22	s@gmail.com	asd	1	sdsd	7500
23	w@gmail.com	w	2	w	4000
24	qqq@gmail.com	s	1	sa	0
25	sds@gmail.com	s	12	ss	0
26	qqq@gmail.com	fio	12	s	0
27	bnebnebn@gmail.com	Белов Никита Евгеньевич	893011234546	Москва, Красная площадь	0
\.


--
-- TOC entry 4920 (class 0 OID 25112)
-- Dependencies: 223
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employee (id_of_employee, name, email, number) FROM stdin;
1	Иван Иванов	ivanov@mail.ru	8900-111-22-33
2	Анна Смирнова	anna_smirno@mail.ru	8900-222-22-44
3	Олег Кузнецов	olekuz@mail.ru	8923-772-23-45
4	Мария Федорова	maria_fed@mail.ru	8945-666-32-41
0	Олге Бобов	bobov@gmail.com	8900-111-22-32
\.


--
-- TOC entry 4931 (class 0 OID 25441)
-- Dependencies: 234
-- Data for Name: individual_order_table; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.individual_order_table (id_of_order, id_of_master, orderdate, user_id, requirements, budget, color, materials, deadline) FROM stdin;
2	0	2024-11-18 20:19:23.801	19	qqq@gmail.com	1	qqq@gmail.com	qqq@gmail.com	2024-11-20 00:00:00
3	0	2024-11-18 20:25:12.031	20	Огромный медведь	33000	Коричневый	хлопок, плюш	2024-12-01 00:00:00
4	0	2024-11-19 00:46:40.178	24	asd	33	s	s	2024-11-01 00:00:00
5	0	2024-11-19 00:57:24.269	25	details	777	colore	materials	2024-11-30 00:00:00
6	0	2024-11-19 00:58:59.719	26	qqq@gmail.com	2	qqq@gmail.com	qqq@gmail.com	2024-11-28 00:00:00
7	0	2024-12-02 14:03:15.169	27	Подарок на день рождения	5000	Розовый	Плюш	2024-12-08 00:00:00
\.


--
-- TOC entry 4916 (class 0 OID 25092)
-- Dependencies: 219
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.item (id, name, size, color, material, price, quantity, image) FROM stdin;
1	Заяц	10	Красный	Плюш	3200	2	d0yi_IoDvmY.jpg
2	Брошь-лиса	14	Orange	Bisser	1500	5	4g9IqY-NVNQ.jpg
3	Мухоморы	10	Red	Ткань	900	3	mushrooms.jpg
4	Красный_медведь	50	Red	Плюш	4000	1	bear_read2.jpg
5	Броши_Осенние_листья	15	Green_Orange	bisser	1200	4	list.jpg
\.


--
-- TOC entry 4912 (class 0 OID 24897)
-- Dependencies: 215
-- Data for Name: manager; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.manager (id_of_employee) FROM stdin;
1
0
\.


--
-- TOC entry 4915 (class 0 OID 25087)
-- Dependencies: 218
-- Data for Name: master; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.master (id_of_employee, exp, name_of_master) FROM stdin;
2	5	Анна Смирнова
1	1	Иван Иванов
0	111	Олге Бобов
\.


--
-- TOC entry 4917 (class 0 OID 25097)
-- Dependencies: 220
-- Data for Name: master_good; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.master_good (master_id_of_employee, good_id_of_good, materials, techtask) FROM stdin;
0	1	Плюш	Default task
0	2	Bisser	Default task
0	4	Плюш	Default task
\.


--
-- TOC entry 4918 (class 0 OID 25102)
-- Dependencies: 221
-- Data for Name: materials; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.materials (id, id_of_provider, type, quantity, id_of_employee) FROM stdin;
1	1	хлопок	100	2
2	2	шерсть	50	2
\.


--
-- TOC entry 4923 (class 0 OID 25304)
-- Dependencies: 226
-- Data for Name: myuser; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.myuser (id, email, name, password, role) FROM stdin;
9	new@gmail.com	new	$2a$10$BQGqNOIuFOKp5MinnS1KAui.Z7WWvRgECBT6lZqxAmtZXXUoPC5ce	ROLE_USER
10	qqq@gmail.com	qqq	$2a$10$gZQDQ0GBc5hm7EHqk7jhQeh8Si8Sv.Wkcehd6EOynI6MxwblRVN9a	ROLE_USER
11	sadasd@gmail.com	asdasd	$2a$10$c6240oqKnjGu6C/HJeDLj.OjazrnhWRTl8kguwaxGsy2.ACpUhNc2	ROLE_USER
777	n.e.beloff2004@gmail.com	admin	$2a$10$gZQDQ0GBc5hm7EHqk7jhQeh8Si8Sv.Wkcehd6EOynI6MxwblRVN9a	ROLE_ADMIN
12	ggg@gmail.com		$2a$10$JXNA9iJCT0Y9LzzElkt.qOcVgGHjS7whSNWDMBv51RWfzMWl9vvq6	ROLE_USER
\.


--
-- TOC entry 4922 (class 0 OID 25273)
-- Dependencies: 225
-- Data for Name: order_deletion_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_deletion_log (id_of_order, deleted_at) FROM stdin;
5	2024-09-28 10:47:44.170705
2	2024-09-28 10:47:44.170705
\.


--
-- TOC entry 4933 (class 0 OID 25456)
-- Dependencies: 236
-- Data for Name: order_files; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_files (id, file_name, order_id) FROM stdin;
2	d2793fa0-41ce-4491-a29b-a33109b09c38_Тиньк анкета1.png	2
3	91207233-ec68-4fc9-944f-83c903117cd5_Тиньк анкета2.png	2
4	d2976aaf-928c-42d8-9ef1-6d6c4a288064_image-f7h4a3I-S-transformed.png	3
5	5d6e89e6-48b0-4479-8dc3-908032ab7ea9_DALL·E 2024-10-26 13.01.39 - A BPMN (Business Process Model and Notation) diagram illustrating the preparation of Olivier salad with beef tongue. The diagram should include these .webp	5
6	f2d01f8c-60e9-464a-badc-1a152b1a07d6_image-dNhUQVgo5-transformed.png	7
7	a963ef58-0cdb-4b13-acaa-994b2b2d5c0a_image-f7h4a3I-S-transformed.png	7
\.


--
-- TOC entry 4914 (class 0 OID 25082)
-- Dependencies: 217
-- Data for Name: order_table; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_table (id_of_order, id_of_master, id_of_courier, id_of_manager, total_price, orderdate, productid, user_id) FROM stdin;
4	0	0	0	5600	2024-09-28 16:56:31.345	{2,1,3}	5
5	0	0	0	3300	2024-10-16 22:09:39.089	{2,3}	6
6	2	0	0	6400	2024-10-16 22:23:59.661	{1}	7
7	2	3	1	900	2024-10-16 22:31:23.94	{3}	12
8	2	0	0	3200	2024-10-16 22:35:28.663	{1}	13
11	0	3	0	13900	2024-10-17 12:36:22.911	{1,2}	16
14	0	3	0	4000	2024-11-19 00:44:08.217	{4}	23
\.


--
-- TOC entry 4925 (class 0 OID 25330)
-- Dependencies: 228
-- Data for Name: persistentsession; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.persistentsession (id, last_used, series, token, username) FROM stdin;
\.


--
-- TOC entry 4919 (class 0 OID 25107)
-- Dependencies: 222
-- Data for Name: provider; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.provider (id_of_provider, name, email, number) FROM stdin;
1	ООО "Текстиль"	textil@mail.ru	9998877
2	ИП "Пряжа"	pryaja@mail.ru	8887766
\.


--
-- TOC entry 4946 (class 0 OID 0)
-- Dependencies: 232
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customers_id_seq', 27, true);


--
-- TOC entry 4947 (class 0 OID 0)
-- Dependencies: 233
-- Name: individual_order_table_id_of_order_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.individual_order_table_id_of_order_seq', 7, true);


--
-- TOC entry 4948 (class 0 OID 0)
-- Dependencies: 230
-- Name: item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.item_id_seq', 9, true);


--
-- TOC entry 4949 (class 0 OID 0)
-- Dependencies: 229
-- Name: myuser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.myuser_id_seq', 12, true);


--
-- TOC entry 4950 (class 0 OID 0)
-- Dependencies: 235
-- Name: order_files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_files_id_seq', 7, true);


--
-- TOC entry 4951 (class 0 OID 0)
-- Dependencies: 231
-- Name: order_table_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_table_id_seq', 14, true);


--
-- TOC entry 4952 (class 0 OID 0)
-- Dependencies: 227
-- Name: persistentsession_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.persistentsession_id_seq', 1, false);


--
-- TOC entry 4738 (class 2606 OID 25121)
-- Name: courier Courier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courier
    ADD CONSTRAINT "Courier_pkey" PRIMARY KEY (id_of_emplyee);


--
-- TOC entry 4736 (class 2606 OID 25116)
-- Name: employee Employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT "Employee_pkey" PRIMARY KEY (id_of_employee);


--
-- TOC entry 4720 (class 2606 OID 24901)
-- Name: manager Manager_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.manager
    ADD CONSTRAINT "Manager_pkey" PRIMARY KEY (id_of_employee);


--
-- TOC entry 4732 (class 2606 OID 25106)
-- Name: materials Materials_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materials
    ADD CONSTRAINT "Materials_pkey" PRIMARY KEY (id);


--
-- TOC entry 4722 (class 2606 OID 25081)
-- Name: customers client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT client_pkey PRIMARY KEY (id);


--
-- TOC entry 4728 (class 2606 OID 25096)
-- Name: item good_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT good_pkey PRIMARY KEY (id);


--
-- TOC entry 4746 (class 2606 OID 25449)
-- Name: individual_order_table individual_order_table_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.individual_order_table
    ADD CONSTRAINT individual_order_table_pkey PRIMARY KEY (id_of_order);


--
-- TOC entry 4730 (class 2606 OID 25101)
-- Name: master_good master_good_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.master_good
    ADD CONSTRAINT master_good_pkey PRIMARY KEY (master_id_of_employee, good_id_of_good);


--
-- TOC entry 4726 (class 2606 OID 25091)
-- Name: master master_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.master
    ADD CONSTRAINT master_pkey PRIMARY KEY (id_of_employee);


--
-- TOC entry 4740 (class 2606 OID 25310)
-- Name: myuser myuser_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.myuser
    ADD CONSTRAINT myuser_pkey PRIMARY KEY (id);


--
-- TOC entry 4748 (class 2606 OID 25477)
-- Name: order_files order_files_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files
    ADD CONSTRAINT order_files_pkey PRIMARY KEY (id);


--
-- TOC entry 4724 (class 2606 OID 25086)
-- Name: order_table orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id_of_order);


--
-- TOC entry 4744 (class 2606 OID 25337)
-- Name: persistentsession persistentsession_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.persistentsession
    ADD CONSTRAINT persistentsession_pkey PRIMARY KEY (id);


--
-- TOC entry 4734 (class 2606 OID 25111)
-- Name: provider provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.provider
    ADD CONSTRAINT provider_pkey PRIMARY KEY (id_of_provider);


--
-- TOC entry 4742 (class 2606 OID 25312)
-- Name: myuser uk_lg7nrqqfougttfwk58m9mc2d8; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.myuser
    ADD CONSTRAINT uk_lg7nrqqfougttfwk58m9mc2d8 UNIQUE (name);


--
-- TOC entry 4767 (class 2620 OID 25279)
-- Name: item after_good_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER after_good_delete AFTER DELETE ON public.item FOR EACH ROW EXECUTE FUNCTION public.after_good_delete();


--
-- TOC entry 4762 (class 2620 OID 25277)
-- Name: order_table before_order_delete; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER before_order_delete BEFORE DELETE ON public.order_table FOR EACH ROW EXECUTE FUNCTION public.log_before_order_delete();


--
-- TOC entry 4763 (class 2620 OID 25263)
-- Name: order_table order_m; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER order_m BEFORE INSERT ON public.order_table FOR EACH ROW EXECUTE FUNCTION public.order_mm();


--
-- TOC entry 4768 (class 2620 OID 25242)
-- Name: master_good tr_results_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_results_insert BEFORE INSERT ON public.master_good FOR EACH ROW EXECUTE FUNCTION public.trigger_results_insert();


--
-- TOC entry 4764 (class 2620 OID 25437)
-- Name: order_table trg_add_to_master_good; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_add_to_master_good AFTER INSERT ON public.order_table FOR EACH ROW EXECUTE FUNCTION public.add_to_master_good();


--
-- TOC entry 4765 (class 2620 OID 25435)
-- Name: order_table trg_assign_courier_and_manager; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_assign_courier_and_manager BEFORE INSERT ON public.order_table FOR EACH ROW EXECUTE FUNCTION public.assign_courier_and_manager();


--
-- TOC entry 4766 (class 2620 OID 25433)
-- Name: order_table trg_set_master; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_set_master BEFORE INSERT ON public.order_table FOR EACH ROW EXECUTE FUNCTION public.set_master_on_order_insert();


--
-- TOC entry 4759 (class 2606 OID 25192)
-- Name: courier Courier_id_of_emplyee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courier
    ADD CONSTRAINT "Courier_id_of_emplyee_fkey" FOREIGN KEY (id_of_emplyee) REFERENCES public.employee(id_of_employee) NOT VALID;


--
-- TOC entry 4749 (class 2606 OID 25122)
-- Name: manager Manager_id_of_employee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.manager
    ADD CONSTRAINT "Manager_id_of_employee_fkey" FOREIGN KEY (id_of_employee) REFERENCES public.employee(id_of_employee) NOT VALID;


--
-- TOC entry 4757 (class 2606 OID 25182)
-- Name: materials Materials_id_of_employee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materials
    ADD CONSTRAINT "Materials_id_of_employee_fkey" FOREIGN KEY (id_of_employee) REFERENCES public.master(id_of_employee) NOT VALID;


--
-- TOC entry 4758 (class 2606 OID 25187)
-- Name: materials Materials_id_of_provider_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materials
    ADD CONSTRAINT "Materials_id_of_provider_fkey" FOREIGN KEY (id_of_provider) REFERENCES public.provider(id_of_provider) NOT VALID;


--
-- TOC entry 4750 (class 2606 OID 25338)
-- Name: order_table fk9tg63wrh5d28ya2ab7jpqfta; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT fk9tg63wrh5d28ya2ab7jpqfta FOREIGN KEY (user_id) REFERENCES public.customers(id);


--
-- TOC entry 4760 (class 2606 OID 25450)
-- Name: individual_order_table fk_customer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.individual_order_table
    ADD CONSTRAINT fk_customer FOREIGN KEY (user_id) REFERENCES public.customers(id) ON DELETE CASCADE;


--
-- TOC entry 4761 (class 2606 OID 25464)
-- Name: order_files fk_order; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files
    ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES public.individual_order_table(id_of_order) ON DELETE CASCADE;


--
-- TOC entry 4755 (class 2606 OID 25280)
-- Name: master_good master_good_good_id_of_good_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.master_good
    ADD CONSTRAINT master_good_good_id_of_good_fkey FOREIGN KEY (good_id_of_good) REFERENCES public.item(id) ON DELETE CASCADE;


--
-- TOC entry 4756 (class 2606 OID 25172)
-- Name: master_good master_good_master_id_of_employee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.master_good
    ADD CONSTRAINT master_good_master_id_of_employee_fkey FOREIGN KEY (master_id_of_employee) REFERENCES public.master(id_of_employee) NOT VALID;


--
-- TOC entry 4754 (class 2606 OID 25162)
-- Name: master master_id_of_employee_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.master
    ADD CONSTRAINT master_id_of_employee_fkey FOREIGN KEY (id_of_employee) REFERENCES public.employee(id_of_employee) NOT VALID;


--
-- TOC entry 4751 (class 2606 OID 25137)
-- Name: order_table orders_id_of_courier_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT orders_id_of_courier_fkey FOREIGN KEY (id_of_courier) REFERENCES public.courier(id_of_emplyee) NOT VALID;


--
-- TOC entry 4752 (class 2606 OID 25142)
-- Name: order_table orders_id_of_manager_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT orders_id_of_manager_fkey FOREIGN KEY (id_of_manager) REFERENCES public.manager(id_of_employee) NOT VALID;


--
-- TOC entry 4753 (class 2606 OID 25132)
-- Name: order_table orders_id_of_master_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT orders_id_of_master_fkey FOREIGN KEY (id_of_master) REFERENCES public.master(id_of_employee) NOT VALID;


-- Completed on 2024-12-02 19:12:09

--
-- PostgreSQL database dump complete
--

