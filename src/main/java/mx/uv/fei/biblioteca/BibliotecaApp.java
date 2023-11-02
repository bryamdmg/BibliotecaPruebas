package main.java.mx.uv.fei.biblioteca;

import main.java.mx.uv.fei.dao.LibroDAO;
import main.java.mx.uv.fei.dao.PrestamoDAO;
import main.java.mx.uv.fei.logic.Libro;

import java.sql.SQLException;
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
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea

            switch (opcion) {
                case 1:
                    altaLibro(libroDAO);
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
                    System.out.println("Saliendo del programa.");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void altaLibro(LibroDAO libroDAO) {
        // Implementa la lógica de alta de libros aquí
        try {
            // Crea un objeto Libro con los datos necesarios
            Libro libro = new Libro();
            libro.setId(1); // Cambia el ID por el valor deseado
            libro.setTitulo("Título del libro"); // Cambia el título por el valor deseado
            libro.setRestringido(false); // Cambia el estado restringido si es necesario

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
        // Implementa la lógica de préstamo de libros aquí
        System.out.print("Ingrese el ID del libro a prestar: ");
        int libroId = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        try {
            // Obtén la fecha actual (puedes utilizar otras formas de obtener la fecha)
            Date fechaPrestamo = new Date();

            prestamoDAO.prestamoLibro(libroId, fechaPrestamo);
            System.out.println("Libro prestado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al realizar el préstamo: " + e.getMessage());
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

