CREATE DATABASE biblioteca;
CREATE USER 'usuarioB'@localhost IDENTIFIED BY 'usuarioB';
GRANT CREATE, DELETE, UPDATE, SELECT ON *.* TO 'usuarioB'@localhost IDENTIFIED BY 'usuarioB';
USE biblioteca;

CREATE TABLE Libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    restringido BOOLEAN NOT NULL,
    estado ENUM('disponible', 'extension', 'prestado', 'retraso', 'perdida') NOT NULL
);

CREATE TABLE Prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    libro_id INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE NOT NULL,
    FOREIGN KEY (libro_id) REFERENCES Libros(id)
);