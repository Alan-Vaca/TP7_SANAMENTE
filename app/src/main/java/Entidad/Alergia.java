package Entidad;

public class Alergia {
    int idAlergia;
    String descripcionAlergia;
    String ingredientesAlergicos;
    public Alergia() {
        this.idAlergia = 0;
        this.descripcionAlergia = "";
        this.ingredientesAlergicos = "";
    }

    public int getIdAlergia() {
        return idAlergia;
    }

    public void setIdAlergia(int idEtiquetado) {
        this.idAlergia = idEtiquetado;
    }

    public String getDescripcionAlergia() {
        return descripcionAlergia;
    }

    public String getIngredientesAlergicos() {
        return ingredientesAlergicos;
    }

    public void setIngredientesAlergicos(String ingredientesAlergicos) {
        this.ingredientesAlergicos = ingredientesAlergicos;
    }

    public void setDescripcionAlergia(String descripcionAlergia) {
        this.descripcionAlergia = descripcionAlergia;
    }

    @Override
    public String toString() {
        return descripcionAlergia;
    }
}
