package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarProducto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
    }

    public void MenuHistorial(View view) {
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }
}