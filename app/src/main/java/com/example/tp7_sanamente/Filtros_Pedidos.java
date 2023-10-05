package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Filtros_Pedidos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_pedidos);
    }

    /*SE APLICARA DEPENDIENDO SI ES UN PEDIDO O UN HISTORIAL*/
    public void AplicarFiltroHistorial(View view) {
        //AplicarFiltroPedido(view);
        Intent AplicarFiltroHistorial = new Intent(this, MiHistorial.class);
        startActivity(AplicarFiltroHistorial);
    }

    public void AplicarFiltroPedido(View view) {
        Intent AplicarFiltroPedido = new Intent(this, Mis_Pedidos.class);
        startActivity(AplicarFiltroPedido);
    }
}