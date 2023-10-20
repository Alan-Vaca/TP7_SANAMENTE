package Entidad;

public class Etiquetado {
    int idEtiquetado;
    String descripcion;
    boolean estado;

    public Etiquetado() {
    }

    public int getIdEtiquetado() {
        return idEtiquetado;
    }

    public void setIdEtiquetado(int idEtiquetado) {
        this.idEtiquetado = idEtiquetado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
