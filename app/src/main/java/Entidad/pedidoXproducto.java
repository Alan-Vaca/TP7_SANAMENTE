package Entidad;

public class pedidoXproducto {
    int idPedidoXproducto;
    Producto producto;
    int cantidad;
    Pedido pedidoAsociado;

    public pedidoXproducto() {
    }

    public int getIdPedidoXproducto() {
        return idPedidoXproducto;
    }

    public void setIdPedidoXproducto(int idPedidoXproducto) {
        this.idPedidoXproducto = idPedidoXproducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Pedido getPedidoAsociado() {
        return pedidoAsociado;
    }

    public void setPedidoAsociado(Pedido pedidoAsociado) {
        this.pedidoAsociado = pedidoAsociado;
    }

    @Override
    public String toString() {
        return "pedidoXproducto{" +
                "idPedidoXproducto=" + idPedidoXproducto +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", pedidoAsociado=" + pedidoAsociado +
                '}';
    }
}
