package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class MenuAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
    }

    public void LoguearseComoCliente(){
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("cliente");
        usuario.setContraseña("123");

        new MenuAdmin.obtenerUsuarioXloginTask().execute(usuario);
    }

    public void LoguearseComoComercio(){
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("comercio");
        usuario.setContraseña("321");

        new MenuAdmin.obtenerUsuarioXloginTask().execute(usuario);
    }
    public void ClienteNotificaciones(View view) {
        LoguearseComoCliente();
        Intent ingresarNotificaciones = new Intent(this, MisNotificaciones.class);
        startActivity(ingresarNotificaciones);
    }

    public void ClienteMenuMiUsuario(View view) {
        LoguearseComoCliente();
        Intent menuMiUsuario = new Intent(this, MiUsuario.class);
        startActivity(menuMiUsuario);
    }

    public void ClienteMenuHistorial(View view) {
        LoguearseComoCliente();
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void ClienteMenuCatalogo(View view) {
        LoguearseComoCliente();
        Intent menuCatalogo = new Intent(this, Mis_Productos.class);
        startActivity(menuCatalogo);
    }


    public void ClienteMenuMiCarrito(View view) {
        LoguearseComoCliente();
        Intent menuMiCarrito = new Intent(this, MiCarritoCompras.class);
        startActivity(menuMiCarrito);
    }

    public void ClienteMenuOfertas(View view) {
        LoguearseComoCliente();
        Toast.makeText(MenuAdmin.this, "TUS OFERTAS:" + '\n' + "ITEM 1", Toast.LENGTH_LONG).show();
    }

    public void ComercioMenuGenerarReporte(View view) {
        LoguearseComoComercio();
        Toast.makeText(MenuAdmin.this, "SE GENERO EL SIGUIENTE REPORTE:" + '\n' + "ITEM 1", Toast.LENGTH_LONG).show();
    }


    public void ComercioMiUsuarioComercio(View view) {
        LoguearseComoComercio();
        Intent miUsuarioComercio = new Intent(this, MiUsuarioComercio.class);
        startActivity(miUsuarioComercio);
    }

    public void ComercioMenuHistorial(View view) {
        LoguearseComoComercio();
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void ComercioMenuPedidos_Comerciante(View view) {
        LoguearseComoComercio();
        Intent Mis_Pedidos = new Intent(this, Mis_Pedidos.class);
        startActivity(Mis_Pedidos);
    }

    public void ComercioMenuMis_Productos(View view) {
        LoguearseComoComercio();
        Intent menuMis_Productos = new Intent(this, Mis_Productos.class);
        startActivity(menuMis_Productos);
    }

    public void ComercioMenuAgregarProducto(View view) {
        LoguearseComoComercio();
        Intent menuAgregarProducto = new Intent(this, AgregarProducto.class);
        startActivity(menuAgregarProducto);
    }

    public void Salir(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }

    private class obtenerUsuarioXloginTask extends AsyncTask<Usuario, Void, Usuario> {
        @Override
        protected Usuario doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Usuario us = new Usuario();
            try {
                us = con.obtenerUsuarioXlogin(user[0]);

                return us;
            } catch (Exception e) {
                e.printStackTrace();
                return us;
            }
        }

        @Override
        protected void onPostExecute(Usuario user) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (user.getIdUsuario() > 0) {
                ArrayList<pedidoXproducto> listadoCarrito = new ArrayList<pedidoXproducto>();

                SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorCarrito = preferences.edit();
                Gson gsonCarrito = new Gson();
                String listaComoJson = gsonCarrito.toJson(listadoCarrito);
                editorCarrito.putString("listadoCarrito", listaComoJson);
                editorCarrito.apply();


                ArrayList<Producto> listaFiltrada = new ArrayList<Producto>();
                SharedPreferences preferencesFiltro = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltro = preferencesFiltro.edit();
                Gson gsonFiltro = new Gson();
                String listaComoJsonFiltrada = gsonFiltro.toJson(listaFiltrada);
                editorFiltro.putString("listadoProductosFiltrados", listaComoJsonFiltrada);
                editorFiltro.apply();


                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String usuarioJson = gson.toJson(user);
                editor.putString("usuarioLogueado", usuarioJson);
                editor.apply();

            } else {
                Toast.makeText(MenuAdmin.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }

    }
}