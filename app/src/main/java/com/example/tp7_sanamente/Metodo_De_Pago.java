package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import BaseDeDatos.Conexion;
import Entidad.Pedido;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Metodo_De_Pago extends AppCompatActivity {

    EditText numeroTarjeta, nombreTarjeta, fecha, codSeguridad;
    Spinner tipoPago;
    Usuario user;
    String[] mediosPago = {"Seleccione", "Efectivo", "Tarjeta de debito", "Tarjeta de credito"};

    ArrayList<pedidoXproducto> misProductosPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);

        numeroTarjeta = (EditText)findViewById(R.id.mp_et_numeroTarjeta);
        nombreTarjeta = (EditText)findViewById(R.id.mp_et_nombre);
        fecha = (EditText)findViewById(R.id.mp_et_fecha);
        codSeguridad = (EditText)findViewById(R.id.mp_et_codigoSeguridad);
        tipoPago = (Spinner)findViewById(R.id.spinnerTipoPago);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(Metodo_De_Pago.this, android.R.layout.simple_spinner_dropdown_item, mediosPago);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoPago.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String listaComoJson = preferences.getString("listadoCarrito", "");
        Type type = new TypeToken<ArrayList<pedidoXproducto>>(){}.getType();
        ArrayList<pedidoXproducto> listadoCarrito = gson.fromJson(listaComoJson, type);
        misProductosPedido = listadoCarrito;


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gsonUser = new Gson();
            user = gsonUser.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast.makeText(Metodo_De_Pago.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

        tipoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               int posicionTipoPago = tipoPago.getSelectedItemPosition();

               if(posicionTipoPago <= 1){
                   numeroTarjeta.setEnabled(false);
                   numeroTarjeta.setText("");
                   nombreTarjeta.setEnabled(false);
                   nombreTarjeta.setText("");
                   fecha.setEnabled(false);
                   fecha.setText("");
                   codSeguridad.setEnabled(false);
                   codSeguridad.setText("");
               }
               else{
                   numeroTarjeta.setEnabled(true);
                   nombreTarjeta.setEnabled(true);
                   fecha.setEnabled(true);
                   codSeguridad.setEnabled(true);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hagas nada aquí si no deseas realizar alguna acción cuando no se selecciona ningún elemento
            }
        });
    }




    public void MetodoDePagoFinalizarCompra(View view) {
        if(validarMetodoPago()){
            //Contendra los datos de todos los pedidos
            Pedido pedidoGeneral = new Pedido();
            int IDtipoPago = tipoPago.getSelectedItemPosition();
            pedidoGeneral.setMedioPago(IDtipoPago);
            int estado = 1;
            pedidoGeneral.setEstado(estado); //INICIO EL ESTADO
            Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());
            pedidoGeneral.setFecha(fechaActual);
            float monto = 0;
            for (pedidoXproducto item : misProductosPedido) {
                monto += (item.getCantidad() * item.getProducto().getPrecio());
            }
            pedidoGeneral.setMonto(monto);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
            builder.setView(dialogView);

            final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
            Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
            Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

            mensajeConfirm.setText("¿CONFIRMA COMPRA?");
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
                    new Metodo_De_Pago.agregarPedido().execute(pedidoGeneral);
                    dialog.dismiss();
                }
            });

        }
    }


    private class agregarPedido extends AsyncTask<Pedido, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Pedido... pedido) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.altaPedido(user,pedido[0],misProductosPedido);
            } catch (Exception e) {
                e.printStackTrace();
                return exito;
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                Toast.makeText(Metodo_De_Pago.this, "EL PEDIDO YA FUE SOLICITADO, REVISA TU HISTORIAL", Toast.LENGTH_LONG).show();
                Intent historial = new Intent(Metodo_De_Pago.this, MiHistorial.class);
                startActivity(historial);
            } else {
                Toast.makeText(Metodo_De_Pago.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }


    public Boolean validarMetodoPago(){
        boolean isValid = true;

        if(tipoPago.getSelectedItemPosition() == 0){
            Toast.makeText(Metodo_De_Pago.this, "SELECCIONE UN TIPO DE PAGO", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(tipoPago.getSelectedItemPosition() > 1 ){
            if(numeroTarjeta.getText().toString().length() != 16) {
                numeroTarjeta.setError("El número de tarjeta debe contener 16 dígitos");
                isValid = false;
            } else {
                numeroTarjeta.setError(null);
            }
            if(nombreTarjeta.getText().toString().length() > 24 || nombreTarjeta.getText().toString().length() <= 0) {
                nombreTarjeta.setError("El nombre de tarjeta no puede exceder los 24 caracteres o nombre de tarjeta vacio");
                isValid = false;
            } else {
                nombreTarjeta.setError(null);
            }
            String[] fechaArray = fecha.getText().toString().split("/");
            if(fechaArray.length != 2) {
                fecha.setError("El formato de fecha debe ser mm/aa");
                isValid = false;
            } else {
                int mes = Integer.parseInt(fechaArray[0]);
                int ano = Integer.parseInt(fechaArray[1]);
                if(mes < 1 || mes > 12 || ano < 21) {
                    fecha.setError("Fecha de expiración inválida");
                    isValid = false;
                } else {
                    fecha.setError(null);
                }
            }

            if(codSeguridad.getText().toString().length() != 3) {
                codSeguridad.setError("El código de seguridad debe contener 3 dígitos");
                isValid = false;
            } else {
                codSeguridad.setError(null);
            }

            //if(isValid) {
            //    Intent metodoDePagoFinalizarCompra = new Intent(this, MiHistorial.class);
            //    startActivity(metodoDePagoFinalizarCompra);
            //}
        }



        return isValid;
    }

    public void MetodoDePagoCancelarCompra(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        mensajeConfirm.setText("¿SALIR DE COMPRA?");
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
                Intent metodoDePagoCancelarCompra = new Intent(Metodo_De_Pago.this, MiCarritoCompras.class);
                startActivity(metodoDePagoCancelarCompra);
                dialog.dismiss();
            }
        });

    }
}