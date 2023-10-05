package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Detalle_Pedido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);
    }

    /*ACA DEPENDE QUE USUARIO SE LOGUEO, POR EL MOMENTO SOLO VUELVE AL HISTORIAL*/
    public void VolverHistorial(View view) {
        //VolverPedidos(view);
        Intent VolverHistorial = new Intent(this, MiHistorial.class);
        startActivity(VolverHistorial);
    }

    public void VolverPedidos(View view) {
        Intent VolverPedidos = new Intent(this, Mis_Pedidos.class);
        startActivity(VolverPedidos);
    }
    /*ACA DEPENDE QUE USUARIO SE LOGUEO, POR EL MOMENTO SOLO VUELVE AL HISTORIAL*/
    public void CalificarPedido(View view) {
        Intent CalificarPedido = new Intent(this, Calificar.class);
        startActivity(CalificarPedido);
    }

    public void ImprimirPedido(View view) {
        Toast.makeText(Detalle_Pedido.this, "SERA IMPRIMIDO", Toast.LENGTH_LONG).show();
    }
}