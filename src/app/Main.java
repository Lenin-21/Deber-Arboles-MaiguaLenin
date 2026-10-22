package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import java.util.List;
import java.util.Scanner;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoUnidad;

public class Main {
    private final Scanner entrada;
    private ArbolBiblioteca biblioteca;

    public Main(Scanner entrada) {
        this.entrada = entrada;

        biblioteca = new ArbolBiblioteca(
            new UnidadBiblioteca(
                "BIB",
                "Biblioteca FISEI",
                TipoUnidad.BIBLIOTECA
            )
        );
    }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, "UTF-8")) {
            new Main(entrada).ejecutar();
        }
    }

    public void ejecutar() {
        System.out.println("CATALOGO DE LA BIBLIOTECA FISEI");
        System.out.println(
            "Se inicia con la raiz BIB. "
            + "La opcion 1 carga los datos del deber."
        );

        try {
            boolean continuar = true;

            while (continuar) {
                mostrarMenu();
                int opcion = leerEntero("Opcion: ");

                try {
                    switch (opcion) {
                        case 1:
                            cargarInicial();
                            break;

                        case 2:
                            System.out.println(
                                biblioteca.mostrarJerarquia()
                            );
                            break;

                        case 3:
                            agregarUnidad();
                            break;

                        case 4:
                            buscarUnidad();
                            break;

                        case 5:
                            consultarEstructura();
                            break;

                        case 6:
                            insertarLibro();
                            break;

                        case 7:
                            buscarLibro();
                            break;

                        case 8:
                            modificarLibro();
                            break;

                        case 9:
                            eliminarLibro();
                            break;

                        case 10:
                            mostrarRecorrido();
                            break;

                        case 11:
                            ejecutarReto();
                            break;

                        case 12:
                            reconstruirDesdePreorden();
                            break;

                        case 13:
                            PruebasBiblioteca.ejecutar();
                            break;

                        case 0:
                            continuar = false;
                            break;

                        default:
                            System.out.println(
                                "Opcion inexistente. Intenta nuevamente."
                            );
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println(
                        "Dato invalido: " + ex.getMessage()
                    );
                }
            }

            System.out.println("Programa finalizado.");

        } catch (FinEntrada ex) {
            System.out.println(
                "\nFin de la entrada. Programa finalizado."
            );
        }
    }

    private void mostrarMenu() {
        System.out.println("\n1. Cargar estructura inicial");
        System.out.println("2. Mostrar jerarquia");
        System.out.println("3. Agregar unidad");
        System.out.println("4. Buscar unidad");
        System.out.println(
            "5. Consultar altura, hojas, nivel y total de libros"
        );
        System.out.println("6. Insertar libro en un estante");
        System.out.println("7. Buscar libro en un estante");
        System.out.println("8. Modificar libro de un estante");
        System.out.println("9. Eliminar libro de un estante");
        System.out.println(
            "10. Mostrar recorrido y traza de un estante"
        );
        System.out.println("11. Ejecutar retos de un estante");
        System.out.println(
            "12. Extra: reconstruir ABB desde preorden"
        );
        System.out.println("13. Ejecutar PruebasBiblioteca");
        System.out.println("0. Salir");
    }

    private void cargarInicial() {
        System.out.println(
            "Esta accion reemplaza la biblioteca actual y sus cambios."
        );

        String confirmacion = leerTexto(
            "Escribe SI para continuar: "
        ).trim();

        if (!"SI".equalsIgnoreCase(confirmacion)) {
            System.out.println("Carga cancelada.");
            return;
        }

        biblioteca = DatosPrueba.crearBibliotecaInicial();

        System.out.println(
            "Estructura inicial cargada: 7 unidades y 13 libros."
        );
    }

    private void agregarUnidad() {
        String padre = leerTexto("Codigo de la unidad padre: ");
        String codigo = leerTexto("Codigo de la nueva unidad: ");
        String nombre = leerTexto("Nombre: ");

        System.out.println("1. BIBLIOTECA  2. AREA  3. ESTANTE");

        TipoUnidad tipo;

        switch (leerEntero("Tipo: ")) {
            case 1:
                tipo = TipoUnidad.BIBLIOTECA;
                break;

            case 2:
                tipo = TipoUnidad.AREA;
                break;

            case 3:
                tipo = TipoUnidad.ESTANTE;
                break;

            default:
                System.out.println("Tipo inexistente.");
                return;
        }

        boolean agregada = biblioteca.agregar(
            padre,
            new UnidadBiblioteca(codigo, nombre, tipo)
        );

        System.out.println(
            agregada
                ? "Unidad agregada."
                : "No se agrego: revisa padre, jerarquia y codigo repetido."
        );
    }

    private void buscarUnidad() {
        NodoUnidad nodo = biblioteca.buscar(
            leerTexto("Codigo de unidad: ")
        );

        if (nodo == null) {
            System.out.println("Unidad no encontrada.");
            return;
        }

        UnidadBiblioteca unidad = nodo.getDato();

        System.out.println(
            unidad.getCodigo() + " | "
            + unidad.getNombre() + " | "
            + unidad.getTipo()
        );

        if (unidad.getTipo() == TipoUnidad.ESTANTE) {
            System.out.println(
                "Libros del estante: " + nodo.getCatalogo().contar()
            );
        }
    }

    private void consultarEstructura() {
        System.out.println(
            "Altura en aristas: " + biblioteca.altura()
        );
        System.out.println(
            "Hojas: " + biblioteca.contarHojas()
        );
        System.out.println(
            "Total de libros: " + biblioteca.contarLibrosTotal()
        );

        String codigo = leerTexto(
            "Codigo para consultar nivel: "
        );

        System.out.println(
            "Nivel: " + biblioteca.nivel(codigo)
            + " (raiz = 0; inexistente = -1)"
        );
    }

    private void insertarLibro() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero("Codigo entero positivo: ");
        String titulo = leerTexto("Titulo: ");
        String autor = leerTexto("Autor: ");
        int anio = leerEntero("Anio (1900-2026): ");

        boolean insertado = catalogo.insertar(
            new Libro(codigo, titulo, autor, anio)
        );

        System.out.println(
            insertado
                ? "Libro insertado."
                : "Ya existe un libro con ese codigo en el estante."
        );
    }

    private void buscarLibro() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        Libro libro = catalogo.buscar(
            leerEntero("Codigo del libro: ")
        );

        if (libro == null) {
            System.out.println("Libro no encontrado.");
        } else {
            mostrarLibro(libro);
        }
    }

    private void modificarLibro() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero(
            "Codigo del libro que se modifica: "
        );

        if (catalogo.buscar(codigo) == null) {
            System.out.println("Libro no encontrado.");
            return;
        }

        String titulo = leerTexto("Nuevo titulo: ");
        String autor = leerTexto("Nuevo autor: ");
        int anio = leerEntero("Nuevo anio (1900-2026): ");

        boolean modificado = catalogo.modificar(
            codigo, titulo, autor, anio
        );

        System.out.println(
            modificado
                ? "Libro modificado; se conserva su codigo."
                : "Libro no encontrado."
        );
    }

    private void eliminarLibro() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        int codigo = leerEntero(
            "Codigo del libro que se elimina: "
        );

        System.out.println(
            catalogo.eliminar(codigo)
                ? "Libro eliminado."
                : "Libro no encontrado."
        );
    }

    private void mostrarRecorrido() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        System.out.println("1. PREORDEN  2. INORDEN  3. POSORDEN");

        TipoRecorrido tipo;

        switch (leerEntero("Recorrido: ")) {
            case 1:
                tipo = TipoRecorrido.PREORDEN;
                break;

            case 2:
                tipo = TipoRecorrido.INORDEN;
                break;

            case 3:
                tipo = TipoRecorrido.POSORDEN;
                break;

            default:
                System.out.println("Recorrido inexistente.");
                return;
        }

        System.out.println(
            "Codigos: " + codigos(catalogo.recorrer(tipo))
        );

        System.out.println(catalogo.trazaRecorrido(tipo));
    }

    private void ejecutarReto() {
        CatalogoABB catalogo = seleccionarCatalogo();

        if (catalogo == null) {
            return;
        }

        System.out.println(
            "1. Contar en rango  "
            + "2. Camino de busqueda  "
            + "3. Validar ABB"
        );

        switch (leerEntero("Reto: ")) {
            case 1:
                int a = leerEntero("Limite inferior a: ");
                int b = leerEntero("Limite superior b: ");

                System.out.println(
                    "Cantidad en [" + a + ", " + b + "]: "
                    + catalogo.contarEnRango(a, b)
                );
                break;

            case 2:
                int codigo = leerEntero("Codigo buscado: ");

                System.out.println(
                    "Camino: " + catalogo.caminoBusqueda(codigo)
                );

                System.out.println(
                    catalogo.buscar(codigo) != null
                        ? "Libro encontrado."
                        : "Libro no encontrado."
                );
                break;

            case 3:
                System.out.println(
                    "Es ABB valido: " + catalogo.esABBValido()
                );
                break;

            default:
                System.out.println("Reto inexistente.");
        }
    }

    private void reconstruirDesdePreorden() {
        System.out.println(
            "Se construye un catalogo de demostracion "
            + "sin reemplazar los estantes."
        );

        String texto = leerTexto(
            "Codigos del preorden separados por espacios "
            + "(vacio = ABB vacio): "
        ).trim();

        String[] partes = texto.isEmpty()
                ? new String[0]
                : texto.split("\\s+");

        int[] preorden = new int[partes.length];

        try {
            for (int i = 0; i < partes.length; i++) {
                preorden[i] = Integer.parseInt(partes[i]);
            }
        } catch (NumberFormatException ex) {
            System.out.println(
                "Cada codigo debe ser un entero positivo."
            );
            return;
        }

        CatalogoABB catalogo = CatalogoABB.desdePreorden(preorden);

        System.out.println(
            "PREORDEN: "
            + codigos(catalogo.recorrer(TipoRecorrido.PREORDEN))
        );

        System.out.println(
            "INORDEN: "
            + codigos(catalogo.recorrer(TipoRecorrido.INORDEN))
        );

        System.out.println(
            "Es ABB valido: " + catalogo.esABBValido()
        );
    }

    private CatalogoABB seleccionarCatalogo() {
        NodoUnidad nodo = biblioteca.buscar(
            leerTexto("Codigo del estante: ")
        );

        if (nodo == null
                || nodo.getDato().getTipo() != TipoUnidad.ESTANTE) {
            System.out.println(
                "El codigo debe corresponder a un ESTANTE existente."
            );
            return null;
        }

        return nodo.getCatalogo();
    }

    private void mostrarLibro(Libro libro) {
        System.out.println(
            libro.getCodigo() + " | "
            + libro.getTitulo() + " | "
            + libro.getAutor() + " | "
            + libro.getAnio()
        );
    }

    private String codigos(List<Libro> libros) {
        StringBuilder texto = new StringBuilder();

        for (Libro libro : libros) {
            if (texto.length() > 0) {
                texto.append(' ');
            }

            texto.append(libro.getCodigo());
        }

        return texto.toString();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();

            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                System.out.println(
                    "Introduce un numero entero valido."
                );
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);

        if (!entrada.hasNextLine()) {
            throw new FinEntrada();
        }

        return entrada.nextLine();
    }

    private static final class FinEntrada extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}