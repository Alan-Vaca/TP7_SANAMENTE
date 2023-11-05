package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import Entidad.Usuario;

public class Menu_Cliente extends AppCompatActivity {

    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            //Toast.makeText(Menu_Cliente.this, "USUARIO INGRESADO: " + user.getIdUsuario() + "-" + user.getNombreUsuario(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Menu_Cliente.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        mensajeConfirm.setText("¿DESEA SALIR?");
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancelarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuPrincipal = new Intent(Menu_Cliente.this, MainActivity.class);
                startActivity(menuPrincipal);
                dialog.dismiss();
            }
        });


    }
}