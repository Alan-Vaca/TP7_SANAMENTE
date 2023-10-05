package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Menu_Cliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);
    }

    public void Notificaciones(View view) {
      Intent ingresarNotificaciones = new Intent(this, MisNotificaciones.class);
      startActivity(ingresarNotificaciones);
    }

    public void MenuMiUsuario(View view) {
        Intent menuMiUsuario = new Intent(this, MiUsuario.class);
        startActivity(menuMiUsuario);
    }

    public void MenuHistorial(View view) {
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void MenuCatalogo(View view) {
        Intent menuCatalogo = new Intent(this, Mis_Productos.class);
        startActivity(menuCatalogo);
    }


    public void MenuMiCarrito(View view) {
        Intent menuMiCarrito = new Intent(this, MiCarritoCompras.class);
        startActivity(menuMiCarrito);
    }


    public void MenuOfertas(View view) {
        Toast.makeText(Menu_Cliente.this, "TUS OFERTAS:" + '\n' + "ITEM 1", Toast.LENGTH_LONG).show();

    }


    public void MenuPrincipal(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }
}