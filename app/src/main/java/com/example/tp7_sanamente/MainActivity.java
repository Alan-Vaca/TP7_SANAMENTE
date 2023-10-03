package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import Entidad.Usuario;

public class MainActivity extends AppCompatActivity {

    Switch esComerciante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        esComerciante = (Switch)findViewById(R.id.sw_comerciante);

    }


    public void Registrarse(View view){
        Intent registrarse = new Intent(this, Registrar_Cliente.class);
        startActivity(registrarse);
    }

    public void Ingresar(View view) {

        Usuario usuario = new Usuario();


        boolean comerciante = esComerciante.isChecked();

        if (!comerciante) {
            Intent ingresarcliente = new Intent(this, Menu_Cliente.class);
            startActivity(ingresarcliente);
        } else {
            Intent ingresarcomercio = new Intent(this, MenuComercio.class);
            startActivity(ingresarcomercio);
        }
    }
}