package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Etiquetado;
import Entidad.Producto;

public class AgregarProducto extends AppCompatActivity {

    EditText nombreProducto, ingredientes, precio, inventario;
    Spinner etiquetado1, etiquetado2, etiquetado3;
    ArrayList<Etiquetado> listaEtiquetados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        nombreProducto = (EditText)findViewById(R.id.ap_et_nombreDelProducto);
        ingredientes = (EditText)findViewById(R.id.ap_et_ingredientes);
        precio = (EditText)findViewById(R.id.ap_n_precio);
        inventario = (EditText)findViewById(R.id.ap_n_stock);

        etiquetado1 = (Spinner)findViewById(R.id.ap_sp_item1);
        etiquetado2 = (Spinner)findViewById(R.id.ap_sp_item2);
        etiquetado3 = (Spinner)findViewById(R.id.ap_sp_item3);

        listaEtiquetados = new ArrayList<Etiquetado>();

        new AgregarProducto.obtenerListadoEtiquetado().execute();

    }

    private class obtenerListadoEtiquetado extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {

            Conexion con = new Conexion();
            try {
                listaEtiquetados = con.obtenerListadoEtiquetado();

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

                ArrayAdapter<Etiquetado> adapter = new ArrayAdapter<>(AgregarProducto.this, android.R.layout.simple_spinner_dropdown_item, listadoEtiquetado);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                etiquetado1.setAdapter(adapter);
                etiquetado2.setAdapter(adapter);
                etiquetado3.setAdapter(adapter);

            } else {
                Toast.makeText(AgregarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
            }
        }

    }



    public void AgregarProductoCancelar(View view) {
        Intent agregarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(agregarProductoCancelar);
    }

    public void AgregarProductoAgregar(View view) {
        boolean isValid = true;
        String productNameTxt = nombreProducto.getText().toString();
        String ingredientsTxt = ingredientes.getText().toString();
        int price = 0;
        int stock = 0;
        

        // Validar nombre de producto
        if (productNameTxt.isEmpty()) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese el nombre del producto.", Toast.LENGTH_LONG).show();
        }

        // Validar que contenga al menos un ingrediente
        if (ingredientsTxt.isEmpty()) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese al menos un ingrediente.", Toast.LENGTH_LONG).show();
        }

        // Validar que el precio sea mayor a cero
        try {
            price = Integer.parseInt(precio.getText().toString());
            if (price <= 0) {
                isValid = false;
                Toast.makeText(AgregarProducto.this, "El precio debe ser mayor a cero.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese un precio válido.", Toast.LENGTH_LONG).show();
        }

        // Validar que el stock sea mayor o igual a cero
        try {
            stock = Integer.parseInt(inventario.getText().toString());
            if (stock < 0) {
                isValid = false;
                Toast.makeText(AgregarProducto.this, "El stock no puede ser negativo.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese un valor de inventario válido.", Toast.LENGTH_LONG).show();
        }


        if(isValid == true)
        {
            Intent agregarProductoAgregar = new Intent(this, Mis_Productos.class);
            startActivity(agregarProductoAgregar);
        }
    }
}