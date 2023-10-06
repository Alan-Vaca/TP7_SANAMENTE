package Entidad;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable{
    int idUsuario;
    String nombreUsuario;
    String nombre;
    String Apellido;
    String Contraseña;
    int DNI;
    String Direccion;
    boolean Estado;

    boolean Cliente;

    public Usuario() {
        Estado = true;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }

    public int getDNI() {
        return DNI;
    }

    public void setDNI(int DNI) {
        this.DNI = DNI;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public boolean isEstado() {
        return Estado;
    }

    public void setEstado(boolean estado) {
        Estado = estado;
    }

    public boolean isCliente() {
        return Cliente;
    }

    public void setCliente(boolean cliente) {
        Cliente = cliente;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                ", Contraseña='" + Contraseña + '\'' +
                ", DNI=" + DNI +
                ", Direccion='" + Direccion + '\'' +
                ", Estado=" + Estado +
                '}';
    }




    protected Usuario(Parcel in) {
        idUsuario = in.readInt();
        nombreUsuario = in.readString();
        nombre = in.readString();
        Apellido = in.readString();
        Contraseña = in.readString();
        DNI = in.readInt();
        Direccion = in.readString();
        Estado = in.readByte() != 0;
        Cliente = in.readByte() != 0;
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeString(nombreUsuario);
        dest.writeString(nombre);
        dest.writeString(Apellido);
        dest.writeString(Contraseña);
        dest.writeInt(DNI);
        dest.writeString(Direccion);
        dest.writeByte((byte) (Estado ? 1 : 0));
        dest.writeByte((byte) (Cliente ? 1 : 0));
    }
}
