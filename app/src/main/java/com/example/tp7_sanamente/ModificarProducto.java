package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.Usuario;

public class ModificarProducto extends AppCompatActivity {

    Spinner etiquetado_1, etiquetado_2,etiquetado_3;
    ArrayList<Etiquetado> listaEtiquetados;
    TextView nombreProducto, ingredienteProducto, precioProducto, stockProducto, detalleStock;

    Button btnModificarAgregarCarrito;
    Usuario user;
    Producto productoSeleccionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);

        etiquetado_1 = (Spinner)findViewById(R.id.ap_sp_item1);
        etiquetado_2 = (Spinner)findViewById(R.id.ap_sp_item2);
        etiquetado_3 = (Spinner)findViewById(R.id.ap_sp_item3);

        nombreProducto = (TextView)findViewById(R.id.ap_et_nombreDelProductoMod);
        ingredienteProducto = (TextView)findViewById(R.id.ap_et_ingredientes);
        precioProducto = (TextView)findViewById(R.id.ap_n_precio);
        stockProducto = (TextView)findViewById(R.id.ap_n_stock);

        detalleStock = (TextView)findViewById(R.id.txtInfoStock);
        btnModificarAgregarCarrito = (Button)findViewById(R.id.btnModAddCart);

        listaEtiquetados = new ArrayList<Etiquetado>();

        new ModificarProducto.obtenerListadoEtiquetado().execute();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            if(user.isCliente()){
                nombreProducto.setEnabled(false);
                ingredienteProducto.setEnabled(false);
                precioProducto.setEnabled(false);
                etiquetado_1.setEnabled(false);
                etiquetado_2.setEnabled(false);
                etiquetado_3.setEnabled(false);

                btnModificarAgregarCarrito.setText("AGREGAR AL CARRITO");
                detalleStock.setText("CANTIDAD A COMPRAR:");
            }else{
                detalleStock.setText("STOCK:");
                btnModificarAgregarCarrito.setText("MODIFICAR PRODUCTO");
            }

        }else{
            Toast.makeText(ModificarProducto.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


        SharedPreferences sharedPreferencesProducto = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String productoJson = sharedPreferencesProducto.getString("productoSeleccionado", "");

        productoSeleccionado = new Producto();
        if (!productoJson.isEmpty()) {
            Gson gson = new Gson();
            productoSeleccionado = gson.fromJson(productoJson, Producto.class);

            nombreProducto.setText(productoSeleccionado.getNombre());
            ingredienteProducto.setText(productoSeleccionado.getIngredientes());
            String precioUnitario = String.valueOf(productoSeleccionado.getPrecio());
            precioProducto.setText(precioUnitario);

            if(user.isCliente()){
                stockProducto.setText("0");
            }else {
                String stockdelProducto = String.valueOf(productoSeleccionado.getStock());
                stockProducto.setText(stockdelProducto);
            }
            new ModificarProducto.obtenerEtiquetadosXproducto().execute(productoSeleccionado);

        }else{
            Toast.makeText(ModificarProducto.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    private class obtenerListadoEtiquetado extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {
            ArrayList<Etiquetado> listadoXproducto = new ArrayList<Etiquetado>();
            Conexion con = new Conexion();
            try {
                listadoXproducto = con.obtenerListadoEtiquetado();

                return listadoXproducto;
            } catch (Exception e) {
                e.printStackTrace();
                return listadoXproducto;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Etiquetado> listadoEtiquetado) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();
            ArrayAdapter<Etiquetado> adapter = new ArrayAdapter<>(ModificarProducto.this, android.R.layout.simple_spinner_dropdown_item, listadoEtiquetado);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            etiquetado_1.setAdapter(adapter);
            etiquetado_2.setAdapter(adapter);
            etiquetado_3.setAdapter(adapter);

        }

    }


    private class obtenerEtiquetadosXproducto extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {

            Conexion con = new Conexion();
            try {
                listaEtiquetados = con.obtenerListadoEtiquetadoXproducto(producto[0]);

                return listaEtiquetados;
            } catch (Exception e) {
                e.printStackTrace();
                return listaEtiquetados;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Etiquetado> listadoEtiquetado) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listadoEtiquetado.size() > 0) {



                if (listadoEtiquetado.size() > 0) {
                    etiquetado_1.setSelection(listadoEtiquetado.get(0).getIdEtiquetado());
                    if (listadoEtiquetado.size() > 1)
                        etiquetado_2.setSelection(listadoEtiquetado.get(1).getIdEtiquetado());
                    if (listadoEtiquetado.size() > 2)
                        etiquetado_2.setSelection(listadoEtiquetado.get(2).getIdEtiquetado());
                } else {
                    Toast.makeText(ModificarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(ModificarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void ModificarProductoCancelar(View view) {
        Intent modificarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(modificarProductoCancelar);
    }

    public void ModificarProductoModificar(View view) {
        Intent modificarProductoModificar = new Intent(this, Mis_Productos.class);
        startActivity(modificarProductoModificar);
    }

}