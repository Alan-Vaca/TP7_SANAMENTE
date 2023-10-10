package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Etiquetado;
import Entidad.Producto;

public class ModificarProducto extends AppCompatActivity {

    Spinner etiquetado_1, etiquetado_2,etiquetado_3;
    ArrayList<Etiquetado> listaEtiquetados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);

        etiquetado_1 = (Spinner)findViewById(R.id.ap_sp_item1);
        etiquetado_2 = (Spinner)findViewById(R.id.ap_sp_item2);
        etiquetado_3 = (Spinner)findViewById(R.id.ap_sp_item3);

        listaEtiquetados = new ArrayList<Etiquetado>();

        new ModificarProducto.obtenerListadoEtiquetado().execute();

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

                ArrayAdapter<Etiquetado> adapter = new ArrayAdapter<>(ModificarProducto.this, android.R.layout.simple_spinner_dropdown_item, listadoEtiquetado);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                etiquetado_1.setAdapter(adapter);
                etiquetado_2.setAdapter(adapter);
                etiquetado_3.setAdapter(adapter);

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