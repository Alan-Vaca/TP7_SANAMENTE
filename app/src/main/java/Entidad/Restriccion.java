package Entidad;

public class Restriccion {
    int idRestriccion;
    Cliente clienteAsociado;

    //SE APLICARAN COMO FILTROS PARA LOS PRODUCTOS
    boolean hipertenso;
    boolean diabetico;
    boolean celiaco;
    String alergico;

    public Restriccion() {
    }

    public int getIdRestriccion() {
        return idRestriccion;
    }

    public void setIdRestriccion(int idRestriccion) {
        this.idRestriccion = idRestriccion;
    }

    public Cliente getClienteAsociado() {
        return clienteAsociado;
    }

    public void setClienteAsociado(Cliente clienteAsociado) {
        this.clienteAsociado = clienteAsociado;
    }

    public boolean isHipertenso() {
        return hipertenso;
    }

    public void setHipertenso(boolean hipertenso) {
        this.hipertenso = hipertenso;
    }

    public boolean isDiabetico() {
        return diabetico;
    }

    public void setDiabetico(boolean diabetico) {
        this.diabetico = diabetico;
    }

    public boolean isCeliaco() {
        return celiaco;
    }

    public void setCeliaco(boolean celiaco) {
        this.celiaco = celiaco;
    }

    public String getAlergico() {
        return alergico;
    }

    public void setAlergico(String alergico) {
        this.alergico = alergico;
    }

    @Override
    public String toString() {
        return "Restriccion{" +
                "idRestriccion=" + idRestriccion +
                ", clienteAsociado=" + clienteAsociado +
                ", hipertenso=" + hipertenso +
                ", diabetico=" + diabetico +
                ", celiaco=" + celiaco +
                ", alergico='" + alergico + '\'' +
                '}';
    }
}
