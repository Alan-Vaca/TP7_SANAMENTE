package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import Entidad.Usuario;

public class Mis_Productos extends AppCompatActivity {

    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            //Toast.makeText(Menu_Cliente.this, "USUARIO INGRESADO: " + user.getIdUsuario() + "-" + user.getNombreUsuario(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Mis_Productos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    public void CatalogoVerProducto(View view) {
        Intent catalogoVerProducto = new Intent(this, ModificarProducto.class);
        startActivity(catalogoVerProducto);
    }

    public void CatalogoFiltros(View view) {
        Intent catalogoFiltros = new Intent(this, Filtros.class);
        startActivity(catalogoFiltros);
    }

    public void CatalogoAgregarNuevoProducto(View view) {
        Intent catalogoAgregarNuevoProducto = new Intent(this, AgregarProducto.class);
        startActivity(catalogoAgregarNuevoProducto);
    }

    public void VolverMenu(View view) {
        if(user.isCliente()) {

            Intent MenuCliente = new Intent(this, Menu_Cliente.class);
            startActivity(MenuCliente);
        }else {
            Intent MenuComercio = new Intent(this, MenuComercio.class);
            startActivity(MenuComercio);
        }
    }
}