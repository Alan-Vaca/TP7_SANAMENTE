package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuComercio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_comercio);
    }


    public void MenuGenerarReporte(View view) {
        Toast.makeText(MenuComercio.this, "SE GENERO EL SIGUIENTE REPORTE:" + '\n' + "ITEM 1", Toast.LENGTH_LONG).show();
    }


    public void MiUsuarioComercio(View view) {
        Intent miUsuarioComercio = new Intent(this, MiUsuarioComercio.class);
        startActivity(miUsuarioComercio);
    }

    public void MenuHistorial(View view) {
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void MenuPedidos_Comerciante(View view) {
        Intent Mis_Pedidos = new Intent(this, Mis_Pedidos.class);
        startActivity(Mis_Pedidos);
    }

    public void MenuMis_Productos(View view) {
        Intent menuMis_Productos = new Intent(this, Mis_Productos.class);
        startActivity(menuMis_Productos);
    }

    public void MenuAgregarProducto(View view) {
        Intent menuAgregarProducto = new Intent(this, AgregarProducto.class);
        startActivity(menuAgregarProducto);
    }

    public void MenuPrincipal(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }
}