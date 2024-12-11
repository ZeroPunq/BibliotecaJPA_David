package Practica.Servicios;

import Practica.DAOs.*;
import Practica.DTOs.*;

import java.time.LocalDate;

public class Biblioteca {
    private final UsuarioDAO usuarioDAO;
    private final LibroDAO libroDAO;
    private final EjemplarDAO ejemplarDAO;
    private final PrestamoDAO prestamoDAO;

    public Biblioteca(UsuarioDAO usuarioDAO, LibroDAO libroDAO, EjemplarDAO ejemplarDAO, PrestamoDAO prestamoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.libroDAO = libroDAO;
        this.ejemplarDAO = ejemplarDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // Gestión de Usuarios
    public void registrarUsuario(String dni, String nombre, String email, String password, String tipo) {
        Usuario usuario = new Usuario();
        usuario.setDni(dni);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setTipo(tipo);
        usuarioDAO.guardar(usuario);
        System.out.println("Usuario registrado con éxito.");
    }

    public void listarUsuarios() {
        usuarioDAO.listarTodos().forEach(System.out::println);
    }

    // Gestión de Libros
    public void registrarLibro(String isbn, String titulo, String autor) {
        Libro libro = new Libro();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libroDAO.guardar(libro);
        System.out.println("Libro registrado con éxito.");
    }

    public void listarLibros() {
        libroDAO.listarTodos().forEach(System.out::println);
    }

    // Gestión de Ejemplares
    public void registrarEjemplar(String isbn, String estado) {
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un libro con ISBN: " + isbn));

        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(libro);
        ejemplar.setEstado(estado);
        ejemplarDAO.guardar(ejemplar);
        System.out.println("Ejemplar registrado con éxito.");
    }

    public void contarEjemplaresDisponibles(String isbn) {
        long disponibles = ejemplarDAO.listarTodos().stream()
                .filter(e -> e.getLibro().getIsbn().equals(isbn) && e.getEstado().equals("Disponible"))
                .count();

        System.out.println("Ejemplares disponibles para el ISBN " + isbn + ": " + disponibles);
    }

    // Gestión de Préstamos
    public void registrarPrestamo(int usuarioId, int ejemplarId) {
        Usuario usuario = usuarioDAO.listarTodos().stream()
                .filter(u -> u.getId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
            throw new IllegalStateException("El usuario tiene una penalización activa hasta: " + usuario.getPenalizacionHasta());
        }

        long prestamosActivos = prestamoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().equals(usuario) && p.getFechaDevolucion() == null)
                .count();

        if (prestamosActivos >= 3) {
            throw new IllegalStateException("El usuario ya tiene 3 préstamos activos.");
        }

        Ejemplar ejemplar = ejemplarDAO.listarTodos().stream()
                .filter(e -> e.getId().equals(ejemplarId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado."));

        if (!ejemplar.getEstado().equals("Disponible")) {
            throw new IllegalStateException("El ejemplar no está disponible.");
        }

        ejemplar.setEstado("Prestado");
        ejemplarDAO.guardar(ejemplar);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.now());
        prestamoDAO.guardar(prestamo);

        System.out.println("Préstamo registrado. Fecha límite: " + LocalDate.now().plusDays(15));
    }

    public void registrarDevolucion(int prestamoId) {
        Prestamo prestamo = prestamoDAO.listarTodos().stream()
                .filter(p -> p.getId().equals(prestamoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));

        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("El préstamo ya fue devuelto.");
        }

        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado("Disponible");
        ejemplarDAO.guardar(ejemplar);

        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoDAO.guardar(prestamo);

        if (LocalDate.now().isAfter(prestamo.getFechaInicio().plusDays(15))) {
            Usuario usuario = prestamo.getUsuario();
            long diasRetraso = LocalDate.now().toEpochDay() - prestamo.getFechaInicio().plusDays(15).toEpochDay();
            usuario.setPenalizacionHasta(LocalDate.now().plusDays(diasRetraso * 15));
            usuarioDAO.guardar(usuario);
            System.out.println("Préstamo devuelto con retraso. Penalización aplicada hasta: " + usuario.getPenalizacionHasta());
        } else {
            System.out.println("Préstamo devuelto a tiempo.");
        }
    }

    public void listarPrestamosGestor() {
        prestamoDAO.listarTodos().forEach(System.out::println);
    }

    public void listarPrestamosUsuario(int usuarioId) {
        prestamoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().getId().equals(usuarioId))
                .forEach(System.out::println);
    }
}
