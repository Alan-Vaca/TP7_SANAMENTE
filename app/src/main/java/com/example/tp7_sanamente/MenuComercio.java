package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuComercio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_comercio);
    }

    /* FALTA CREAR PANTALLA
    public void MenuGenerarReporte(View view) {
        Intent menuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(menuCliente);
    }
     */

    public void MiUsuarioComercio(View view) {
        Intent miUsuarioComercio = new Intent(this, MiUsuarioComercio.class);
        startActivity(miUsuarioComercio);
    }

    public void MenuHistorial(View view) {
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void MenuPedidos_Comerciante(View view) {
        Intent menuPedidos_Comerciante = new Intent(this, Pedidos_Comerciante.class);
        startActivity(menuPedidos_Comerciante);
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