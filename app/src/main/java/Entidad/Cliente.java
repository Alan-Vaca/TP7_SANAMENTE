package Entidad;

import android.os.Parcel;
import android.os.Parcelable;

public class Cliente implements Parcelable {
    int idCliente;
    boolean estado;
    Usuario usuarioAsociado;

    public Cliente() {
        estado = true;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Usuario getUsuarioAsociado() {
        return usuarioAsociado;
    }

    public void setUsuarioAsociado(Usuario usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", estado=" + estado +
                ", usuarioAsociado=" + usuarioAsociado +
                '}';
    }

    // Implementación de Parcelable
    protected Cliente(Parcel in) {
        idCliente = in.readInt();
        estado = in.readByte() != 0;
        usuarioAsociado = in.readParcelable(Usuario.class.getClassLoader());
    }

    public static final Parcelable.Creator<Cliente> CREATOR = new Parcelable.Creator<Cliente>() {
        @Override
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        @Override
        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCliente);
        dest.writeByte((byte) (estado ? 1 : 0));
        dest.writeParcelable(usuarioAsociado, flags);
    }
}