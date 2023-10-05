package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MiUsuarioComercio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario_comercio);
    }

    public void MenuComercio(View view) {
        Intent menuComercio = new Intent(this, MenuComercio.class);
        startActivity(menuComercio);
    }

    public void MenuMi_Comercio(View view) {
        Intent MenuMi_Comercio = new Intent(this, Mi_Comercio.class);
        startActivity(MenuMi_Comercio);
    }

}