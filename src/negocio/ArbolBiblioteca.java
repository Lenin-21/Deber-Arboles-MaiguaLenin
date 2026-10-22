package negocio;

import enums.TipoUnidad;
import java.util.Locale;
import modelo.UnidadBiblioteca;

public final class ArbolBiblioteca {
    private final NodoUnidad raiz;

    public ArbolBiblioteca(UnidadBiblioteca raiz) {
        if (raiz == null || raiz.getTipo() != TipoUnidad.BIBLIOTECA) {
            throw new IllegalArgumentException(
                "La raiz debe ser de tipo BIBLIOTECA."
            );
        }

        this.raiz = new NodoUnidad(raiz);
    }

    public NodoUnidad getRaiz() {
        return raiz;
    }

    // AGREGAR UNIDADES

    public boolean agregar(String codigoPadre, UnidadBiblioteca nueva) {
        if (nueva == null || buscar(nueva.getCodigo()) != null) {
            return false;
        }

        NodoUnidad padre = buscar(codigoPadre);

        if (padre == null
                || !padre.getDato().getTipo().puedeTenerHijo(
                    nueva.getTipo()
                )) {
            return false;
        }

        padre.agregarHijo(new NodoUnidad(nueva));
        return true;
    }

    // BUSCAR UNIDADES

    public NodoUnidad buscar(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        return buscar(
            raiz,
            codigo.trim().toUpperCase(Locale.ROOT)
        );
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {
        if (nodo.getDato().getCodigo().equals(codigo)) {
            return nodo;
        }

        return buscarEnHijos(nodo, codigo, 0);
    }

    private NodoUnidad buscarEnHijos(
        NodoUnidad padre,
        String codigo,
        int indice
    ) {
        if (indice == padre.getHijos().size()) {
            return null;
        }

        NodoUnidad encontrado = buscar(
            padre.getHijos().get(indice),
            codigo
        );

        if (encontrado != null) {
            return encontrado;
        }

        return buscarEnHijos(padre, codigo, indice + 1);
    }

    // ALTURA EN ARISTAS

    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoUnidad nodo) {
        return alturaHijos(nodo, 0, 0);
    }

    private int alturaHijos(
        NodoUnidad padre,
        int indice,
        int maxima
    ) {
        if (indice == padre.getHijos().size()) {
            return maxima;
        }

        int candidata = 1 + altura(padre.getHijos().get(indice));

        return alturaHijos(
            padre,
            indice + 1,
            Math.max(maxima, candidata)
        );
    }

    // CONTAR HOJAS

    public int contarHojas() {
        return contarHojas(raiz);
    }

    private int contarHojas(NodoUnidad nodo) {
        if (nodo.getHijos().isEmpty()) {
            return 1;
        }

        return contarHojasHijos(nodo, 0);
    }

    private int contarHojasHijos(NodoUnidad padre, int indice) {
        if (indice == padre.getHijos().size()) {
            return 0;
        }

        return contarHojas(padre.getHijos().get(indice))
             + contarHojasHijos(padre, indice + 1);
    }

    // NIVEL DE UNA UNIDAD

    public int nivel(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return -1;
        }

        return nivel(
            raiz,
            codigo.trim().toUpperCase(Locale.ROOT),
            0
        );
    }

    private int nivel(
        NodoUnidad nodo,
        String codigo,
        int nivelActual
    ) {
        if (nodo.getDato().getCodigo().equals(codigo)) {
            return nivelActual;
        }

        return nivelEnHijos(nodo, codigo, nivelActual, 0);
    }

    private int nivelEnHijos(
        NodoUnidad padre,
        String codigo,
        int nivelPadre,
        int indice
    ) {
        if (indice == padre.getHijos().size()) {
            return -1;
        }

        int encontrado = nivel(
            padre.getHijos().get(indice),
            codigo,
            nivelPadre + 1
        );

        if (encontrado >= 0) {
            return encontrado;
        }

        return nivelEnHijos(
            padre,
            codigo,
            nivelPadre,
            indice + 1
        );
    }

    // MOSTRAR JERARQUIA EN PREORDEN

    public String mostrarJerarquia() {
        StringBuilder salida = new StringBuilder();

        mostrarJerarquia(raiz, 0, salida);
        salida.deleteCharAt(salida.length() - 1);

        return salida.toString();
    }

    private void mostrarJerarquia(
        NodoUnidad nodo,
        int nivel,
        StringBuilder salida
    ) {
        agregarSangria(nivel, salida);

        salida.append(nodo.getDato().getCodigo())
              .append(' ')
              .append(nodo.getDato().getNombre())
              .append('\n');

        mostrarHijos(nodo, nivel, 0, salida);
    }

    private void agregarSangria(int niveles, StringBuilder salida) {
        if (niveles > 0) {
            salida.append("  ");
            agregarSangria(niveles - 1, salida);
        }
    }

    private void mostrarHijos(
        NodoUnidad padre,
        int nivelPadre,
        int indice,
        StringBuilder salida
    ) {
        if (indice < padre.getHijos().size()) {
            mostrarJerarquia(
                padre.getHijos().get(indice),
                nivelPadre + 1,
                salida
            );

            mostrarHijos(padre, nivelPadre, indice + 1, salida);
        }
    }

    // TOTAL DE LIBROS DE TODOS LOS ESTANTES

    public int contarLibrosTotal() {
        return contarLibrosTotal(raiz);
    }

    private int contarLibrosTotal(NodoUnidad nodo) {
        int librosPropios = nodo.getCatalogo() == null
                ? 0
                : nodo.getCatalogo().contar();

        return librosPropios + contarLibrosHijos(nodo, 0);
    }

    private int contarLibrosHijos(NodoUnidad padre, int indice) {
        if (indice == padre.getHijos().size()) {
            return 0;
        }

        return contarLibrosTotal(padre.getHijos().get(indice))
             + contarLibrosHijos(padre, indice + 1);
    }
}