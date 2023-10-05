package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Registrar_Comercio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comercio);
    }

    public void IngresarComercio(View view) {
        Intent IngresarComercio = new Intent(this, MainActivity.class);
        startActivity(IngresarComercio);
    }
    public void VolverRegistro(View view) {
        Intent VolverRegistro = new Intent(this, Registrar_Usuario.class);
        startActivity(VolverRegistro);
    }
}