package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import Entidad.Usuario;

public class Detalle_Pedido extends AppCompatActivity {

    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            //Toast.makeText(Menu_Cliente.this, "USUARIO INGRESADO: " + user.getIdUsuario() + "-" + user.getNombreUsuario(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Detalle_Pedido.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }


    /*ACA DEPENDE QUE USUARIO SE LOGUEO, POR EL MOMENTO SOLO VUELVE AL HISTORIAL*/
    public void VolverHistorial(View view) {
        if(user.isCliente()){
            Intent VolverHistorial = new Intent(this, MiHistorial.class);
            startActivity(VolverHistorial);
        }else {
            VolverPedidos(view);
        }
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