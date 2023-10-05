package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Mi_Usuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario);
    }

    public void MenuModificarUsuario(View view) {
        Intent menuModificarUsuario = new Intent(this, Modificar_Usuario.class);
        startActivity(menuModificarUsuario);
    }


    public void MenuCliente(View view) {
        Intent menuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(menuCliente);
    }


}