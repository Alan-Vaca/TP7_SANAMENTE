package com.example.tp7_sanamente;

import static BaseDeDatos.consultasReportes.reporteSemanal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import BaseDeDatos.consultasReportes;
import Entidad.Comercio;
import Entidad.Pedido;
import Entidad.Reporte;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class MenuComercio extends AppCompatActivity {

    Usuario user;
    Reporte reporte;


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


        //new MenuComercio.obtenerinforme().execute(user);
    }


    private class obtenerinforme extends AsyncTask<Usuario, Void, Reporte> {
        @Override
        protected Reporte doInBackground(Usuario... usuario) {
            reporte = new Reporte();
            consultasReportes consulta = new consultasReportes();

            try {
                reporte = consulta.reporteSemanal(user.getIdUsuario());
                return reporte;
            } catch (Exception e) {
                e.printStackTrace();
                return reporte;
            }
        }
        @Override
        protected void onPostExecute(Reporte informe) {
            reporte = informe;
            Toast.makeText(MenuComercio.this, "Generando Reporte", Toast.LENGTH_LONG).show();

            ArrayList<String> datosInforme = new ArrayList<>();

            datosInforme.add("REPORTE SANAMENTE");
            datosInforme.add("Detalle del Informe");
            datosInforme.add("-----------------------");
            datosInforme.add("-----------------------");



            datosInforme.add("Ventas Totales: ");
            datosInforme.add("Facturación total: $ " + reporte.getMontoTotal());

            datosInforme.add("Producto mas vendido: " + reporte.getnombreProducto());
            datosInforme.add("Cantidad de ventas: " + reporte.getCantidadProducto());

            datosInforme.add("Cliente mas frecuente: " + reporte.getUsuario().getNombre() +
                    " " + reporte.getUsuario().getApellido() +
                    ", " + reporte.getUsuario().getDireccion());
            datosInforme.add("Cantidad de pedidos: " + reporte.getCantidadUsuario());

            datosInforme.add("Medio de pago mas usado: " + reporte.getDescripcionPago());


            boolean exito = ExportarPdf.exportarAPdf(MenuComercio.this, datosInforme, "InformeComercio");


            if (exito) {
                Toast.makeText(MenuComercio.this, "PDF generado con éxito", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MenuComercio.this, "Error al generar el PDF", Toast.LENGTH_LONG).show();
            }





        }

    }



    public void MenuGenerarReporte(View view) {

        new MenuComercio.obtenerinforme().execute(user);



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