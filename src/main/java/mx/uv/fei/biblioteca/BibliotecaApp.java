package main.java.mx.uv.fei.biblioteca;

import main.java.mx.uv.fei.dao.LibroDAO;
import main.java.mx.uv.fei.dao.PrestamoDAO;
import main.java.mx.uv.fei.logic.Libro;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class BibliotecaApp {
    public static void main(String[] args) {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Biblioteca - Menú Principal");
            System.out.println("1. Alta de Libro");
            System.out.println("2. Consulta de Libro");
            System.out.println("3. Préstamo de Libro");
            System.out.println("4. Recepción de Libro");
            System.out.println("5. Ingresar fecha de devolución");
            System.out.println("6. Salir");
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
                    prestamoLibro(prestamoDAO, scanner);
                    break;
                case 4:
                    recepcionLibro(prestamoDAO, scanner);
                    break;
                case 5:
                    ingresarFechaDevolucion(prestamoDAO, scanner);
                    break;
                case 6:
                    System.out.println("Saliendo del programa.");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void altaLibro(LibroDAO libroDAO, Scanner scanner) {
        System.out.println("Alta de Libro");

        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();

        System.out.print("¿Es un libro restringido? (true/false): ");
        boolean restringido = scanner.nextBoolean();
        scanner.nextLine(); // Consumir la nueva línea

        try {
            // Crea un objeto Libro con los datos ingresados por el usuario
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


    private static void consultaLibro(LibroDAO libroDAO, Scanner scanner) {
        // Implementa la lógica de consulta de libros aquí
        System.out.print("Ingrese el ID del libro a consultar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        try {
            Libro libro = libroDAO.consultaLibro(id);
            if (libro != null) {
                System.out.println("Información del libro:");
                System.out.println("ID: " + libro.getId());
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Restringido: " + libro.isRestringido());
                System.out.println("Estado: " + libro.getEstado());
            } else {
                System.out.println("Libro no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al consultar el libro: " + e.getMessage());
        }
    }

    private static void prestamoLibro(PrestamoDAO prestamoDAO, Scanner scanner) {
        System.out.print("Ingrese el ID del libro a prestar: ");
        int libroId = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        System.out.print("Ingrese la fecha de préstamo (yyyy-MM-dd): ");
        String fechaPrestamoStr = scanner.nextLine();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaPrestamo = dateFormat.parse(fechaPrestamoStr);

            prestamoDAO.prestamoLibro(libroId, fechaPrestamo);
            System.out.println("Libro prestado con éxito.");
        } catch (ParseException e) {
            System.out.println("Error al analizar la fecha. Formato válido: yyyy-MM-dd");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al realizar el préstamo: " + e.getMessage());
        }
    }

    private static void ingresarFechaDevolucion(PrestamoDAO prestamoDAO, Scanner scanner) {
        System.out.print("Ingrese el ID del libro prestado: ");
        int libroId = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        System.out.print("Ingrese la nueva fecha de devolución (YYYY-MM-DD): ");
        String fechaDevolucionStr = scanner.nextLine();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDevolucion = dateFormat.parse(fechaDevolucionStr);

            prestamoDAO.ingresarFechaDevolucion(libroId, fechaDevolucion);
            System.out.println("Fecha de devolución actualizada con éxito.");
        } catch (ParseException e) {
            System.out.println("Formato de fecha incorrecto. Utilice el formato YYYY-MM-DD.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al actualizar la fecha de devolución: " + e.getMessage());
        }
    }


    private static void recepcionLibro(PrestamoDAO prestamoDAO, Scanner scanner) {
        // Implementa la lógica de recepción de libros aquí
        System.out.print("Ingrese el ID del libro a recibir: ");
        int libroId = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        try {
            prestamoDAO.recepcionLibro(libroId);
            System.out.println("Libro recibido con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al recibir el libro: " + e.getMessage());
        }
    }
}

