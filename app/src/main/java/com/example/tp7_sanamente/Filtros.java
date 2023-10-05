package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Filtros extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);
    }


    public void FiltrosAplicarFiltros(View view) {
        Intent filtrosAplicarFiltros = new Intent(this, Mis_Productos.class);
        startActivity(filtrosAplicarFiltros);
    }
}