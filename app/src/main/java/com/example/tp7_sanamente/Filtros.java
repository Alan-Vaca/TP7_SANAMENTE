package com.example.tp7_sanamente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import BaseDeDatos.consultasProductos;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Filtros extends AppCompatActivity {


    EditText filtroNombre, filtroContiene, filtroNoContiene;
    RadioButton rbCalificaciones, rbPrecio, rbReciente;
    CheckBox cbHipertenso, cbDiabetico, cbCeliaco;
    ListView lvFiltros;
    Usuario user;
    ArrayList<Producto> listaProductosObtenido;
    ArrayList<Producto> listaProductos;
    ArrayList<Producto> listaFiltrada;
    consultasProductos consultaProductos;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);


        filtroNombre = findViewById(R.id.fc_et_filtroNombre);
        cbHipertenso = findViewById(R.id.r_cb_hipertenso);
        cbDiabetico = findViewById(R.id.r_cb_diabetico);
        cbCeliaco = findViewById(R.id.r_cb_celiaco);
        filtroContiene = findViewById(R.id.r_com_nombre);
        filtroNoContiene = findViewById(R.id.r_com_direccion);
        rbCalificaciones = findViewById(R.id.fc_rb_calificaciones);
        rbPrecio = findViewById(R.id.fc_rb_precio);
        rbReciente = findViewById(R.id.fc_rb_reciente);

        listaProductos = new ArrayList<Producto>();
        consultaProductos = new consultasProductos();
        lvFiltros = findViewById(R.id.grd_catalogo);
        Button btnAplicarFiltros = findViewById(R.id.fc_bn_aplicarFiltros);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");
        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            new Filtros.obtenerListadoProducto().execute(user);
        }else{
            Toast.makeText(Filtros.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


        btnAplicarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aplicarFiltros(view);
            }
        });


        SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
        filtroNombre.setText(preferences.getString("filtroNombre", ""));
        filtroContiene.setText(preferences.getString("comNombre", ""));
        filtroNoContiene.setText(preferences.getString("comDireccion", ""));
        cbHipertenso.setChecked(preferences.getBoolean("cbHipertenso", false));
        cbDiabetico.setChecked(preferences.getBoolean("cbDiabetico", false));
        cbCeliaco.setChecked(preferences.getBoolean("cbCeliaco", false));
        rbCalificaciones.setChecked(preferences.getBoolean("rbCalificaciones", false));
        rbPrecio.setChecked(preferences.getBoolean("rbPrecio", false));
        rbReciente.setChecked(preferences.getBoolean("rbReciente", false));

    }

    public void VolverAProductos(View view) {

        Intent VolverAProductos = new Intent(this, Mis_Productos.class);
        startActivity(VolverAProductos);

    }

    public void QuitarFiltros(View view){
        filtroNombre.setText("");
        filtroContiene.setText("");
        filtroNoContiene.setText("");
        cbHipertenso.setChecked(false);
        cbDiabetico.setChecked(false);
        cbCeliaco.setChecked(false);
        rbCalificaciones.setChecked(false);
        rbPrecio.setChecked(false);
        rbReciente.setChecked(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aplicarFiltros(view);
            }
        }, 1000);
    }

    public void aplicarFiltros(View view) {

        String nombre = filtroNombre.getText().toString().trim();
        boolean hipertenso = cbHipertenso.isChecked();
        boolean diabetico = cbDiabetico.isChecked();
        boolean celiaco = cbCeliaco.isChecked();
        String contiene = filtroContiene.getText().toString().trim();
        String noContiene = filtroNoContiene.getText().toString().trim();

        String ordenarPor = "";
        if (rbCalificaciones.isChecked()) {
            ordenarPor = "calificaciones";
        } else if (rbPrecio.isChecked()) {
            ordenarPor = "precio";
        } else if (rbReciente.isChecked()) {
            ordenarPor = "reciente";
        }


         new AplicarFiltrosTask().execute(nombre, hipertenso, diabetico, celiaco, contiene,
               noContiene, ordenarPor);

        SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("filtroNombre", filtroNombre.getText().toString());
        editor.putString("comNombre", filtroContiene.getText().toString());
        editor.putString("comDireccion", filtroNoContiene.getText().toString());
        editor.putBoolean("cbHipertenso", cbHipertenso.isChecked());
        editor.putBoolean("cbDiabetico", cbDiabetico.isChecked());
        editor.putBoolean("cbCeliaco", cbCeliaco.isChecked());
        editor.putBoolean("rbCalificaciones", rbCalificaciones.isChecked());
        editor.putBoolean("rbPrecio", rbPrecio.isChecked());
        editor.putBoolean("rbReciente", rbReciente.isChecked());
        editor.apply();

    }

    private class AplicarFiltrosTask extends AsyncTask<Object, Void, ArrayList<Producto>> {
        @Override
        protected ArrayList<Producto> doInBackground(Object... params) {


            String filtroNombre = (String) params[0];
            boolean hipertenso = (boolean) params[1];
            boolean diabetico = (boolean) params[2];
            boolean celiaco = (boolean) params[3];
            String contiene = (String) params[4];
            String noContiene = (String) params[5];
            String ordenarPor = (String) params[6];


            if (hipertenso || diabetico || celiaco) {
                Conexion con = new Conexion();
                try {
                    listaProductosObtenido = con.obtenerListadoProductosConRestricciones(hipertenso, diabetico, celiaco);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                listaProductosObtenido = listaProductos;
            }


            listaProductosObtenido = consultaProductos.obtenerListadoProductosFiltrados(listaProductosObtenido,
                    filtroNombre, contiene, noContiene, ordenarPor);




            return listaProductosObtenido;

        }

        @Override
        protected void onPostExecute(ArrayList<Producto> listaProductosObtenido) {

                listaFiltrada = listaProductosObtenido;

                try {
                    SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String listaComoJson = gson.toJson(listaFiltrada);
                    editor.putString("listadoProductosFiltrados", listaComoJson);
                    editor.apply();
                }
                catch (Exception e) {
                    Log.d("Filtro.enviar", e.toString());
                }

                Intent intent = new Intent(Filtros.this, Mis_Productos.class);
                startActivity(intent);
                finish();
                /*
                ArrayAdapter<Producto> adapter = new ArrayAdapter<>(Filtros.this, android.R.layout.simple_spinner_dropdown_item, listaFiltrada);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvFiltros.setAdapter(adapter);
                 */
        }
    }

    private class obtenerListadoProducto extends AsyncTask<Usuario, Void, ArrayList<Producto>> {
        @Override
        protected ArrayList<Producto> doInBackground(Usuario... usuario) {

            Conexion con = new Conexion();
            try {
                listaProductos = con.obtenerListadoProductos(usuario[0]);
                return listaProductos;
            } catch (Exception e) {
                e.printStackTrace();
                return listaProductos;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Producto> listaProductosObtenido) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listaProductosObtenido.size() > 0) {
                listaProductos = listaProductosObtenido;
            }
        }
    }





}

/*
    public void FiltrosAplicarFiltros(View view) {
        Intent filtrosAplicarFiltros = new Intent(this, Mis_Productos.class);
        startActivity(filtrosAplicarFiltros);
    }
}
*/
