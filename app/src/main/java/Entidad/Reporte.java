package Entidad;

import java.util.ArrayList;

public class Reporte {


    String nombreProducto;
    int cantidadProducto;
    Usuario usuario;
    int cantidadUsuario;
    int cantidadPedidos;

    float montoTotal;
    int medioPago;
    int cantidadMedioPago;
    String descripcionPago;
    public Reporte() {
    }

    public float getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(float montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(int medioPago) {
        this.medioPago = medioPago;
    }


    public int getCantidadPedidos() {
        return cantidadPedidos;
    }

    public void setCantidadPedidos(int cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidad) {
        this.cantidadProducto = cantidad;
    }

    public String getnombreProducto() {
        return nombreProducto;
    }

    public void setnombreProducto(String nombre) {
        this.nombreProducto = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCantidadUsuario() {
        return cantidadUsuario;
    }

    public void setCantidadUsuario(int cantidadUsuario) {
        this.cantidadUsuario = cantidadUsuario;
    }

    public int getCantidadMedioPago() {
        return cantidadMedioPago;
    }

    public void setCantidadMedioPago(int cantidadMedioPago) {
        this.cantidadMedioPago = cantidadMedioPago;
    }

    public String getDescripcionPago() {
        return descripcionPago;
    }

    public void setDescripcionPago(String descripcionPago) {
        this.descripcionPago = descripcionPago;
    }


    @Override
    public String toString() {
        return "ReporteVentas:" +
                "PedidosTotales='" + cantidadPedidos + '\'' +
                "nombreProducto='" + nombreProducto + '\'' +
                ", cantidadProducto=" + cantidadProducto +
                ", usuarioNombre=" + usuario.getNombre() +
                ", usuarioApellido=" + usuario.getApellido() +
                ", nombreUsuario=" + usuario.getNombreUsuario() +
                ", usuarioDireccion=" + usuario.getDireccion() +
                ", cantidadUsuario=" + cantidadUsuario +
                ", montoTotal=" + montoTotal +
                ", descripcionPago='" + descripcionPago +
                ", cantidadMedioPago=" + cantidadMedioPago +
                 '\'' +
                '}';
    }
}
