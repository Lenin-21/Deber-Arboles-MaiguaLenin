package negocio;

import modelo.Libro;

public class NodoLibro {
    private Libro dato;
    private NodoLibro izquierdo;
    private NodoLibro derecho;

    public NodoLibro(Libro dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                "El libro del nodo no puede ser null."
            );
        }

        this.dato = dato;
    }

    public Libro getDato() {
        return dato;
    }

    public void setDato(Libro dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                "El libro del nodo no puede ser null."
            );
        }

        this.dato = dato;
    }

    public NodoLibro getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoLibro izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoLibro getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoLibro derecho) {
        this.derecho = derecho;
    }
}