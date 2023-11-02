package main.java.mx.uv.fei.dao;

import main.java.mx.uv.fei.logic.Libro;

import java.sql.SQLException;

public interface ILibro {
    void altaLibro(Libro libro) throws SQLException;

    Libro consultaLibro(int id) throws SQLException;
}
