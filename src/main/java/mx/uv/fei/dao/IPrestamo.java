package main.java.mx.uv.fei.dao;

import main.java.mx.uv.fei.logic.Prestamo;

import java.sql.SQLException;
import java.util.Date;

public interface IPrestamo {
    void prestamoLibro(int libroId, Date fechaPrestamo) throws SQLException;
    void recepcionLibro(int libroId) throws SQLException;
    void ingresarFechaDevolucion(int libroId, Date nuevaFechaDevolucion) throws SQLException;
    Prestamo obtenerPrestamo(int id) throws SQLException;
}
