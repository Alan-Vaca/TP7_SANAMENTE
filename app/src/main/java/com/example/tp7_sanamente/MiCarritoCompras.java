package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MiCarritoCompras extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_carrito_compras);
    }

    public void MiCarritoDeComprasSeguirComprando(View view) {
        Intent miCarritoDeComprasSeguirComprando = new Intent(this, Mis_Productos.class);
        startActivity(miCarritoDeComprasSeguirComprando);
    }

    public void MiCarritoDeComprasComprar(View view) {
        Intent miCarritoDeComprasComprar = new Intent(this, Metodo_De_Pago.class);
        startActivity(miCarritoDeComprasComprar);
    }

    public void MenuCliente(View view) {
        Intent MenuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(MenuCliente);
    }
}