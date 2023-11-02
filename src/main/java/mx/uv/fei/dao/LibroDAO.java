package main.java.mx.uv.fei.dao;

import main.java.mx.uv.fei.dataaccess.DatabaseManager;
import main.java.mx.uv.fei.logic.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class LibroDAO implements ILibro{
    @Override
    public void altaLibro(Libro libro) throws SQLException {
        String sql = "INSERT INTO Libros (titulo, restringido, estado) VALUES (?, ?, 'disponible')";
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, libro.getTitulo());
            preparedStatement.setBoolean(2, libro.isRestringido());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Libro consultaLibro(int id) throws SQLException {
        String sql = "SELECT id, titulo, restringido, estado FROM Libros WHERE id = ?";
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Libro libro = new Libro();
        try {
            if (resultSet.next()) {
                libro.setId(resultSet.getInt("id"));
                libro.setTitulo(resultSet.getString("titulo"));
                libro.setRestringido(resultSet.getBoolean("restringido"));
                libro.setEstado(resultSet.getString("estado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libro;
    }
}
