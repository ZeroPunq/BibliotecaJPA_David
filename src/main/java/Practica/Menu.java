package Practica;

import Practica.Conexion;
import Practica.DAOs.*;
import Practica.Servicios.Biblioteca;

import java.util.Scanner;

public class Menu {
    private final Biblioteca biblioteca;
    private final Scanner scanner;

    public Menu() {
        // Inicializar EntityManager y DAOs
        var em = Conexion.getEntityManager();
        this.biblioteca = new Biblioteca(
                new UsuarioDAO(em),
                new LibroDAO(em),
                new EjemplarDAO(em),
                new PrestamoDAO(em)
        );
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        while (true) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Listar Usuarios");
            System.out.println("3. Registrar Libro");
            System.out.println("4. Listar Libros");
            System.out.println("5. Registrar Ejemplar");
            System.out.println("6. Ver Ejemplares Disponibles");
            System.out.println("7. Registrar Préstamo");
            System.out.println("8. Registrar Devolución");
            System.out.println("9. Ver Todos los Préstamos");
            System.out.println("10. Ver Préstamos del Usuario");
            System.out.println("11. Salir");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            try {
                switch (opcion) {
                    case 1 -> registrarUsuario();
                    case 2 -> biblioteca.listarUsuarios();
                    case 3 -> registrarLibro();
                    case 4 -> biblioteca.listarLibros();
                    case 5 -> registrarEjemplar();
                    case 6 -> verEjemplaresDisponibles();
                    case 7 -> registrarPrestamo();
                    case 8 -> registrarDevolucion();
                    case 9 -> biblioteca.listarPrestamosGestor();
                    case 10 -> verPrestamosUsuario();
                    case 11 -> {
                        System.out.println("Saliendo del sistema...");
                        Conexion.cerrar();
                        return;
                    }
                    default -> System.out.println("Opción inválida, intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registrarUsuario() {
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Tipo (normal/administrador): ");
        String tipo = scanner.nextLine();

        biblioteca.registrarUsuario(dni, nombre, email, password, tipo);
    }

    private void registrarLibro() {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        biblioteca.registrarLibro(isbn, titulo, autor);
    }

    private void registrarEjemplar() {
        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine();
        System.out.print("Estado del ejemplar (Disponible, Prestado, Dañado): ");
        String estado = scanner.nextLine();

        biblioteca.registrarEjemplar(isbn, estado);
    }

    private void verEjemplaresDisponibles() {
        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine();
        biblioteca.contarEjemplaresDisponibles(isbn);
    }

    private void registrarPrestamo() {
        System.out.print("ID del usuario: ");
        int usuarioId = scanner.nextInt();
        System.out.print("ID del ejemplar: ");
        int ejemplarId = scanner.nextInt();

        biblioteca.registrarPrestamo(usuarioId, ejemplarId);
    }

    private void registrarDevolucion() {
        System.out.print("ID del préstamo: ");
        int prestamoId = scanner.nextInt();

        biblioteca.registrarDevolucion(prestamoId);
    }

    private void verPrestamosUsuario() {
        System.out.print("ID del usuario: ");
        int usuarioId = scanner.nextInt();
        biblioteca.listarPrestamosUsuario(usuarioId);
    }
}
