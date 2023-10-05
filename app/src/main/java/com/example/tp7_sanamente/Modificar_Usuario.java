package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Modificar_Usuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario);
    }

    public void MenuMiUsuario(View view) {
        Intent menuMiUsuario = new Intent(this, Mi_Usuario.class);
        startActivity(menuMiUsuario);
    }
}