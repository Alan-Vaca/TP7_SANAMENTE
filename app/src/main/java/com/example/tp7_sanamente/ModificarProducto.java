package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ModificarProducto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);
    }

    public void ModificarProductoCancelar(View view) {
        Intent modificarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(modificarProductoCancelar);
    }

    public void ModificarProductoModificar(View view) {
        Intent modificarProductoModificar = new Intent(this, Mis_Productos.class);
        startActivity(modificarProductoModificar);
    }

}