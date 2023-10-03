package Entidad;

public class CalificacionXcliente {
    int idCalificacion;
    float calificacion; // 0- 0.5- 1- 1.5- 2- 2.5- 3- 3.5- 4- 4.5- 5
    String comentario;
    Cliente cliente;

    public CalificacionXcliente() {
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "CalificacionXcliente{" +
                "idCalificacion=" + idCalificacion +
                ", calificacion=" + calificacion +
                ", comentario='" + comentario + '\'' +
                ", cliente=" + cliente +
                '}';
    }
}
