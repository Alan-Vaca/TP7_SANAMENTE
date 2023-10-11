package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import Entidad.Usuario;

public class MiHistorial extends AppCompatActivity {

    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_historial);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast.makeText(MiHistorial.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    public void FiltrosHistorial(View view) {
        //Se usa el mismo que el de pedidos
        Intent FiltrosHistorial = new Intent(this, Filtros_Pedidos.class);
        startActivity(FiltrosHistorial);
    }
    public void DetalleHistorial(View view) {
        Intent DetalleHistorial = new Intent(this, Detalle_Pedido.class);
        startActivity(DetalleHistorial);
    }

    public void VolverAlMenu(View view) {
        if(user.isCliente()){
            MenuCliente(view);
        }else{
            MenuComercio(view);
        }
    }

    public void MenuCliente(View view) {
        Intent MenuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(MenuCliente);
    }

    public void MenuComercio(View view) {
        Intent MenuComercio = new Intent(this, MenuComercio.class);
        startActivity(MenuComercio);
    }
}