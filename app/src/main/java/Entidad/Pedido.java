package Entidad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Pedido implements Parcelable{
    int idPedido;
    Float monto;
    Date fecha;
    int estado; //(1 entregado, 2 cancelado, 3 confirmado)
    int medioPago;
    Cliente cliente;
    Comercio comercio;

    String motivoCancelacion;

    public Pedido() {
        idPedido = 0; // Inicializa idPedido con un valor adecuado
        monto = 0.0f; // Inicializa monto con un valor adecuado
        fecha = new Date(); // Inicializa fecha con un valor adecuado
        estado = 0; // Inicializa estado con un valor adecuado
        medioPago = 0; // Inicializa medioPago con un valor adecuado
        cliente = new Cliente(); // Inicializa cliente con un objeto Cliente adecuado
        comercio = new Comercio(); // Inicializa comercio con un objeto Comercio adecuado
        motivoCancelacion = ""; // Inicializa el posible motivo de cancelacion
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

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
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



    protected Pedido(Parcel in) {
        idPedido = in.readInt();
        monto = in.readFloat();
        fecha = (Date) in.readSerializable(); // Lee la fecha como serializable
        estado = in.readInt();
        medioPago = in.readInt();
        cliente = in.readParcelable(Cliente.class.getClassLoader()); // Lee un objeto Cliente
        comercio = in.readParcelable(Comercio.class.getClassLoader()); // Lee un objeto Comercio
        motivoCancelacion = in.readString();
    }

    public static final Parcelable.Creator<Pedido> CREATOR = new Parcelable.Creator<Pedido>() {
        @Override
        public Pedido createFromParcel(Parcel in) {
            return new Pedido(in);
        }

        @Override
        public Pedido[] newArray(int size) {
            return new Pedido[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPedido);
        dest.writeFloat(monto);
        dest.writeSerializable(fecha); // Escribe la fecha como serializable
        dest.writeInt(estado);
        dest.writeInt(medioPago);
        dest.writeParcelable(cliente, flags); // Escribe el objeto Cliente
        dest.writeParcelable(comercio, flags);
        dest.writeString(motivoCancelacion);
    }

}
