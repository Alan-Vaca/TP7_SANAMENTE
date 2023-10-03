package Entidad;

import java.util.Date;

public class Historial {
    int idHistorial;
    Cliente clientePedido;
    Pedido pedidoRealizado;
    Date fecha;
    int estado; //(1 entregado, 2 cancelado, 3 confirmado)

    public Historial() {
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Cliente getClientePedido() {
        return clientePedido;
    }

    public void setClientePedido(Cliente clientePedido) {
        this.clientePedido = clientePedido;
    }

    public Pedido getPedidoRealizado() {
        return pedidoRealizado;
    }

    public void setPedidoRealizado(Pedido pedidoRealizado) {
        this.pedidoRealizado = pedidoRealizado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Historial{" +
                "idHistorial=" + idHistorial +
                ", clientePedido=" + clientePedido +
                ", pedidoRealizado=" + pedidoRealizado +
                ", fecha=" + fecha +
                ", estado=" + estado +
                '}';
    }
}
