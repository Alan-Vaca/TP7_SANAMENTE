package Entidad;

import android.os.Parcel;
import android.os.Parcelable;

public class Comercio implements Parcelable {
    int idComercio;
    int cuit;
    String nombreComercio;
    int dias; // NOSE MUY BIEN QUE HACE
    String horarios;
    boolean estado;
    Usuario usuarioAsociado;

    public Comercio() {
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public int getCuit() {
        return cuit;
    }

    public void setCuit(int cuit) {
        this.cuit = cuit;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
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
        return "Comercio{" +
                "idComercio=" + idComercio +
                ", cuit=" + cuit +
                ", nombreComercio='" + nombreComercio + '\'' +
                ", dias=" + dias +
                ", horarios='" + horarios + '\'' +
                ", estado=" + estado +
                ", usuarioAsociado=" + usuarioAsociado +
                '}';
    }

    // Implementación de Parcelable
    protected Comercio(Parcel in) {
        idComercio = in.readInt();
        cuit = in.readInt();
        nombreComercio = in.readString();
        dias = in.readInt();
        horarios = in.readString();
        estado = in.readByte() != 0;
        usuarioAsociado = in.readParcelable(Usuario.class.getClassLoader());
    }

    public static final Parcelable.Creator<Comercio> CREATOR = new Parcelable.Creator<Comercio>() {
        @Override
        public Comercio createFromParcel(Parcel in) {
            return new Comercio(in);
        }

        @Override
        public Comercio[] newArray(int size) {
            return new Comercio[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idComercio);
        dest.writeInt(cuit);
        dest.writeString(nombreComercio);
        dest.writeInt(dias);
        dest.writeString(horarios);
        dest.writeByte((byte) (estado ? 1 : 0));
        dest.writeParcelable(usuarioAsociado, flags);
    }
}