package negocio;

import enums.TipoUnidad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.UnidadBiblioteca;

public final class NodoUnidad {
    private final UnidadBiblioteca dato;
    private final List<NodoUnidad> hijos;
    private final List<NodoUnidad> vistaHijos;
    private final CatalogoABB catalogo;

    public NodoUnidad(UnidadBiblioteca dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                "La unidad del nodo es obligatoria."
            );
        }

        this.dato = dato;
        this.hijos = new ArrayList<NodoUnidad>();
        this.vistaHijos = Collections.unmodifiableList(hijos);

        this.catalogo = dato.getTipo() == TipoUnidad.ESTANTE
                ? new CatalogoABB()
                : null;
    }

    public UnidadBiblioteca getDato() {
        return dato;
    }

    public List<NodoUnidad> getHijos() {
        return vistaHijos;
    }

    public CatalogoABB getCatalogo() {
        return catalogo;
    }

    void agregarHijo(NodoUnidad hijo) {
        if (hijo == null
                || !dato.getTipo().puedeTenerHijo(
                    hijo.getDato().getTipo()
                )) {
            throw new IllegalArgumentException(
                "Relacion entre unidades no permitida."
            );
        }

        hijos.add(hijo);
    }
}