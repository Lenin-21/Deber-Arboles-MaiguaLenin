package app;

import enums.TipoUnidad;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;

public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static int[] codigosSwE1() {
        return new int[] {
            60, 35, 80, 20, 45, 70, 90, 40, 75
        };
    }

    public static ArbolBiblioteca crearBibliotecaInicial() {
        ArbolBiblioteca biblioteca = new ArbolBiblioteca(
            new UnidadBiblioteca(
                "BIB",
                "Biblioteca FISEI",
                TipoUnidad.BIBLIOTECA
            )
        );

        biblioteca.agregar(
            "BIB",
            new UnidadBiblioteca("SW", "Software", TipoUnidad.AREA)
        );

        biblioteca.agregar(
            "SW",
            new UnidadBiblioteca(
                "SW-E1",
                "Estante 1 de Software",
                TipoUnidad.ESTANTE
            )
        );

        biblioteca.agregar(
            "SW",
            new UnidadBiblioteca(
                "SW-E2",
                "Estante 2 de Software",
                TipoUnidad.ESTANTE
            )
        );

        biblioteca.agregar(
            "BIB",
            new UnidadBiblioteca(
                "TI",
                "Tecnologias de la Informacion",
                TipoUnidad.AREA
            )
        );

        biblioteca.agregar(
            "TI",
            new UnidadBiblioteca(
                "TI-E1",
                "Estante 1 de TI",
                TipoUnidad.ESTANTE
            )
        );

        biblioteca.agregar(
            "BIB",
            new UnidadBiblioteca(
                "TEL",
                "Telecomunicaciones",
                TipoUnidad.AREA
            )
        );

        cargarLibros(biblioteca, "SW-E1", codigosSwE1());

        cargarLibros(
            biblioteca,
            "SW-E2",
            new int[] {15, 10, 25}
        );

        cargarLibros(
            biblioteca,
            "TI-E1",
            new int[] {50}
        );

        return biblioteca;
    }

    private static void cargarLibros(
        ArbolBiblioteca biblioteca,
        String estante,
        int[] codigos
    ) {
        for (int codigo : codigos) {
            biblioteca.buscar(estante)
                      .getCatalogo()
                      .insertar(libroDePrueba(codigo));
        }
    }

    public static Libro libroDePrueba(int codigo) {
        return new Libro(
            codigo,
            "Libro de prueba " + codigo,
            "Autor de prueba",
            2026
        );
    }
}