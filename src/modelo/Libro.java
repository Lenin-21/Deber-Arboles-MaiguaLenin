package modelo;

public class Libro {
    private final int codigo;
    private String titulo;
    private String autor;
    private int anio;

    public Libro(int codigo, String titulo, String autor, int anio) {
        if (codigo <= 0) {
            throw new IllegalArgumentException(
                "El codigo debe ser mayor que 0."
            );
        }

        validarTexto(titulo, "titulo");
        validarTexto(autor, "autor");
        validarAnio(anio);

        this.codigo = codigo;
        this.titulo = titulo.trim();
        this.autor = autor.trim();
        this.anio = anio;
    }

    private static void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El " + campo + " no puede estar vacio."
            );
        }
    }

    private static void validarAnio(int anio) {
        if (anio < 1900 || anio > 2026) {
            throw new IllegalArgumentException(
                "El anio debe estar entre 1900 y 2026."
            );
        }
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        validarTexto(titulo, "titulo");
        this.titulo = titulo.trim();
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        validarTexto(autor, "autor");
        this.autor = autor.trim();
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        validarAnio(anio);
        this.anio = anio;
    }

    @Override
    public String toString() {
        return codigo + " | " + titulo + " | " + autor + " | " + anio;
    }
}