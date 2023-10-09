package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import Entidad.Comercio;
import Entidad.Usuario;

public class Mi_Comercio extends AppCompatActivity {

    Comercio comercio;
    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_comercio);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        comercio = new Comercio();
        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            comercio.setUsuarioAsociado(user);
        }else{
            Toast.makeText(Mi_Comercio.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }


    public void MenuMiUsuarioComercio(View view) {
        Intent menuMiUsuarioComercio = new Intent(this, MiUsuarioComercio.class);
        startActivity(menuMiUsuarioComercio);
    }

    public void MenuComercio(View view) {
        Intent menuComercio = new Intent(this, MenuComercio.class);
        startActivity(menuComercio);
    }


}