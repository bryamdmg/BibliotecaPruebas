package main.java.mx.uv.fei.dao;

import main.java.mx.uv.fei.dataaccess.DatabaseManager;
import main.java.mx.uv.fei.logic.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    public void marcarLibroComoPerdido(int libroId) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.getConnection();

        try {

            // Paso 1: Verificar el estado del libro y obtener la fecha de devolución
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

            // Paso 2: Calcular la fecha actual
            Date fechaActual = new Date();

            // Paso 3: Verificar si el libro es perdido
            Calendar calendarFechaDevolucion = Calendar.getInstance();
            calendarFechaDevolucion.setTime(fechaDevolucion);
            calendarFechaDevolucion.add(Calendar.MONTH, 1); // Agregar un mes

            if (fechaActual.after(calendarFechaDevolucion.getTime())) {
                // El libro se considera perdido
                estado = "perdido";

                // Paso 4: Actualizar el estado del libro
                String actualizarEstadoLibroSQL = "UPDATE Libros SET estado = ? WHERE id = ?";
                try (PreparedStatement actualizarEstadoLibroStmt = connection.prepareStatement(actualizarEstadoLibroSQL)) {
                    actualizarEstadoLibroStmt.setString(1, estado);
                    actualizarEstadoLibroStmt.setInt(2, libroId);
                    actualizarEstadoLibroStmt.executeUpdate();
                }
            }

            // Confirmar la transacción
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Revertir la transacción en caso de error
            }
            throw e; // Relanzar la excepción para notificar el error al usuario
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Restablecer el modo de autocommit
                connection.close();
            }
        }
    }
}
