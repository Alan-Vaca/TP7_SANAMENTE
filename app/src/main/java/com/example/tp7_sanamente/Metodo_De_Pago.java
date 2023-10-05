package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Metodo_De_Pago extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);
    }


    public void MetodoDePagoFinalizarCompra(View view) {
        Intent metodoDePagoFinalizarCompra = new Intent(this, MiHistorial.class);
        startActivity(metodoDePagoFinalizarCompra);
    }

    public void MetodoDePagoCancelarCompra(View view) {
        Intent metodoDePagoCancelarCompra = new Intent(this, MiCarritoCompras.class);
        startActivity(metodoDePagoCancelarCompra);
    }
}