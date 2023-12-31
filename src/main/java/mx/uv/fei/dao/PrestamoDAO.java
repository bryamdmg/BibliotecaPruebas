package main.java.mx.uv.fei.dao;

import main.java.mx.uv.fei.dataaccess.DatabaseManager;
import main.java.mx.uv.fei.logic.Libro;
import main.java.mx.uv.fei.logic.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.sql.ResultSet;

public class PrestamoDAO implements IPrestamo {
    @Override
    public void prestamoLibro(int libroId, Date fechaPrestamo) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        try {
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false);

            String verificarDisponibilidadSQL = "SELECT estado FROM Libros WHERE id = ?";
            String estadoLibro;
            try (PreparedStatement verificarDisponibilidadStmt = connection.prepareStatement(verificarDisponibilidadSQL)) {
                verificarDisponibilidadStmt.setInt(1, libroId);
                try (ResultSet resultSet = verificarDisponibilidadStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Libro no encontrado.");
                    }
                    estadoLibro = resultSet.getString("estado");
                }
            }

            if (!estadoLibro.equals("disponible")) {
                throw new SQLException("El libro no está disponible para préstamo.");
            }

            String estado;
            Date fechaDevolucion;

            String restringidoSQL = "SELECT restringido FROM Libros WHERE id = ?";
            try (PreparedStatement restringidoStmt = connection.prepareStatement(restringidoSQL)) {
                restringidoStmt.setInt(1, libroId);
                try (ResultSet resultSet = restringidoStmt.executeQuery()) {
                    if (resultSet.next()) {
                        boolean restringido = resultSet.getBoolean("restringido");

                        if (restringido) {
                            estado = "retraso";
                            fechaDevolucion = fechaPrestamo;
                        } else {
                            estado = "prestado";
                            fechaDevolucion = new Date(fechaPrestamo.getTime() + (3 * 24 * 60 * 60 * 1000));
                        }
                    } else {
                        throw new SQLException("Libro no encontrado.");
                    }
                }
            }

            String insertPrestamoSQL = "INSERT INTO Prestamos (libro_id, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?)";
            try (PreparedStatement insertPrestamoStmt = connection.prepareStatement(insertPrestamoSQL)) {
                insertPrestamoStmt.setInt(1, libroId);
                insertPrestamoStmt.setDate(2, new java.sql.Date(fechaPrestamo.getTime()));
                insertPrestamoStmt.setDate(3, new java.sql.Date(fechaDevolucion.getTime()));
                insertPrestamoStmt.executeUpdate();
            }

            String actualizarEstadoLibroSQL = "UPDATE Libros SET estado = ? WHERE id = ?";
            try (PreparedStatement actualizarEstadoLibroStmt = connection.prepareStatement(actualizarEstadoLibroSQL)) {
                actualizarEstadoLibroStmt.setString(1, estado);
                actualizarEstadoLibroStmt.setInt(2, libroId);
                actualizarEstadoLibroStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public void recepcionLibro(int libroId) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        try {
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false);

            String verificarEstadoSQL = "SELECT estado FROM Libros WHERE id = ?";
            String estado;
            Date fechaDevolucion;
            try (PreparedStatement verificarEstadoStmt = connection.prepareStatement(verificarEstadoSQL)) {
                verificarEstadoStmt.setInt(1, libroId);
                try (ResultSet resultSet = verificarEstadoStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Libro no encontrado.");
                    }
                    estado = resultSet.getString("estado");
                }
            }

            if (!estado.equals("prestado") && !estado.equals("retraso")) {
                throw new SQLException("El libro no está en préstamo o está en estado de pérdida.");
            }

            String obtenerFechaDevolucionSQL = "SELECT fecha_devolucion FROM Prestamos WHERE libro_id = ?";
            try (PreparedStatement obtenerFechaDevolucionStmt = connection.prepareStatement(obtenerFechaDevolucionSQL)) {
                obtenerFechaDevolucionStmt.setInt(1, libroId);
                try (ResultSet resultSet = obtenerFechaDevolucionStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Información de préstamo no encontrada.");
                    }
                    fechaDevolucion = resultSet.getDate("fecha_devolucion");
                }
            }

            String actualizarEstadoLibroSQL = "UPDATE Libros SET estado = 'disponible' WHERE id = ?";
            try (PreparedStatement actualizarEstadoLibroStmt = connection.prepareStatement(actualizarEstadoLibroSQL)) {
                actualizarEstadoLibroStmt.setInt(1, libroId);
                actualizarEstadoLibroStmt.executeUpdate();
            }

            String eliminarPrestamoSQL = "DELETE FROM Prestamos WHERE libro_id = ?";
            try (PreparedStatement eliminarPrestamoStmt = connection.prepareStatement(eliminarPrestamoSQL)) {
                eliminarPrestamoStmt.setInt(1, libroId);
                eliminarPrestamoStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public void ingresarFechaDevolucion(int libroId, Date nuevaFechaDevolucion) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        try {
            connection.setAutoCommit(false);

            String verificarEstadoSQL = "SELECT estado FROM Libros WHERE id = ?";
            String estado;
            try (PreparedStatement verificarEstadoStmt = connection.prepareStatement(verificarEstadoSQL)) {
                verificarEstadoStmt.setInt(1, libroId);
                try (ResultSet resultSet = verificarEstadoStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Libro no encontrado.");
                    }
                    estado = resultSet.getString("estado");
                }
            }

            if (!estado.equals("prestado")) {
                throw new SQLException("El libro no está en estado de préstamo.");
            }

            String actualizarFechaDevolucionSQL = "UPDATE Prestamos SET fecha_devolucion = ? WHERE libro_id = ?";
            try (PreparedStatement actualizarFechaDevolucionStmt = connection.prepareStatement(actualizarFechaDevolucionSQL)) {
                actualizarFechaDevolucionStmt.setDate(1, new java.sql.Date(nuevaFechaDevolucion.getTime()));
                actualizarFechaDevolucionStmt.setInt(2, libroId);
                actualizarFechaDevolucionStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public Prestamo obtenerPrestamo(int id) throws SQLException {
        Prestamo prestamo = new Prestamo();
        String sql = "SELECT id, libro_id, fecha_prestamo, fecha_devolucion FROM prestamos WHERE id = ?";
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            if (resultSet.next()) {
                prestamo.setId(resultSet.getInt("id"));
                prestamo.setLibro_id(resultSet.getInt("libro_id"));
                prestamo.setFecha_prestamo(resultSet.getDate("fecha_prestamo"));
                prestamo.setFecha_devolucion(resultSet.getDate("fecha_devolucion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamo;
    }
}

