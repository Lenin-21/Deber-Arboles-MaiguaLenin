package modelo;

import enums.TipoUnidad;
import java.util.Locale;

public final class UnidadBiblioteca {
    private final String codigo;
    private final String nombre;
    private final TipoUnidad tipo;

    public UnidadBiblioteca(
        String codigo,
        String nombre,
        TipoUnidad tipo
    ) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El codigo de la unidad no puede estar vacio."
            );
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre de la unidad no puede estar vacio."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                "El tipo de unidad es obligatorio."
            );
        }

        this.codigo = codigo.trim().toUpperCase(Locale.ROOT);
        this.nombre = nombre.trim();
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoUnidad getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return codigo + " " + nombre;
    }
}