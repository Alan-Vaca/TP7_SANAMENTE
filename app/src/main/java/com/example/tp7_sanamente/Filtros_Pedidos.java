



package com.example.tp7_sanamente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import BaseDeDatos.Conexion;
import BaseDeDatos.consultasHistoriales;
import BaseDeDatos.consultasPedidos;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Usuario;


public class Filtros_Pedidos extends AppCompatActivity {

    EditText fechaDesde, horaDesde, fechaHasta, horaHasta;
    CheckBox cbEntregado, cbCancelado, cbPendiente;
    RadioButton rbEnEspera, rbRecientes, rbSinFiltro;
    Usuario user;
    ArrayList<Historial> listadoHistorialesFiltrado, listadoHistorialesObtenido, listadoHistorial;
     ArrayList<Pedido> listadoPedidos, listadoPedidosObtenido, listadPedidosFiltrado;
    consultasHistoriales consultasHistoriales;
    consultasPedidos consultasPedidos;

    boolean isHistorial;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_pedidos);

        fechaDesde = (EditText)findViewById(R.id.fp_et_fechaDesde);
        //horaDesde = (EditText)findViewById(R.id.fp_et_horaDesde);
        fechaHasta = (EditText)findViewById(R.id.fp_et_fechaHasta);
        //horaHasta = (EditText)findViewById(R.id.fp_et_horaHasta);
        cbEntregado = findViewById(R.id.fp_cb_entregado);
        cbCancelado = findViewById(R.id.fp_cb_cancelado);
        cbPendiente = findViewById(R.id.fp_cb_pendiente);
        rbEnEspera = findViewById(R.id.fp_rb_enEspera);
        rbRecientes = findViewById(R.id.fp_rb_recientes);
        rbSinFiltro = findViewById(R.id.fp_rb_sinOrdenar);

        consultasHistoriales = new consultasHistoriales();
        consultasPedidos = new consultasPedidos();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");
        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast.makeText(Filtros_Pedidos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


        Intent intent = getIntent();
        isHistorial = intent.getBooleanExtra("isHistorial", false);

        SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
        fechaDesde.setText(preferences.getString("fechaDesdeStr", ""));
        fechaHasta.setText(preferences.getString("fechaHastaStr", ""));
        rbRecientes.setChecked(preferences.getBoolean("ordenreciente", false));
        rbEnEspera.setChecked(preferences.getBoolean("ordenespera", false));
        cbEntregado.setChecked(preferences.getBoolean("entregado", false));
        cbCancelado.setChecked(preferences.getBoolean("cancelado", false));
        cbPendiente.setChecked(preferences.getBoolean("pendiente", false));
    }

    public void QuitarFiltro(View view){
        fechaDesde.setText("");
        fechaHasta.setText("");
        rbRecientes.setChecked(false);
        rbEnEspera.setChecked(false);
        cbEntregado.setChecked(false);
        cbCancelado.setChecked(false);
        cbPendiente.setChecked(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AplicarFiltroHistorial(view);
            }
        }, 2000);
    }


    public void AplicarFiltroHistorial(View view) {
        //AplicarFiltroPedido(view);

        boolean isValid = true;

        ///////////// VALIDACIONES FECHAS  //////////////////////

        // Validar que fechaDesde pertenezca a un formato dd/mm/aaaa. Si no, mostrar mensaje de error
        String fechaDesdeStr = fechaDesde.getText().toString();
        if (!fechaDesdeStr.isEmpty() && !isValidDateFormat(fechaDesdeStr, "dd/MM/yyyy")) {
            fechaDesde.setError("Formato de fecha inválido");
            isValid = false;
        } else {
            fechaDesde.setError(null);
        }

        // Validar que fechaHasta pertenezca a un formato dd/mm/aaaa. Si no, mostrar mensaje de error
        String fechaHastaStr = fechaHasta.getText().toString();
        if (!fechaHastaStr.isEmpty() && !isValidDateFormat(fechaHastaStr, "dd/MM/yyyy")) {
            fechaHasta.setError("Formato de fecha inválido");
            isValid = false;
        } else {
            fechaHasta.setError(null);
        }


        boolean entregado = cbEntregado.isChecked();
        boolean cancelado = cbCancelado.isChecked();
        boolean pendiente = cbPendiente.isChecked();
        String orden = "";


        if(rbEnEspera.isChecked()){orden = "en espera";}
        else if (rbRecientes.isChecked()){ orden = "recientes"; }
        else {orden = "";}



        if(isValid) { //

            SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fechaDesdeStr", fechaDesde.getText().toString());
            editor.putString("fechaHastaStr", fechaHasta.getText().toString());
            editor.putBoolean("ordenreciente", rbRecientes.isChecked());
            editor.putBoolean("ordenespera", rbEnEspera.isChecked());
            editor.putBoolean("entregado", cbEntregado.isChecked());
            editor.putBoolean("cancelado", cbCancelado.isChecked());
            editor.putBoolean("pendiente", cbPendiente.isChecked());
            editor.apply();
            if (isHistorial) {


                new AplicarFiltrosHistorialTask().execute(fechaDesdeStr, fechaHastaStr,
                        entregado, cancelado, pendiente, orden);
            }
            else if (!isHistorial) {
                new AplicarFiltrosPedidoTask().execute(fechaDesdeStr, fechaHastaStr, entregado,
                        cancelado, pendiente, orden);
            }
        }else {
            Toast.makeText(Filtros_Pedidos.this, "Por favor, ingresa fechas y horas en el formato correcto", Toast.LENGTH_SHORT).show();
        }
    }

    private class AplicarFiltrosHistorialTask extends AsyncTask<Object, Void, ArrayList<Historial>> {
        @Override
        protected ArrayList<Historial> doInBackground(Object... params) {


            String fechaDesde = (String) params[0];
            String fechaHasta = (String) params[1];
            boolean entregado = (boolean) params[2];
            boolean cancelado = (boolean) params[3];
            boolean pendiente = (boolean) params[4];
            String orden = (String) params[5];

            Conexion con = new Conexion();
            try {
                listadoHistorial = con.obtenerListadoHistorial(user);
            } catch (Exception e) {
                e.printStackTrace();
            }


            listadoHistorialesObtenido = consultasHistoriales.obtenerListadoHistorialesFiltrado(listadoHistorial, fechaDesde, fechaHasta,
                    entregado, cancelado, pendiente, orden);

            return listadoHistorialesObtenido;

        }

        @Override
        protected void onPostExecute(ArrayList<Historial> historiales) {

            listadoHistorialesFiltrado = listadoHistorialesObtenido;


            try {
                SharedPreferences preferencesFiltradoHistorial = getSharedPreferences("mi_prefHistorial", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltradoHistorial = preferencesFiltradoHistorial.edit();

                Gson gsonFiltradoHistorial = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();
                String listaComoJsonFiltradosHistorial = gsonFiltradoHistorial.toJson(listadoHistorialesFiltrado);
                editorFiltradoHistorial.putString("listadoHistorialesFiltrado", listaComoJsonFiltradosHistorial);
                editorFiltradoHistorial.apply();


                Intent intent = new Intent(Filtros_Pedidos.this, MiHistorial.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.d("Filtro.Historial", e.toString());
            }

        }
    }

    private class AplicarFiltrosPedidoTask extends AsyncTask<Object, Void, ArrayList<Pedido>> {
        @Override
        protected ArrayList<Pedido> doInBackground(Object... params) {

            String fechaDesde = (String) params[0];
            String fechaHasta = (String) params[1];
            boolean entregado = (boolean) params[2];
            boolean cancelado = (boolean) params[3];
            boolean pendiente = (boolean) params[4];
            String orden = (String) params[5];


            Conexion con = new Conexion();
            try {
                listadoPedidos = con.obtenerListadoPedidos(user);
            } catch (Exception e) {
                e.printStackTrace();
            }


            listadoPedidosObtenido = consultasPedidos.obtenerListadoPedidosFiltrado(listadoPedidos, fechaDesde, fechaHasta,
                    entregado, cancelado, pendiente, orden);

            return listadoPedidosObtenido;

        }

        @Override
        protected void onPostExecute(ArrayList<Pedido> pedidos) {
            listadPedidosFiltrado = listadoPedidosObtenido;

            try {
                SharedPreferences preferencesFiltradoPedido = getSharedPreferences("mi_prefPedido", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltradoPedido = preferencesFiltradoPedido.edit();

                Gson gsonFiltradoPedido = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();
                String listaComoJsonFiltradosPedido = gsonFiltradoPedido.toJson(listadPedidosFiltrado);
                editorFiltradoPedido.putString("listadoPedidosFiltrado", listaComoJsonFiltradosPedido);
                editorFiltradoPedido.apply();


                Intent intent = new Intent(Filtros_Pedidos.this, Mis_Pedidos.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.d("Filtro.Pedido", e.toString());
            }



        }
    }


    public void AplicarFiltroPedido(View view) {

        Intent AplicarFiltroPedido = new Intent(this, Mis_Pedidos.class);
        startActivity(AplicarFiltroPedido);

    }

    public void VolverAPedidos(View view) {

        Intent VolverAPedidos = new Intent(this, Mis_Pedidos.class);
        startActivity(VolverAPedidos);

    }



    // Función para validar el formato de fecha y hora
    private boolean isValidDateFormat(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setLenient(false); //es estricto al momento de analizar la fecha
        try {
            sdf.parse(value);// Analiza la cadena "value" en un objeto Date utilizando el formato de fecha definido en "sdf"
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

/*SE APLICARA DEPENDIENDO SI ES UN PEDIDO O UN HISTORIAL*/
/*

 */