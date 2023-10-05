package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class Registrar_Usuario extends AppCompatActivity {

    Switch esComerciante;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        esComerciante = (Switch)findViewById(R.id.swregistrarComoComerciante);
    }


    public void Continuar(View view) {
        boolean comerciante = esComerciante.isChecked();

        if (comerciante) {
            Intent registrarComerciante = new Intent(this, Registrar_Comercio.class);
            startActivity(registrarComerciante);
        } else {
            Intent registrarCliente = new Intent(this, Registrar_Cliente.class);
            startActivity(registrarCliente);
        }
    }

    public void CancelarRegistro(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }
}