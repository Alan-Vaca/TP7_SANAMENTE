package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Mis_Pedidos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);
    }

    public void FiltrosPedidos(View view) {
        //Se usa el mismo que el de pedidos
        Intent FiltrosHistorial = new Intent(this, Filtros_Pedidos.class);
        startActivity(FiltrosHistorial);
    }
    public void DetallePedidos(View view) {
        Intent DetalleHistorial = new Intent(this, Detalle_Pedido.class);
        startActivity(DetalleHistorial);
    }

    public void CancelarPedido(View view) {
        Toast.makeText(Mis_Pedidos.this, "PEDIDO CANCELADO", Toast.LENGTH_LONG).show();
    }
}