package Practica.Servicios;

import Practica.DAOs.*;
import Practica.DTOs.*;

import java.time.LocalDate;

public class Biblioteca {
    // DAOs para gestionar las operaciones de cada entidad (Usuarios, Libros, Ejemplares, Préstamos)
    private final UsuarioDAO usuarioDAO;
    private final LibroDAO libroDAO;
    private final EjemplarDAO ejemplarDAO;
    private final PrestamoDAO prestamoDAO;

    // Constructor que inicializa los DAOs
    public Biblioteca(UsuarioDAO usuarioDAO, LibroDAO libroDAO, EjemplarDAO ejemplarDAO, PrestamoDAO prestamoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.libroDAO = libroDAO;
        this.ejemplarDAO = ejemplarDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // Método para registrar un nuevo usuario
    public void registrarUsuario(String dni, String nombre, String email, String password, String tipo) {
        Usuario usuario = new Usuario(); // Crear un nuevo objeto Usuario
        usuario.setDni(dni); // Asignar DNI
        usuario.setNombre(nombre); // Asignar nombre
        usuario.setEmail(email); // Asignar email
        usuario.setPassword(password); // Asignar contraseña
        usuario.setTipo(tipo); // Asignar tipo (normal o administrador)
        usuarioDAO.guardar(usuario); // Guardar usuario en la base de datos
        System.out.println("Usuario registrado con éxito.");
    }

    // Método para listar todos los usuarios registrados
    public void listarUsuarios() {
        usuarioDAO.listarTodos().forEach(System.out::println); // Listar usuarios y mostrarlos en consola
    }

    // Método para registrar un nuevo libro
    public void registrarLibro(String isbn, String titulo, String autor) {
        Libro libro = new Libro(); // Crear un nuevo objeto Libro
        libro.setIsbn(isbn); // Asignar ISBN
        libro.setTitulo(titulo); // Asignar título
        libro.setAutor(autor); // Asignar autor
        libroDAO.guardar(libro); // Guardar libro en la base de datos
        System.out.println("Libro registrado con éxito.");
    }

    // Método para listar todos los libros registrados
    public void listarLibros() {
        libroDAO.listarTodos().forEach(System.out::println); // Listar libros y mostrarlos en consola
    }

    // Método para registrar un nuevo ejemplar asociado a un libro
    public void registrarEjemplar(String isbn, String estado) {
        // Buscar el libro asociado al ISBN
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals(isbn)) // Filtrar por ISBN
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un libro con ISBN: " + isbn));

        // Crear un nuevo ejemplar
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(libro); // Asociar el libro al ejemplar
        ejemplar.setEstado(estado); // Asignar el estado (Disponible, Prestado, Dañado)
        ejemplarDAO.guardar(ejemplar); // Guardar el ejemplar en la base de datos
        System.out.println("Ejemplar registrado con éxito.");
    }

    // Método para contar cuántos ejemplares están disponibles para un libro
    public void contarEjemplaresDisponibles(String isbn) {
        long disponibles = ejemplarDAO.listarTodos().stream()
                .filter(e -> e.getLibro().getIsbn().equals(isbn) && e.getEstado().equals("Disponible")) // Filtrar por estado
                .count(); // Contar los ejemplares disponibles

        System.out.println("Ejemplares disponibles para el ISBN " + isbn + ": " + disponibles);
    }

    // Método para registrar un nuevo préstamo
    public void registrarPrestamo(int usuarioId, int ejemplarId) {
        // Buscar usuario por ID
        Usuario usuario = usuarioDAO.listarTodos().stream()
                .filter(u -> u.getId().equals(usuarioId)) // Filtrar por ID
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Validar penalización activa
        if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
            throw new IllegalStateException("El usuario tiene una penalización activa hasta: " + usuario.getPenalizacionHasta());
        }

        // Contar préstamos activos del usuario
        long prestamosActivos = prestamoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().equals(usuario) && p.getFechaDevolucion() == null) // Filtrar préstamos sin devolución
                .count();

        if (prestamosActivos >= 3) { // Validar que no tenga más de 3 préstamos activos
            throw new IllegalStateException("El usuario ya tiene 3 préstamos activos.");
        }

        // Buscar ejemplar por ID
        Ejemplar ejemplar = ejemplarDAO.listarTodos().stream()
                .filter(e -> e.getId().equals(ejemplarId)) // Filtrar por ID
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado."));

        if (!ejemplar.getEstado().equals("Disponible")) { // Validar disponibilidad del ejemplar
            throw new IllegalStateException("El ejemplar no está disponible.");
        }

        // Cambiar el estado del ejemplar a "Prestado"
        ejemplar.setEstado("Prestado");
        ejemplarDAO.guardar(ejemplar);

        // Crear y guardar el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario); // Asociar usuario
        prestamo.setEjemplar(ejemplar); // Asociar ejemplar
        prestamo.setFechaInicio(LocalDate.now()); // Registrar fecha de inicio
        prestamoDAO.guardar(prestamo);

        System.out.println("Préstamo registrado. Fecha límite: " + LocalDate.now().plusDays(15)); // Mostrar fecha límite
    }

    // Método para registrar la devolución de un préstamo
    public void registrarDevolucion(int prestamoId) {
        // Buscar préstamo por ID
        Prestamo prestamo = prestamoDAO.listarTodos().stream()
                .filter(p -> p.getId().equals(prestamoId)) // Filtrar por ID
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));

        if (prestamo.getFechaDevolucion() != null) { // Validar si ya fue devuelto
            throw new IllegalStateException("El préstamo ya fue devuelto.");
        }

        // Cambiar estado del ejemplar a "Disponible"
        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado("Disponible");
        ejemplarDAO.guardar(ejemplar);

        // Registrar fecha de devolución
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoDAO.guardar(prestamo);

        // Validar retraso y aplicar penalización si es necesario
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

    // Método para listar todos los préstamos (gestor)
    public void listarPrestamosGestor() {
        prestamoDAO.listarTodos().forEach(System.out::println); // Listar y mostrar en consola
    }

    // Método para listar los préstamos de un usuario específico
    public void listarPrestamosUsuario(int usuarioId) {
        prestamoDAO.listarTodos().stream()
                .filter(p -> p.getUsuario().getId().equals(usuarioId)) // Filtrar por ID de usuario
                .forEach(System.out::println); // Mostrar en consola
    }
}
