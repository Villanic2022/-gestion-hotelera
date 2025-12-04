-- ===========================================
--  ESQUEMA INICIAL SISTEMA GESTIÓN HOTELERA
-- ===========================================

-- ===== CLIENTE =====
CREATE TABLE public.cliente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE
);

-- ===== CANAL =====
CREATE TABLE public.canal (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT canal_codigo_key UNIQUE (codigo)
);

-- ===== HOTEL =====
CREATE TABLE public.hotel (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    pais VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    cliente_id INT,
    CONSTRAINT fk_hotel_cliente FOREIGN KEY (cliente_id) REFERENCES public.cliente(id)
);

-- ===== HUESPED =====
CREATE TABLE public.huesped (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    telefono VARCHAR(50),
    pais VARCHAR(100),
    tipo_documento VARCHAR(50),
    numero_documento VARCHAR(50),
    notas TEXT,
    creado_en TIMESTAMP DEFAULT now(),
    cliente_id INT,
    CONSTRAINT fk_huesped_cliente FOREIGN KEY (cliente_id) REFERENCES public.cliente(id)
);

-- ===== ROL =====
CREATE TABLE public.rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT rol_nombre_key UNIQUE (nombre)
);

-- ===== USUARIO =====
CREATE TABLE public.usuario (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    email VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE,
    cliente_id INT,
    CONSTRAINT usuario_usuario_key UNIQUE (usuario),
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES public.cliente(id)
);

-- ===== TIPO_HABITACION =====
CREATE TABLE public.tipo_habitacion (
    id SERIAL PRIMARY KEY,
    hotel_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    capacidad_base INT,
    capacidad_max INT,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT tipo_habitacion_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(id)
);

-- ===== USUARIO_ROL =====
CREATE TABLE public.usuario_rol (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    CONSTRAINT usuario_rol_pkey PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT usuario_rol_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuario(id),
    CONSTRAINT usuario_rol_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.rol(id)
);

-- ===== CANAL_TIPO_HABITACION =====
CREATE TABLE public.canal_tipo_habitacion (
    id SERIAL PRIMARY KEY,
    hotel_id INT NOT NULL,
    canal_id INT NOT NULL,
    tipo_habitacion_id INT NOT NULL,
    codigo_externo VARCHAR(100),
    rateplan_externo VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT canal_tipo_habitacion_canal_id_fkey FOREIGN KEY (canal_id) REFERENCES public.canal(id),
    CONSTRAINT canal_tipo_habitacion_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(id),
    CONSTRAINT canal_tipo_habitacion_tipo_habitacion_id_fkey FOREIGN KEY (tipo_habitacion_id) REFERENCES public.tipo_habitacion(id)
);

-- ===== HABITACION =====
CREATE TABLE public.habitacion (
    id SERIAL PRIMARY KEY,
    hotel_id INT NOT NULL,
    tipo_habitacion_id INT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    piso VARCHAR(50),
    estado VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT habitacion_hotel_id_codigo_key UNIQUE (hotel_id, codigo),
    CONSTRAINT habitacion_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(id),
    CONSTRAINT habitacion_tipo_habitacion_id_fkey FOREIGN KEY (tipo_habitacion_id) REFERENCES public.tipo_habitacion(id)
);

-- ===== RESERVA =====
CREATE TABLE public.reserva (
    id SERIAL PRIMARY KEY,
    hotel_id INT NOT NULL,
    tipo_habitacion_id INT NOT NULL,
    habitacion_id INT,
    canal VARCHAR(50),
    id_externo VARCHAR(100),
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    adultos INT,
    ninos INT,
    estado VARCHAR(50),         -- EstadoReserva (PENDIENTE, CONFIRMADA, etc.)
    precio_total NUMERIC(10,2),
    moneda VARCHAR(10),
    comentarios_cliente TEXT,
    comentarios_internos TEXT,
    creado_en TIMESTAMP DEFAULT now(),
    actualizado_en TIMESTAMP DEFAULT now(),
    estado_pago VARCHAR(20),    -- EstadoPago (PENDIENTE, SENIADO, PAGADO)
    CONSTRAINT reserva_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(id),
    CONSTRAINT reserva_tipo_habitacion_id_fkey FOREIGN KEY (tipo_habitacion_id) REFERENCES public.tipo_habitacion(id),
    CONSTRAINT reserva_habitacion_id_fkey FOREIGN KEY (habitacion_id) REFERENCES public.habitacion(id)
);

CREATE INDEX idx_reserva_id_externo ON public.reserva USING btree (id_externo);

-- ===== RESERVA_HUESPED =====
CREATE TABLE public.reserva_huesped (
    reserva_id INT NOT NULL,
    huesped_id INT NOT NULL,
    es_titular BOOLEAN,
    CONSTRAINT reserva_huesped_pkey PRIMARY KEY (reserva_id, huesped_id),
    CONSTRAINT reserva_huesped_reserva_id_fkey FOREIGN KEY (reserva_id) REFERENCES public.reserva(id),
    CONSTRAINT reserva_huesped_huesped_id_fkey FOREIGN KEY (huesped_id) REFERENCES public.huesped(id)
);

-- ===== PAGO =====
CREATE TABLE public.pago (
    id SERIAL PRIMARY KEY,
    reserva_id INT NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    moneda VARCHAR(10),
    metodo VARCHAR(50),
    pagado_por_canal BOOLEAN,
    referencia_pago VARCHAR(100),
    fecha_pago TIMESTAMP DEFAULT now(),
    CONSTRAINT pago_reserva_id_fkey FOREIGN KEY (reserva_id) REFERENCES public.reserva(id)
);

-- ===== DATOS INICIALES =====
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('RECEPCION'), ('LIMPIEZA');

INSERT INTO canal (codigo, nombre) VALUES
('DIRECTO', 'Reserva Directa'),
('BOOKING', 'Booking.com'),
('AIRBNB', 'Airbnb');
