package com.example.tp7_sanamente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import BaseDeDatos.consultasReportes;
import Entidad.Reporte;
import Entidad.Usuario;

public class Filtros_Reporte extends AppCompatActivity {


    CheckBox cbFacturacion, cbClientes, cbProductos;
    EditText fechaDesde, fechaHasta;
    Usuario user;
    Reporte reporte;

    boolean facturacion, cliente, producto;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        fechaDesde = (EditText)findViewById(R.id.fp_et_InformeFechaDesde);
        fechaHasta = (EditText)findViewById(R.id.fp_et_InformeFechaHasta);
        cbFacturacion = findViewById(R.id.fp_cb_datosFacturacion);
        cbClientes = findViewById(R.id.fp_cb_datosCliente);
        cbProductos = findViewById(R.id.fp_cb_datosProductos);

        Button btnConfirmarReporte = findViewById(R.id.fp_bn_confirmarReporte);




        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gsonUser = new Gson();
            user = gsonUser.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast toast = Toast.makeText(Filtros_Reporte.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }


        btnConfirmarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generarReporte(view);
            }
        });

    }


    public void generarReporte(View view) {
        boolean isValid = true;

        ///////////// VALIDACIONES FECHAS  //////////////////////

        // Validar que fechaDesde pertenezca a un formato dd/MM/yyyy. Si no, mostrar mensaje de error
        String fechaDesdeStr = fechaDesde.getText().toString();
        if (!fechaDesdeStr.isEmpty() && !isValidDateFormat(fechaDesdeStr, "dd/MM/yyyy")) {
            fechaDesde.setError("Formato de fecha inválido");
            isValid = false;
        } else {
            fechaDesde.setError(null);

            // Obtener la fecha actual
            Calendar calendar = Calendar.getInstance();
            Date fechaActual = calendar.getTime();

            // Validar que fechaDesde no sea superior a la fecha actual
            if (!fechaDesdeStr.isEmpty()) {
                try {
                    Date fechaDesdeDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaDesdeStr);

                    if (fechaDesdeDate.after(fechaActual)) {
                        fechaDesde.setError("La fecha desde no puede ser superior a la fecha actual");
                        isValid = false;
                    } else {
                        fechaDesde.setError(null);
                    }
                } catch (ParseException e) {
                    // Manejar la excepción si ocurre un error al analizar la fecha
                    e.printStackTrace();
                    isValid = false;
                }
            }
        }

        // Validar que fechaHasta pertenezca a un formato dd/MM/yyyy. Si no, mostrar mensaje de error
        String fechaHastaStr = fechaHasta.getText().toString();
        if (!fechaHastaStr.isEmpty() && !isValidDateFormat(fechaHastaStr, "dd/MM/yyyy")) {
            fechaHasta.setError("Formato de fecha inválido");
            isValid = false;
        } else {
            fechaHasta.setError(null);

            // Obtener la fecha actual
            Calendar calendar = Calendar.getInstance();
            Date fechaActual = calendar.getTime();

            // Validar que fechaHasta no sea mayor que la fecha actual ni menor que fechaDesde
            if (!fechaHastaStr.isEmpty()) {
                try {
                    Date fechaHastaDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaHastaStr);

                    // Validar que fechaHasta no sea mayor que la fecha actual
                    if (fechaHastaDate.after(fechaActual)) {
                        fechaHasta.setError("La fecha hasta no puede ser superior a la fecha actual");
                        isValid = false;
                    } else {
                        fechaHasta.setError(null);

                        // Validar que fechaHasta no sea menor que fechaDesde
                        if (!fechaDesdeStr.isEmpty()) {
                            Date fechaDesdeDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaDesdeStr);

                            if (fechaHastaDate.before(fechaDesdeDate)) {
                                fechaHasta.setError("La fecha hasta no puede ser menor que la fecha desde");
                                isValid = false;
                            } else {
                                fechaHasta.setError(null);
                            }
                        }
                    }
                } catch (ParseException e) {
                    // Manejar la excepción si ocurre un error al analizar la fecha
                    e.printStackTrace();
                    isValid = false;
                }
            }
        }

        ///////////// VALIDACIONES FECHAS  //////////////////////

        facturacion = cbFacturacion.isChecked();
        cliente = cbClientes.isChecked();
        producto = cbProductos.isChecked();

        if (isValid) {
            if (facturacion || cliente || producto) {
                Toast.makeText(Filtros_Reporte.this, "Generando Reporte... Por favor espere", Toast.LENGTH_LONG).show();
                new Filtros_Reporte.obtenerReporteTask().execute(fechaDesdeStr, fechaHastaStr);
            } else {
                Toast.makeText(Filtros_Reporte.this, "Por favor, seleccione al menos un dato de filtrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Filtros_Reporte.this, "Por favor, ingresa fechas en el formato correcto", Toast.LENGTH_SHORT).show();
        }
    }

    private class obtenerReporteTask extends AsyncTask<Object, Void, Reporte> {
        @Override
        protected Reporte doInBackground(Object... params) {

            reporte = new Entidad.Reporte();
            consultasReportes consulta = new consultasReportes();


            String fechaDesde = (String) params[0];
            String fechaHasta = (String) params[1];
           // boolean facturacion = (boolean) params[2];
            //boolean cliente = (boolean) params[3];
            //boolean producto = (boolean) params[4];





            try {
                reporte = consulta.reporteSemanal(user.getIdUsuario(), fechaDesde, fechaHasta );
                return reporte;
            } catch (Exception e) {
                e.printStackTrace();
                return reporte;
            }

        }

        @Override
        protected void onPostExecute(Reporte informe) {

            reporte = informe;



            ArrayList<String> datosInforme = new ArrayList<>();

            datosInforme.add("REPORTE SANAMENTE");
            datosInforme.add("Detalle del Informe");
            datosInforme.add("-----------------------");



        if(facturacion){
            datosInforme.add("Pedidos Totales: " + reporte.getCantidadPedidos());
            datosInforme.add("Facturación total: $ " + reporte.getMontoTotal());
        }

            if(producto){
                datosInforme.add("Producto mas vendido: " + reporte.getnombreProducto());
                datosInforme.add("Cantidad de ventas: " + reporte.getCantidadProducto());
            }

            if(cliente){
                datosInforme.add("Cliente mas frecuente: " + reporte.getUsuario().getNombre() +

                        " " + reporte.getUsuario().getApellido() +
                        " [usuario: " + reporte.getUsuario().getApellido() + "]" +
                        ", " + reporte.getUsuario().getDireccion());

                datosInforme.add("Cantidad de pedidos: " + reporte.getCantidadUsuario());

                datosInforme.add("Medio de pago mas usado: " + reporte.getDescripcionPago());
            }

            boolean exito = ExportarPdf.exportarAPdf(Filtros_Reporte.this, datosInforme, "InformeComercio");


            if (exito) {
                Toast.makeText(Filtros_Reporte.this, "PDF generado con éxito", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Filtros_Reporte.this, "Error al generar el PDF", Toast.LENGTH_LONG).show();
            }


        }



        }

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


    public void Volver(View view) {

        Intent Volver = new Intent(this, MenuComercio.class);
        startActivity(Volver);

    }


}
