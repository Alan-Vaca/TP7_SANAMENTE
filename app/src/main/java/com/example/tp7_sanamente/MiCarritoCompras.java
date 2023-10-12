package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import Entidad.pedidoXproducto;

public class MiCarritoCompras extends AppCompatActivity {

    ArrayList<pedidoXproducto> misProductosPedido;
    TextView montoTotalTxt;
    ListView lv_pedidosxProducto;
    Float montoTotal;

    pedidoXproducto itemSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_carrito_compras);

        lv_pedidosxProducto = (ListView)findViewById(R.id.lv_pedidosxProducto);
        misProductosPedido = new ArrayList<pedidoXproducto>();
        montoTotalTxt = (TextView)findViewById(R.id.txtMontoTotal);

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
            for (pedidoXproducto item : misProductosPedido) {
                montoTotal += (item.getProducto().getPrecio() * item.getCantidad());
            }
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
            misProductosPedido.clear();
        }

        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String listaComoJson = gson.toJson(misProductosPedido);
        editor.putString("listadoCarrito", listaComoJson);
        editor.apply();
    }

    public void MiCarritoDeComprasEliminarItem(View view) {
        if (itemSeleccionado != null) {
            misProductosPedido.remove(itemSeleccionado);

            SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String listaComoJson = gson.toJson(misProductosPedido);
            editor.putString("listadoCarrito", listaComoJson);
            editor.apply();

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(this, "DEBES SELECCIONAR UN PRODUCTO DE TU CARRITO", Toast.LENGTH_LONG).show();
        }
    }

    public void MiCarritoDeComprasModificarItem(View view) {
        if (itemSeleccionado != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_cantidad, null);
            builder.setView(dialogView);

            final EditText editTextCantidad = dialogView.findViewById(R.id.editTextCantidad);
            Button btnGuardarCantidad = dialogView.findViewById(R.id.btnGuardarCantidad);

            final AlertDialog dialog = builder.create();
            dialog.show();

            btnGuardarCantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nuevaCantidad = Integer.parseInt(editTextCantidad.getText().toString());

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
            Toast.makeText(this, "Selecciona un elemento para modificar", Toast.LENGTH_SHORT).show();
        }
    }

    public void MiCarritoDeComprasComprar(View view) {
        Intent miCarritoDeComprasComprar = new Intent(this, Metodo_De_Pago.class);
        startActivity(miCarritoDeComprasComprar);
    }

    public void MenuCliente(View view) {
        Intent MenuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(MenuCliente);
    }
}