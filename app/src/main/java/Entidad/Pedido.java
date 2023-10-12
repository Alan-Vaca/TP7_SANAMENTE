package Entidad;

import java.util.Date;

public class Pedido {
    int idPedido;
    Float monto;
    Date fecha;
    int estado; //(1 entregado, 2 cancelado, 3 confirmado)
    int medioPago;
    Cliente cliente;
    Comercio comercio;

    public Pedido() {;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return this.estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(int medioPago) {
        this.medioPago = medioPago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Comercio getComercio() {
        return comercio;
    }

    public void setComercio(Comercio comercio) {
        this.comercio = comercio;
    }

    public String getEstadoString(){
        if(this.estado == 1){
            return "PENDIENTE";
        } else if(this.estado == 2){
            return "ENTREGADO";
        } else if (this.estado == 3) {
            return  "CONFIRMADO";
        }else{
            return "CANCELADO";
        }
    }

    @Override
    public String toString() {
        return "N°" + idPedido + " - " + getEstadoString() + " - " + "$" + monto + " - FECHA: " + fecha.toString();
    }
}
