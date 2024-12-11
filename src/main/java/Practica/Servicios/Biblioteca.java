package Practica.Servicios;

import Practica.DAOs.EjemplarDAO;
import Practica.DAOs.LibroDAO;
import Practica.DAOs.PrestamoDAO;
import Practica.DAOs.UsuarioDAO;
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

    public void registrarPrestamo(int usuarioId, int ejemplarId) {
        Usuario usuario = usuarioDAO.listarTodos().stream()
                .filter(u -> u.getId() == usuarioId)
                .findFirst().orElseThrow();
        Ejemplar ejemplar = ejemplarDAO.listarTodos().stream()
                .filter(e -> e.getId() == ejemplarId)
                .findFirst().orElseThrow();
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.now());
        prestamoDAO.guardar(prestamo);
        System.out.println("Préstamo registrado con éxito.");
    }

    public void listarPrestamos() {
        prestamoDAO.listarTodos().forEach(System.out::println);
    }
}
