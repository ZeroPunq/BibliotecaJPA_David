package Practica;

import Practica.DAOs.*;
import Practica.Servicios.Biblioteca;
import jakarta.persistence.EntityManager;

import java.util.Scanner;

public class Menu {
    private final Biblioteca biblioteca;

    public Menu() {
        EntityManager em = (EntityManager) Conexion.getEntityManager();
        this.biblioteca = new Biblioteca(
                new UsuarioDAO(em),
                new LibroDAO(em),
                new EjemplarDAO(em),
                new PrestamoDAO(em)
        );
    }

    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Listar Usuarios");
            System.out.println("3. Registrar Libro");
            System.out.println("4. Listar Libros");
            System.out.println("5. Registrar Préstamo");
            System.out.println("6. Listar Préstamos");
            System.out.println("7. Salir");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1 -> registrarUsuario(scanner);
                case 2 -> biblioteca.listarUsuarios();
                case 3 -> registrarLibro(scanner);
                case 4 -> biblioteca.listarLibros();
                case 5 -> registrarPrestamo(scanner);
                case 6 -> biblioteca.listarPrestamos();
                case 7 -> {
                    Conexion.cerrar();
                    System.out.println("Saliendo del sistema...");
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void registrarUsuario(Scanner scanner) {
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Tipo: ");
        String tipo = scanner.nextLine();

        biblioteca.registrarUsuario(dni, nombre, email, password, tipo);
    }

    private void registrarLibro(Scanner scanner) {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        biblioteca.registrarLibro(isbn, titulo, autor);
    }

    private void registrarPrestamo(Scanner scanner) {
        System.out.print("ID Usuario: ");
        int usuarioId = scanner.nextInt();
        System.out.print("ID Ejemplar: ");
        int ejemplarId = scanner.nextInt();

        biblioteca.registrarPrestamo(usuarioId, ejemplarId);
    }
}
