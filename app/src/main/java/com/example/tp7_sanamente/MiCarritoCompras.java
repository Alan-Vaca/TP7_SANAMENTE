package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.pedidoXproducto;

public class MiCarritoCompras extends AppCompatActivity {

    ArrayList<pedidoXproducto> misProductosPedido;
    TextView montoTotalTxt;
    ListView lv_pedidosxProducto;
    Float montoTotal;
    Button btn_pagar;
    pedidoXproducto itemSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_carrito_compras);

        lv_pedidosxProducto = (ListView)findViewById(R.id.lv_pedidosxProducto);
        misProductosPedido = new ArrayList<pedidoXproducto>();
        montoTotalTxt = (TextView)findViewById(R.id.txtMontoTotal);
        btn_pagar = (Button) findViewById(R.id.MiCC_btn_pagar);

        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String listaComoJson = preferences.getString("listadoCarrito", "");
        Type type = new TypeToken<ArrayList<pedidoXproducto>>(){}.getType();
        ArrayList<pedidoXproducto> listadoCarrito = gson.fromJson(listaComoJson, type);
        misProductosPedido = listadoCarrito;


        ArrayAdapter<pedidoXproducto> adapter = new ArrayAdapter<>(MiCarritoCompras.this, android.R.layout.simple_spinner_dropdown_item, misProductosPedido);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lv_pedidosxProducto.setAdapter(adapter);


        montoTotal = (float) 0;
        if(misProductosPedido.size() > 0){
            btn_pagar.setEnabled(true);
            for (pedidoXproducto item : misProductosPedido) {
                montoTotal += (item.getProducto().getPrecio() * item.getCantidad());
            }
        }
        else{
            btn_pagar.setEnabled(false);
        }
        montoTotalTxt.setText(montoTotal.toString());


        lv_pedidosxProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSeleccionado = new pedidoXproducto();
                itemSeleccionado = misProductosPedido.get(position);


            }
        });

    }

    public void MiCarritoDeComprasSeguirComprando(View view) {
        Intent miCarritoDeComprasSeguirComprando = new Intent(this, Mis_Productos.class);
        startActivity(miCarritoDeComprasSeguirComprando);
    }
    public void MiCarritoDeComprasCancelar(View view) {
        if (misProductosPedido != null && !misProductosPedido.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
            builder.setView(dialogView);

            final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
            Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
            Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

            mensajeConfirm.setText("¿QUIERES ELIMINAR EL CARRITO?");
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
                    misProductosPedido.clear();
                    SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String listaComoJson = gson.toJson(misProductosPedido);
                    editor.putString("listadoCarrito", listaComoJson);
                    editor.apply();

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    dialog.dismiss();
                }
            });


        }

    }

    public void MiCarritoDeComprasEliminarItem(View view) {
        if (itemSeleccionado != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
            builder.setView(dialogView);

            final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
            Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
            Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

            mensajeConfirm.setText("¿ESTAS SEGURO DE ELIMINAR EL PRODUCTO?");
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
                    misProductosPedido.remove(itemSeleccionado);

                    SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String listaComoJson = gson.toJson(misProductosPedido);
                    editor.putString("listadoCarrito", listaComoJson);
                    editor.apply();

                    /* refresca la página */
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

        } else {

            Toast toast = Toast.makeText(this, "DEBES SELECCIONAR UN PRODUCTO DE TU CARRITO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }

    public void MiCarritoDeComprasModificarItem(View view) {
        if (itemSeleccionado != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_cantidad, null);
            builder.setView(dialogView);

            final EditText editTextCantidad = dialogView.findViewById(R.id.editTextCantidad);
            Button btnGuardarCantidad = dialogView.findViewById(R.id.btnGuardarCantidad);
            Button btnCancelarCantidad = dialogView.findViewById(R.id.btnCancelarCantidadMensaje);

            final AlertDialog dialog = builder.create();
            dialog.show();

            btnGuardarCantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                 }
            });

            btnGuardarCantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(editTextCantidad.getText().toString().isEmpty()){
                        editTextCantidad.setError("INGRESA UNA NUEVA CANTIDAD");
                        return;
                    }

                    int nuevaCantidad = Integer.parseInt(editTextCantidad.getText().toString());

                    if(nuevaCantidad <= 0){
                        editTextCantidad.setError("LA CANTIDAD DEBE SER MAYOR A 0");
                        return;
                    }

                    if(nuevaCantidad > itemSeleccionado.getProducto().getStock()){
                        editTextCantidad.setError("LA CANTIDAD EXCEDE EL STOCK");
                        return;
                    }

                    itemSeleccionado.setCantidad(nuevaCantidad);

                    SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String listaComoJson = gson.toJson(misProductosPedido);
                    editor.putString("listadoCarrito", listaComoJson);
                    editor.apply();

                    dialog.dismiss();

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        } else {

            Toast toast = Toast.makeText(this, "Selecciona un elemento para modificar", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }

    public void MiCarritoDeComprasComprar(View view) {
        Intent miCarritoDeComprasComprar = new Intent(this, Metodo_De_Pago.class);
        startActivity(miCarritoDeComprasComprar);
    }

    public void MenuCliente(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent MenuCliente = new Intent(this, MenuAdmin.class);
            startActivity(MenuCliente);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            Intent MenuCliente = new Intent(this, Menu_Cliente.class);
            startActivity(MenuCliente);
        }
    }


}