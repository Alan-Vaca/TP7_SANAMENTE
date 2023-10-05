package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Mis_Productos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);
    }

    public void CatalogoVerProducto(View view) {
        Intent catalogoVerProducto = new Intent(this, ModificarProducto.class);
        startActivity(catalogoVerProducto);
    }

    public void CatalogoFiltros(View view) {
        Intent catalogoFiltros = new Intent(this, Filtros.class);
        startActivity(catalogoFiltros);
    }

    public void CatalogoAgregarNuevoProducto(View view) {
        Intent catalogoAgregarNuevoProducto = new Intent(this, AgregarProducto.class);
        startActivity(catalogoAgregarNuevoProducto);
    }
}