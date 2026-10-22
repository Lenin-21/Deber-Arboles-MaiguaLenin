package negocio;

import enums.TipoRecorrido;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;

public class CatalogoABB {
    private NodoLibro raiz;

    public NodoLibro getRaiz() {
        return raiz;
    }

    public void setRaiz(NodoLibro raiz) {
        this.raiz = raiz;
    }

    // INSERTAR

    public boolean insertar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException(
                    "El libro no puede ser null.");
        }

        if (buscar(libro.getCodigo()) != null) {
            return false;
        }

        raiz = insertar(raiz, libro);
        return true;
    }

    private NodoLibro insertar(NodoLibro nodo, Libro libro) {
        if (nodo == null) {
            return new NodoLibro(libro);
        }

        if (libro.getCodigo() < nodo.getDato().getCodigo()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), libro));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), libro));
        }

        return nodo;
    }

    // BUSCAR

    public Libro buscar(int codigo) {
        return buscar(raiz, codigo);
    }

    private Libro buscar(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;
        }

        int actual = nodo.getDato().getCodigo();

        if (codigo == actual) {
            return nodo.getDato();
        }

        if (codigo < actual) {
            return buscar(nodo.getIzquierdo(), codigo);
        }

        return buscar(nodo.getDerecho(), codigo);
    }

    // CONTAR LIBROS

    public int contar() {
        return contar(raiz);
    }

    private int contar(NodoLibro nodo) {
        if (nodo == null) {
            return 0;
        }

        return 1 + contar(nodo.getIzquierdo())
                + contar(nodo.getDerecho());
    }

    // RECORRIDOS

    public List<Libro> recorrer(TipoRecorrido tipo) {
        validarTipo(tipo);

        List<Libro> libros = new ArrayList<Libro>();
        recorrer(raiz, tipo, libros);

        return libros;
    }

    private void recorrer(
            NodoLibro nodo,
            TipoRecorrido tipo,
            List<Libro> libros) {
        if (nodo == null) {
            return;
        }

        if (tipo == TipoRecorrido.PREORDEN) {
            libros.add(nodo.getDato());
        }

        recorrer(nodo.getIzquierdo(), tipo, libros);

        if (tipo == TipoRecorrido.INORDEN) {
            libros.add(nodo.getDato());
        }

        recorrer(nodo.getDerecho(), tipo, libros);

        if (tipo == TipoRecorrido.POSORDEN) {
            libros.add(nodo.getDato());
        }
    }

    // TRAZA DE LOS RECORRIDOS

    public String trazaRecorrido(TipoRecorrido tipo) {
        validarTipo(tipo);

        StringBuilder traza = new StringBuilder();
        StringBuilder secuencia = new StringBuilder();

        trazar(raiz, tipo, 0, traza, secuencia);

        traza.append("Secuencia ").append(tipo).append(":");

        if (secuencia.length() > 0) {
            traza.append(" ").append(secuencia);
        }

        return traza.toString();
    }

    private void trazar(
            NodoLibro nodo,
            TipoRecorrido tipo,
            int nivel,
            StringBuilder traza,
            StringBuilder secuencia) {
        if (nodo == null) {
            indentar(traza, nivel);
            traza.append("null -> regresa\n");
            return;
        }

        indentar(traza, nivel);
        traza.append("entra ")
                .append(nodo.getDato().getCodigo())
                .append('\n');

        if (tipo == TipoRecorrido.PREORDEN) {
            visitar(nodo, nivel, traza, secuencia);
        }

        trazar(nodo.getIzquierdo(), tipo, nivel + 1, traza, secuencia);

        if (tipo == TipoRecorrido.INORDEN) {
            visitar(nodo, nivel, traza, secuencia);
        }

        trazar(nodo.getDerecho(), tipo, nivel + 1, traza, secuencia);

        if (tipo == TipoRecorrido.POSORDEN) {
            visitar(nodo, nivel, traza, secuencia);
        }
    }

    private void visitar(
            NodoLibro nodo,
            int nivel,
            StringBuilder traza,
            StringBuilder secuencia) {
        int codigo = nodo.getDato().getCodigo();

        indentar(traza, nivel);
        traza.append("VISITA ").append(codigo).append('\n');

        if (secuencia.length() > 0) {
            secuencia.append(' ');
        }

        secuencia.append(codigo);
    }

    private void indentar(StringBuilder texto, int nivel) {
        if (nivel == 0) {
            return;
        }

        texto.append("  ");
        indentar(texto, nivel - 1);
    }

    private void validarTipo(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de recorrido no puede ser null.");
        }
    }

    // MODIFICAR

    public boolean modificar(
            int codigo,
            String titulo,
            String autor,
            int anio) {
        Libro libro = buscar(codigo);

        if (libro == null) {
            return false;
        }

        // Valida todos los datos antes de aplicar cambios.
        Libro datosValidados = new Libro(codigo, titulo, autor, anio);

        libro.setTitulo(datosValidados.getTitulo());
        libro.setAutor(datosValidados.getAutor());
        libro.setAnio(datosValidados.getAnio());

        return true;
    }

    // ELIMINAR

    public boolean eliminar(int codigo) {
        if (buscar(codigo) == null) {
            return false;
        }

        raiz = eliminar(raiz, codigo);
        return true;
    }

    private NodoLibro eliminar(NodoLibro nodo, int codigo) {
        if (nodo == null) {
            return null;
        }

        int actual = nodo.getDato().getCodigo();

        if (codigo < actual) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), codigo));
        } else if (codigo > actual) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), codigo));
        } else {
            // Hoja o nodo con solamente hijo derecho.
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }

            // Nodo con solamente hijo izquierdo.
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }

            // Dos hijos: usar el sucesor inorden.
            NodoLibro sucesor = minimo(nodo.getDerecho());

            nodo.setDato(sucesor.getDato());

            nodo.setDerecho(
                    eliminar(nodo.getDerecho(), sucesor.getDato().getCodigo()));
        }

        return nodo;
    }

    private NodoLibro minimo(NodoLibro nodo) {
        if (nodo.getIzquierdo() == null) {
            return nodo;
        }

        return minimo(nodo.getIzquierdo());
    }

    // RETO 1: CONTAR EN UN RANGO INCLUSIVO CON PODA

    public int contarEnRango(int a, int b) {
        if (a > b) {
            return 0;
        }

        return contarEnRango(raiz, a, b);
    }

    private int contarEnRango(NodoLibro nodo, int a, int b) {
        if (nodo == null) {
            return 0;
        }

        int codigo = nodo.getDato().getCodigo();

        if (codigo < a) {
            return contarEnRango(nodo.getDerecho(), a, b);
        }

        if (codigo > b) {
            return contarEnRango(nodo.getIzquierdo(), a, b);
        }

        return 1 + contarEnRango(nodo.getIzquierdo(), a, b)
                + contarEnRango(nodo.getDerecho(), a, b);
    }

    // RETO 2: CAMINO DE BUSQUEDA

    public List<Integer> caminoBusqueda(int codigo) {
        List<Integer> camino = new ArrayList<Integer>();
        caminoBusqueda(raiz, codigo, camino);

        return camino;
    }

    private void caminoBusqueda(
            NodoLibro nodo,
            int codigo,
            List<Integer> camino) {
        if (nodo == null) {
            return;
        }

        int actual = nodo.getDato().getCodigo();
        camino.add(actual);

        if (codigo < actual) {
            caminoBusqueda(nodo.getIzquierdo(), codigo, camino);
        } else if (codigo > actual) {
            caminoBusqueda(nodo.getDerecho(), codigo, camino);
        }
    }

    // RETO 3: VALIDAR EL ABB CON LIMITES GLOBALES

    public boolean esABBValido() {
        return esABBValido(raiz, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean esABBValido(
            NodoLibro nodo,
            long minimo,
            long maximo) {
        if (nodo == null) {
            return true;
        }

        int codigo = nodo.getDato().getCodigo();

        return codigo > minimo
                && codigo < maximo
                && esABBValido(nodo.getIzquierdo(), minimo, codigo)
                && esABBValido(nodo.getDerecho(), codigo, maximo);
    }

    // EXTRA OPCIONAL: RECONSTRUIR DESDE PREORDEN

    public static CatalogoABB desdePreorden(int[] preorden) {
        if (preorden == null) {
            throw new IllegalArgumentException(
                    "El preorden no puede ser null.");
        }

        int[] indice = { 0 };
        CatalogoABB catalogo = new CatalogoABB();

        catalogo.raiz = construirDesdePreorden(
                preorden, indice, 0L, Long.MAX_VALUE);

        if (indice[0] != preorden.length) {
            throw new IllegalArgumentException(
                    "La secuencia no es un preorden valido "
                            + "con codigos positivos y unicos.");
        }

        return catalogo;
    }

    private static NodoLibro construirDesdePreorden(
            int[] preorden,
            int[] indice,
            long minimo,
            long maximo) {
        if (indice[0] == preorden.length) {
            return null;
        }

        int codigo = preorden[indice[0]];

        if (codigo <= minimo || codigo >= maximo) {
            return null;
        }

        indice[0]++;

        // La entrada solo contiene codigos: se crean datos de ejemplo.
        Libro libro = new Libro(
                codigo,
                "Libro " + codigo,
                "Autor no especificado",
                2026);

        NodoLibro nodo = new NodoLibro(libro);

        nodo.setIzquierdo(
                construirDesdePreorden(preorden, indice, minimo, codigo));

        nodo.setDerecho(
                construirDesdePreorden(preorden, indice, codigo, maximo));

        return nodo;
    }
}