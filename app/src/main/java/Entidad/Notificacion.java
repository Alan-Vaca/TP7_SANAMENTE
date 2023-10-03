package Entidad;

public class Notificacion {
    int idNotificacion;
    Usuario usuarioAsociado;

    //SON ATRIBUTOS QUE SE UTILIZARAN PARA EL ORDEN DE LOS FILTROS
    boolean pedidos;
    boolean productos;
    boolean ofertas;

    public Notificacion() {
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getUsuarioAsociado() {
        return usuarioAsociado;
    }

    public void setUsuarioAsociado(Usuario usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
    }

    public boolean isPedidos() {
        return pedidos;
    }

    public void setPedidos(boolean pedidos) {
        this.pedidos = pedidos;
    }

    public boolean isProductos() {
        return productos;
    }

    public void setProductos(boolean productos) {
        this.productos = productos;
    }

    public boolean isOfertas() {
        return ofertas;
    }

    public void setOfertas(boolean ofertas) {
        this.ofertas = ofertas;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", usuarioAsociado=" + usuarioAsociado +
                ", pedidos=" + pedidos +
                ", productos=" + productos +
                ", ofertas=" + ofertas +
                '}';
    }
}
