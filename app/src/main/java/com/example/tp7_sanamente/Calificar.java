package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Calificar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);
    }

    public void NuevaCalificacion(View view) {
        Intent NuevaCalificacion = new Intent(this, Detalle_Pedido.class);
        startActivity(NuevaCalificacion);
    }

    public void CancelarCalificacion(View view) {
        Intent NuevaCalificacion = new Intent(this, Detalle_Pedido.class);
        startActivity(NuevaCalificacion);
    }
}