-- Crear el esquema
CREATE SCHEMA sistemaGestionViajes;

-- Crear la tabla cliente
CREATE TABLE "sistemagestionviajes"."cliente" (
    cliente_id BIGSERIAL PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(100)
);

-- Crear la tabla vehiculo
CREATE TABLE "sistemagestionviajes"."vehiculo" (
    vehiculo_id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    capacidad INT NOT NULL CHECK (capacidad > 0),
    tipo VARCHAR(50) NOT NULL
);

-- Crear la tabla viaje
CREATE TABLE "sistemagestionviajes"."viaje" (
    viaje_id BIGSERIAL PRIMARY KEY,
    vehiculo_id BIGINT NOT NULL REFERENCES "sistemagestionviajes"."vehiculo"(vehiculo_id) ON DELETE CASCADE ,
    origen VARCHAR(50) NOT NULL,
    destino VARCHAR(50) NOT NULL,
    fecha_salida TIMESTAMP NOT NULL,
    fecha_llegada TIMESTAMP NOT NULL CHECK (fecha_llegada > fecha_salida),
    precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0)
);

-- ENUM estado de la reserva
CREATE TYPE enum_estado AS ENUM ('disponible', 'en_curso', 'finalizado', 'cancelado');

-- Crear la tabla reserva
CREATE TABLE "sistemagestionviajes"."reserva" (
    reserva_id BIGSERIAL PRIMARY KEY,
    viaje_id BIGINT NOT NULL REFERENCES "sistemagestionviajes"."viaje"(viaje_id) ON DELETE CASCADE,
    cliente_id BIGINT NOT NULL REFERENCES "sistemagestionviajes"."cliente"(cliente_id) ON DELETE CASCADE,
    estado  enum_estado NOT NULL,
    fecha_reserva TIMESTAMP NOT NULL DEFAULT NOW()
);
