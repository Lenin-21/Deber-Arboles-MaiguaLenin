package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.Arrays;
import java.util.List;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoLibro;

public final class PruebasBiblioteca {
    private static int total;
    private static int correctas;

    private PruebasBiblioteca() {
    }

    public static void main(String[] args) {
        if (!ejecutar()) {
            System.exit(1);
        }
    }

    public static boolean ejecutar() {
        total = 0;
        correctas = 0;

        System.out.println("PRUEBAS BIBLIOTECA FISEI");

        probar("Raiz sola: altura 0 y una hoja", () -> {
            ArbolBiblioteca arbol = soloRaiz();

            igual(0, arbol.altura());
            igual(1, arbol.contarHojas());
            igual(0, arbol.contarLibrosTotal());
        });

        probar("Inicial: altura 2, cuatro hojas y trece libros", () -> {
            ArbolBiblioteca arbol = DatosPrueba.crearBibliotecaInicial();

            igual(2, arbol.altura());
            igual(4, arbol.contarHojas());
            igual(13, arbol.contarLibrosTotal());
        });

        probar("Niveles de BIB, sw, SW-E2 y XYZ", () -> {
            ArbolBiblioteca arbol = DatosPrueba.crearBibliotecaInicial();

            igual(0, arbol.nivel("BIB"));
            igual(1, arbol.nivel("sw"));
            igual(2, arbol.nivel("SW-E2"));
            igual(-1, arbol.nivel("XYZ"));
        });

        probar("Rechazar ESTANTE directamente bajo BIBLIOTECA", () -> {
            ArbolBiblioteca arbol = soloRaiz();

            falso(arbol.agregar(
                "BIB",
                new UnidadBiblioteca(
                    "E1", "Estante", TipoUnidad.ESTANTE
                )
            ));

            igual(1, arbol.contarHojas());
        });

        probar("Agregar AREA y ESTANTE respeta la jerarquia", () -> {
            ArbolBiblioteca arbol = soloRaiz();

            verdadero(arbol.agregar(
                "bib",
                new UnidadBiblioteca("a", "Area", TipoUnidad.AREA)
            ));

            verdadero(arbol.agregar(
                "A",
                new UnidadBiblioteca(
                    "a-e", "Estante", TipoUnidad.ESTANTE
                )
            ));

            igual("A-E", arbol.buscar("a-e").getDato().getCodigo());
            igual(2, arbol.altura());
        });

        probar("Rechazar codigos de unidad repetidos", () -> {
            ArbolBiblioteca arbol = DatosPrueba.crearBibliotecaInicial();

            falso(arbol.agregar(
                "BIB",
                new UnidadBiblioteca(
                    "sw", "Duplicada", TipoUnidad.AREA
                )
            ));

            igual(13, arbol.contarLibrosTotal());
        });

        probar("Padre y unidad inexistentes", () -> {
            ArbolBiblioteca arbol = soloRaiz();

            falso(arbol.agregar(
                "XYZ",
                new UnidadBiblioteca("A", "Area", TipoUnidad.AREA)
            ));

            igual(null, arbol.buscar("XYZ"));
        });

        probar("Solo los estantes tienen catalogo", () -> {
            ArbolBiblioteca arbol = DatosPrueba.crearBibliotecaInicial();

            igual(null, arbol.buscar("BIB").getCatalogo());
            igual(null, arbol.buscar("SW").getCatalogo());

            verdadero(arbol.buscar("SW-E1").getCatalogo() != null);
        });

        probar("Jerarquia en preorden con dos espacios por nivel", () -> {
            String jerarquia = DatosPrueba.crearBibliotecaInicial()
                                          .mostrarJerarquia();

            String[] lineas = jerarquia.trim().split("\\R");

            igual(7, lineas.length);

            verdadero(lineas[0].startsWith("BIB"));
            verdadero(lineas[1].startsWith("  SW"));
            verdadero(lineas[2].startsWith("    SW-E1"));
            verdadero(lineas[3].startsWith("    SW-E2"));
            verdadero(lineas[4].startsWith("  TI"));
            verdadero(lineas[5].startsWith("    TI-E1"));
            verdadero(lineas[6].startsWith("  TEL"));
        });

        probar("PREORDEN obligatorio de SW-E1", () -> {
            codigosIguales(
                catalogoInicial().recorrer(TipoRecorrido.PREORDEN),
                60, 35, 20, 45, 40, 80, 70, 75, 90
            );
        });

        probar("INORDEN obligatorio de SW-E1", () -> {
            codigosIguales(
                catalogoInicial().recorrer(TipoRecorrido.INORDEN),
                20, 35, 40, 45, 60, 70, 75, 80, 90
            );
        });

        probar("POSORDEN obligatorio de SW-E1", () -> {
            codigosIguales(
                catalogoInicial().recorrer(TipoRecorrido.POSORDEN),
                20, 40, 45, 35, 75, 70, 90, 80, 60
            );
        });

        probar("Insertar codigo repetido conserva el libro original", () -> {
            CatalogoABB catalogo = catalogoInicial();
            Libro original = catalogo.buscar(60);

            falso(catalogo.insertar(
                new Libro(60, "Otro titulo", "Otro autor", 2000)
            ));

            igual(9, catalogo.contar());
            verdadero(original == catalogo.buscar(60));
        });

        probar("Camino de busqueda de 42 ausente", () -> {
            igual(
                Arrays.asList(60, 35, 45, 40),
                catalogoInicial().caminoBusqueda(42)
            );
        });

        probar("Contar codigos en rango inclusivo [40, 75]", () -> {
            igual(5, catalogoInicial().contarEnRango(40, 75));
        });

        probar("Insertar 42 y eliminar 20, 70 y 60", () -> {
            CatalogoABB catalogo = catalogoInicial();

            verdadero(catalogo.insertar(
                DatosPrueba.libroDePrueba(42)
            ));

            verdadero(catalogo.eliminar(20));
            verdadero(catalogo.eliminar(70));
            verdadero(catalogo.eliminar(60));

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                75, 35, 45, 40, 42, 80, 90
            );

            igual(7, catalogo.contar());
            verdadero(catalogo.esABBValido());
        });

        probar("Buscar, modificar y eliminar codigo inexistente", () -> {
            CatalogoABB catalogo = catalogoInicial();

            igual(null, catalogo.buscar(999));
            falso(catalogo.modificar(999, "Titulo", "Autor", 2026));
            falso(catalogo.eliminar(999));

            igual(9, catalogo.contar());
        });

        probar("Libro con codigo cero o negativo lanza excepcion", () -> {
            esperaArgumentoInvalido(
                () -> new Libro(0, "Titulo", "Autor", 2026)
            );

            esperaArgumentoInvalido(
                () -> new Libro(-1, "Titulo", "Autor", 2026)
            );
        });

        probar("Libro rechaza titulo o autor vacios y nulos", () -> {
            esperaArgumentoInvalido(
                () -> new Libro(1, "  ", "Autor", 2026)
            );

            esperaArgumentoInvalido(
                () -> new Libro(1, "Titulo", null, 2026)
            );

            esperaArgumentoInvalido(
                () -> new Libro(1, null, "Autor", 2026)
            );

            esperaArgumentoInvalido(
                () -> new Libro(1, "Titulo", "", 2026)
            );
        });

        probar("Libro valida anios y acepta ambos limites", () -> {
            esperaArgumentoInvalido(
                () -> new Libro(1, "Titulo", "Autor", 1899)
            );

            esperaArgumentoInvalido(
                () -> new Libro(1, "Titulo", "Autor", 2027)
            );

            igual(
                1900,
                new Libro(1, "Titulo", "Autor", 1900).getAnio()
            );

            igual(
                2026,
                new Libro(2, "Titulo", "Autor", 2026).getAnio()
            );
        });

        probar("Modificar conserva codigo y orden", () -> {
            CatalogoABB catalogo = catalogoInicial();

            verdadero(catalogo.modificar(
                35, "Nuevo titulo", "Nueva autora", 2020
            ));

            Libro libro = catalogo.buscar(35);

            igual(35, libro.getCodigo());
            igual("Nuevo titulo", libro.getTitulo());
            igual("Nueva autora", libro.getAutor());
            igual(2020, libro.getAnio());

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.INORDEN),
                20, 35, 40, 45, 60, 70, 75, 80, 90
            );
        });

        probar("Modificacion invalida no deja cambios parciales", () -> {
            CatalogoABB catalogo = catalogoInicial();

            String titulo = catalogo.buscar(35).getTitulo();
            String autor = catalogo.buscar(35).getAutor();
            int anio = catalogo.buscar(35).getAnio();

            esperaArgumentoInvalido(
                () -> catalogo.modificar(
                    35, "Titulo cambiado", "Autor cambiado", 2027
                )
            );

            igual(titulo, catalogo.buscar(35).getTitulo());
            igual(autor, catalogo.buscar(35).getAutor());
            igual(anio, catalogo.buscar(35).getAnio());
        });

        probar("Validacion global detecta nieto mal ubicado", () -> {
            CatalogoABB catalogo = new CatalogoABB();

            NodoLibro raiz = new NodoLibro(
                DatosPrueba.libroDePrueba(60)
            );

            NodoLibro izquierdo = new NodoLibro(
                DatosPrueba.libroDePrueba(35)
            );

            raiz.setIzquierdo(izquierdo);

            // 65 supera el limite 60 impuesto por el antepasado.
            izquierdo.setDerecho(
                new NodoLibro(DatosPrueba.libroDePrueba(65))
            );

            catalogo.setRaiz(raiz);

            falso(catalogo.esABBValido());
        });

        probar("Validacion detecta codigo duplicado en descendiente", () -> {
            CatalogoABB catalogo = catalogoInicial();

            catalogo.getRaiz()
                    .getIzquierdo()
                    .setIzquierdo(
                        new NodoLibro(DatosPrueba.libroDePrueba(60))
                    );

            falso(catalogo.esABBValido());
        });

        probar("Catalogo vacio y eliminacion de la unica raiz", () -> {
            CatalogoABB catalogo = new CatalogoABB();

            igual(0, catalogo.contar());
            igual(null, catalogo.buscar(1));

            verdadero(catalogo.esABBValido());
            verdadero(catalogo.recorrer(TipoRecorrido.PREORDEN).isEmpty());
            verdadero(catalogo.caminoBusqueda(1).isEmpty());

            igual(
                "null -> regresa\nSecuencia PREORDEN:",
                normalizar(
                    catalogo.trazaRecorrido(TipoRecorrido.PREORDEN)
                )
            );

            falso(catalogo.eliminar(1));

            verdadero(catalogo.insertar(
                DatosPrueba.libroDePrueba(1)
            ));

            verdadero(catalogo.eliminar(1));

            igual(null, catalogo.getRaiz());
            igual(0, catalogo.contar());
        });

        probar("Eliminar raiz con un hijo izquierdo", () -> {
            CatalogoABB catalogo = catalogoDe(10, 5);

            verdadero(catalogo.eliminar(10));

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                5
            );
        });

        probar("Eliminar raiz con un hijo derecho", () -> {
            CatalogoABB catalogo = catalogoDe(10, 15);

            verdadero(catalogo.eliminar(10));

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                15
            );
        });

        probar("Sucesor inorden conserva su posible hijo derecho", () -> {
            CatalogoABB catalogo = catalogoDe(
                50, 30, 80, 60, 65, 90
            );

            verdadero(catalogo.eliminar(50));

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                60, 30, 80, 65, 90
            );

            verdadero(catalogo.esABBValido());
        });

        probar("Traza PREORDEN exacta en un arbol pequeno", () -> {
            String esperada =
                "entra 2\nVISITA 2\n"
                + "  entra 1\n  VISITA 1\n"
                + "    null -> regresa\n    null -> regresa\n"
                + "  entra 3\n  VISITA 3\n"
                + "    null -> regresa\n    null -> regresa\n"
                + "Secuencia PREORDEN: 2 1 3";

            igual(
                esperada,
                normalizar(
                    catalogoDe(2, 1, 3)
                        .trazaRecorrido(TipoRecorrido.PREORDEN)
                )
            );
        });

        probar("Trazas INORDEN y POSORDEN terminan con sus secuencias", () -> {
            CatalogoABB catalogo = catalogoDe(2, 1, 3);

            verdadero(
                normalizar(catalogo.trazaRecorrido(TipoRecorrido.INORDEN))
                    .endsWith("Secuencia INORDEN: 1 2 3")
            );

            verdadero(
                normalizar(catalogo.trazaRecorrido(TipoRecorrido.POSORDEN))
                    .endsWith("Secuencia POSORDEN: 1 3 2")
            );
        });

        probar("Rangos y caminos de libros existentes", () -> {
            CatalogoABB catalogo = catalogoInicial();

            igual(1, catalogo.contarEnRango(60, 60));
            igual(0, catalogo.contarEnRango(1, 19));

            igual(
                9,
                catalogo.contarEnRango(
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
                )
            );

            igual(
                Arrays.asList(60),
                catalogo.caminoBusqueda(60)
            );

            igual(
                Arrays.asList(60, 80, 70, 75),
                catalogo.caminoBusqueda(75)
            );
        });

        probar("Codigos extremos no desbordan validacion", () -> {
            CatalogoABB catalogo = catalogoDe(
                1, Integer.MAX_VALUE
            );

            verdadero(catalogo.esABBValido());

            igual(
                2,
                catalogo.contarEnRango(1, Integer.MAX_VALUE)
            );

            igual(
                Integer.MAX_VALUE,
                catalogo.buscar(Integer.MAX_VALUE).getCodigo()
            );
        });

        probar("Extra: reconstruir preorden completo", () -> {
            int[] preorden = {
                60, 35, 20, 45, 40, 80, 70, 75, 90
            };

            CatalogoABB catalogo = CatalogoABB.desdePreorden(preorden);

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                preorden
            );

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.INORDEN),
                20, 35, 40, 45, 60, 70, 75, 80, 90
            );

            verdadero(catalogo.esABBValido());
            igual(9, catalogo.contar());
        });

        probar("Extra: rechazar preorden invalido", () -> {
            esperaArgumentoInvalido(
                () -> CatalogoABB.desdePreorden(
                    new int[] {10, 15, 5}
                )
            );

            esperaArgumentoInvalido(
                () -> CatalogoABB.desdePreorden(
                    new int[] {10, 5, 10}
                )
            );

            esperaArgumentoInvalido(
                () -> CatalogoABB.desdePreorden(new int[] {0})
            );
        });

        probar("Extra: reconstruir vacio y codigos extremos", () -> {
            igual(
                0,
                CatalogoABB.desdePreorden(new int[0]).contar()
            );

            CatalogoABB catalogo = CatalogoABB.desdePreorden(
                new int[] {1, Integer.MAX_VALUE}
            );

            verdadero(catalogo.esABBValido());

            codigosIguales(
                catalogo.recorrer(TipoRecorrido.PREORDEN),
                1, Integer.MAX_VALUE
            );
        });

        probar("Los cambios de un estante actualizan el total", () -> {
            ArbolBiblioteca arbol = DatosPrueba.crearBibliotecaInicial();
            CatalogoABB catalogo = arbol.buscar("TI-E1").getCatalogo();

            verdadero(catalogo.insertar(
                DatosPrueba.libroDePrueba(51)
            ));

            igual(14, arbol.contarLibrosTotal());

            verdadero(catalogo.eliminar(50));

            igual(13, arbol.contarLibrosTotal());
        });

        System.out.println(correctas + "/" + total + " pruebas OK");

        return correctas == total;
    }

    private static ArbolBiblioteca soloRaiz() {
        return new ArbolBiblioteca(
            new UnidadBiblioteca(
                "BIB",
                "Biblioteca FISEI",
                TipoUnidad.BIBLIOTECA
            )
        );
    }

    private static CatalogoABB catalogoInicial() {
        return DatosPrueba.crearBibliotecaInicial()
                          .buscar("SW-E1")
                          .getCatalogo();
    }

    private static CatalogoABB catalogoDe(int... codigos) {
        CatalogoABB catalogo = new CatalogoABB();

        for (int codigo : codigos) {
            catalogo.insertar(DatosPrueba.libroDePrueba(codigo));
        }

        return catalogo;
    }

    private static void probar(String nombre, Runnable prueba) {
        total++;

        try {
            prueba.run();
            correctas++;

            System.out.println("OK " + total + ". " + nombre);
        } catch (AssertionError | RuntimeException ex) {
            System.out.println(
                "FALLO " + total + ". " + nombre
                + " -> " + ex.getMessage()
            );
        }
    }

    private static void igual(Object esperado, Object actual) {
        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(
                "Esperado: " + esperado + "; obtenido: " + actual
            );
        }
    }

    private static void verdadero(boolean valor) {
        if (!valor) {
            throw new AssertionError("Se esperaba true");
        }
    }

    private static void falso(boolean valor) {
        if (valor) {
            throw new AssertionError("Se esperaba false");
        }
    }

    private static void codigosIguales(
        List<Libro> libros,
        int... esperados
    ) {
        igual(esperados.length, libros.size());

        for (int i = 0; i < esperados.length; i++) {
            igual(esperados[i], libros.get(i).getCodigo());
        }
    }

    private static void esperaArgumentoInvalido(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException ex) {
            return;
        }

        throw new AssertionError(
            "Se esperaba IllegalArgumentException"
        );
    }

    private static String normalizar(String texto) {
        return texto.replace("\r\n", "\n").trim();
    }
}