CREATE TABLE espiritu (

                        id SERIAL PRIMARY KEY ,
                        tipo String NOT NULL,
                        nombre String NOT NULL UNIQUE,
                        nivelConexion Int NOT NULL,
)