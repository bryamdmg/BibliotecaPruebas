package main.java.mx.uv.fei.biblioteca;

import main.java.mx.uv.fei.dao.LibroDAO;
import main.java.mx.uv.fei.dao.PrestamoDAO;
import main.java.mx.uv.fei.logic.Libro;
import main.java.mx.uv.fei.logic.Prestamo;
import main.java.mx.uv.fei.logic.Validaciones;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class BibliotecaApp {
    public static void main(String[] args) throws SQLException {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Biblioteca - Menú Principal");
            System.out.println("1. Alta de Libro");
            System.out.println("2. Consulta de Libro");
            System.out.println("3. Préstamo de Libro");
            System.out.println("4. Recepción de Libro");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    altaLibro(libroDAO, scanner);
                    break;
                case 2:
                    consultaLibro(libroDAO, scanner);
                    break;
                case 3:
                    prestamoLibro(prestamoDAO, libroDAO, scanner);
                    break;
                case 4:
                    recepcionLibro(prestamoDAO, scanner);
                    break;
                case 5:
                    System.out.println("Saliendo del programa.");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void altaLibro(LibroDAO libroDAO, Scanner scanner) throws SQLException {
        System.out.println("Alta de Libro");

        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();

        System.out.print("¿Es un libro restringido? (true/false): ");
        boolean restringido = scanner.nextBoolean();
        scanner.nextLine();

        try {
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setRestringido(restringido);

            libroDAO.altaLibro(libro);
            System.out.println("Libro dado de alta con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al dar de alta el libro: " + e.getMessage());
        }
    }

    private static void consultaLibro(LibroDAO libroDAO, Scanner scanner) throws SQLException {
        System.out.println("Consulta de Libro");
        System.out.print("Ingrese el ID del libro a consultar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Libro libro = libroDAO.consultaLibro(id);

        if (libro == null) {
            System.out.println("El libro con ID " + id + " no existe en la biblioteca.");
            return;
        }

        System.out.println("Información del libro:");
        System.out.println("ID: " + libro.getId());
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Restringido: " + libro.isRestringido());
        System.out.println("Estado: " + libro.getEstado());
    }

    private static void prestamoLibro(PrestamoDAO prestamoDAO, LibroDAO libroDAO, Scanner scanner) throws SQLException {
        System.out.println("Préstamo de Libro");
        System.out.print("Ingrese el ID del libro a prestar: ");
        int libroId = scanner.nextInt();
        scanner.nextLine();
        Libro libro = libroDAO.consultaLibro(libroId);

        if (libro == null) {
            System.out.println("El libro con ID " + libroId + " no existe en la biblioteca.");
            return;
        }

        if (!libro.getEstado().equals("disponible")) {
            System.out.println("El libro con ID " + libroId + " no está disponible para préstamo.");
            return;
        }

        System.out.print("Ingrese la fecha de préstamo (yyyy-MM-dd): ");
        String fechaPrestamoStr = scanner.nextLine();

        if (!Validaciones.esFechaValida(fechaPrestamoStr)) {
            System.out.println("Fecha de préstamo no válida.");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaPrestamo = null;
        try {
            fechaPrestamo = dateFormat.parse(fechaPrestamoStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (libro.isRestringido()) {
            if (!Validaciones.esFechaDevolucionValida(fechaPrestamo, true)) {
                System.out.println("La fecha de devolución debe ser exactamente en el siguiente día.");
                return;
            }
        } else {
            Date fechaDevolucion = calcularFechaDevolucion(fechaPrestamo);
            System.out.println("Fecha de devolución calculada: " + fechaDevolucion);

            if (!Validaciones.esFechaDevolucionValida(fechaDevolucion, false)) {
                System.out.println("La fecha de devolución debe ser dentro de 3 días.");
                return;
            }
        }

        try {
            prestamoDAO.prestamoLibro(libroId, fechaPrestamo);
            System.out.println("Libro prestado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al realizar el préstamo: " + e.getMessage());
        }
    }

    private static Date calcularFechaDevolucion(Date fechaPrestamo) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaPrestamo);

        calendar.add(Calendar.DAY_OF_MONTH, 15);

        return calendar.getTime();
    }

    private static void recepcionLibro(PrestamoDAO prestamoDAO, Scanner scanner) throws SQLException {
        System.out.println("Recepción de Libro");

        System.out.print("Ingrese el ID del libro a recibir: ");
        int libroId = scanner.nextInt();
        scanner.nextLine();

        ingresarFechaDevolucion(libroId, scanner);

        LibroDAO libroDAO = new LibroDAO();
        Libro libro = libroDAO.consultaLibro(libroId);

        if (libro == null) {
            System.out.println("El libro con ID " + libroId + " no existe en la biblioteca.");
            return;
        }

        if (!libro.getEstado().equals("prestado") && !libro.getEstado().equals("retraso")) {
            System.out.println("El libro con ID " + libroId + " no está en estado de préstamo.");
            return;
        }

        System.out.print("Ingrese el ID del prestamo: ");
        int prestamoId = scanner.nextInt();
        scanner.nextLine();
        Prestamo prestamo = prestamoDAO.obtenerPrestamo(prestamoId);

        Date fechaDevolucion = prestamo.getFecha_devolucion();

        if (!Validaciones.esFechaValida(String.valueOf(fechaDevolucion))) {
            System.out.println("Fecha de devolución no válida.");
            return;
        }

        if (Validaciones.esLibroPerdido(fechaDevolucion)) {
            System.out.println("El libro se considera perdido.");
            libroDAO.marcarLibroComoPerdido(libroId);
            return;
        }

        try {
            prestamoDAO.recepcionLibro(libroId);
            System.out.println("Libro recibido con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al recibir el libro: " + e.getMessage());
        }
    }


    private static void ingresarFechaDevolucion(int libroId, Scanner scanner) {

        System.out.print("Ingrese la nueva fecha de devolución (YYYY-MM-DD): ");
        String fechaDevolucionStr = scanner.nextLine();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDevolucion = dateFormat.parse(fechaDevolucionStr);

            PrestamoDAO prestamoDAO = new PrestamoDAO();
            prestamoDAO.ingresarFechaDevolucion(libroId, fechaDevolucion);
            System.out.println("Fecha de devolución actualizada con éxito.");
        } catch (ParseException e) {
            System.out.println("Formato de fecha incorrecto. Utilice el formato YYYY-MM-DD.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar la fecha de devolución: " + e.getMessage());
        }
    }
}

