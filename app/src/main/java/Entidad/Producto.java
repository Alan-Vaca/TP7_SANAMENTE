package Entidad;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable{
    int idProducto;
    String ingredientes;
    int stock;
    float precio;
    String nombre;
    int idComercio;

    boolean estado;



    public Producto() {
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre.toUpperCase() + " - $" + precio;
    }


    public String EstadoString(){
        if(isEstado()){
            return "ACTIVO";
        }else{
            return "INACTIVO";
        }
    }




    protected Producto(Parcel in) {
        idProducto = in.readInt();
        ingredientes = in.readString();
        nombre = in.readString();
        stock = in.readInt();
        idComercio = in.readInt();
        estado = in.readByte() != 0;
        precio = in.readFloat();
    }

    public static final Parcelable.Creator<Producto> CREATOR = new Parcelable.Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idProducto);
        dest.writeString(ingredientes);
        dest.writeString(nombre);
        dest.writeFloat(precio);
        dest.writeInt(stock);
        dest.writeInt(idComercio);
        dest.writeByte((byte) (estado ? 1 : 0));
    }

}

