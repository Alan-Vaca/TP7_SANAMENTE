package Entidad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Historial {
    int idHistorial;
    Cliente clientePedido;
    Pedido pedidoRealizado;
    Date fecha;
    int estado;

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

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaFormateada = formatoFecha.format(fecha);
        return "Pedido N°" + getPedidoRealizado().getIdPedido() + " - " + fechaFormateada + " - " + getEstadoString();
    }
}
