package main.java.mx.uv.fei.dao;

import java.sql.SQLException;
import java.util.Date;

public interface IPrestamo {
    void prestamoLibro(int libroId, Date fechaPrestamo) throws SQLException;
    void recepcionLibro(int libroId) throws SQLException;
}
