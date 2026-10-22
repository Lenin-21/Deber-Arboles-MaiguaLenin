package enums;

public enum TipoUnidad {
    BIBLIOTECA,
    AREA,
    ESTANTE;

    public boolean puedeTenerHijo(TipoUnidad hijo) {
        if (this == BIBLIOTECA) {
            return hijo == AREA;
        }

        if (this == AREA) {
            return hijo == ESTANTE;
        }

        return false;
    }
}   