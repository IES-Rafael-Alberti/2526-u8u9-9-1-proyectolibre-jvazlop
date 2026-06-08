CREATE TABLE IF NOT EXISTS clientes (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS habitaciones (
    numero INT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    precio_noche DOUBLE NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente VARCHAR(20) NOT NULL,
    numero_habitacion INT NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente',
    pagada BOOLEAN DEFAULT FALSE,
    num_personas INT DEFAULT 1,
    segundo_huesped VARCHAR(100) DEFAULT '',
    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
);
