package Entidad;

public class Comercio {
    int idComercio;
    int cuit;
    String nombreComercio;
    int dias; //NOSE MUY BIEN QUE HACE
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
}
