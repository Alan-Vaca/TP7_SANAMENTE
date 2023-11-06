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

import java.util.ArrayList;

import Entidad.Usuario;

public class MenuComercio extends AppCompatActivity {

    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_comercio);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            //Toast.makeText(Menu_Cliente.this, "USUARIO INGRESADO: " + user.getIdUsuario() + "-" + user.getNombreUsuario(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MenuComercio.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }


    public void MenuGenerarReporte(View view) {

        ArrayList<String> datosInforme = new ArrayList<>();

        datosInforme.add("REPORTE MI COMERCIAL");
        datosInforme.add("Detalles del informe:");
        datosInforme.add(" - ITEM 1: Detalle de ITEM 1");


        boolean exito = ExportarTxt.generarTxt(MenuComercio.this, datosInforme, "InformeComercio");


        if (exito) {
            Toast.makeText(MenuComercio.this, "PDF generado con éxito", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MenuComercio.this, "Error al generar el PDF", Toast.LENGTH_LONG).show();
        }


       // Toast.makeText(MenuComercio.this, "SE GENERO EL SIGUIENTE REPORTE:" + '\n' + "ITEM 1", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        mensajeConfirm.setText("¿ESTAS SEGURO DE SALIR?");
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
                Intent menuPrincipal = new Intent(MenuComercio.this, MainActivity.class);
                startActivity(menuPrincipal);
                dialog.dismiss();
            }
        });

    }
}