CREATE TABLE IF NOT EXISTS espiritu (
    id SERIAL PRIMARY KEY,
    tipo Varchar(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    nivelConexion Int NOT NULL
);