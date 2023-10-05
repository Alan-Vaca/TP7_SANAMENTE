package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Mi_Comercio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_comercio);
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