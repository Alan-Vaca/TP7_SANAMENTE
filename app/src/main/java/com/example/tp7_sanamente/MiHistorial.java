package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Historial;
import Entidad.Usuario;

public class MiHistorial extends AppCompatActivity {

    ListView lv_historial;
    ArrayList<Historial> listadoHistorial;
    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_historial);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        lv_historial = (ListView)findViewById(R.id.grd_Historial);

        listadoHistorial = new ArrayList<Historial>();

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            new MiHistorial.obtenerListadoHistorial().execute(user);
        }else{
            Toast.makeText(MiHistorial.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


    }

    private class obtenerListadoHistorial extends AsyncTask<Usuario, Void, ArrayList<Historial>> {
        @Override
        protected ArrayList<Historial> doInBackground(Usuario... usuario) {

            Conexion con = new Conexion();
            try {
                listadoHistorial = con.obtenerListadoHistorial(usuario[0]);

                return listadoHistorial;
            } catch (Exception e) {
                e.printStackTrace();
                return listadoHistorial;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Historial> listaHistorial) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listaHistorial.size() > 0) {

                listadoHistorial = listaHistorial;
                ArrayAdapter<Historial> adapter = new ArrayAdapter<>(MiHistorial.this, android.R.layout.simple_spinner_dropdown_item, listadoHistorial);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                lv_historial.setAdapter(adapter);

            } else {
                Toast.makeText(MiHistorial.this, "HUBO UN ERROR AL CONSULTAR LOS HISTORIALES", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void FiltrosHistorial(View view) {
        //Se usa el mismo que el de pedidos
        Intent FiltrosHistorial = new Intent(this, Filtros_Pedidos.class);
        startActivity(FiltrosHistorial);
    }
    public void DetalleHistorial(View view) {
        Intent DetalleHistorial = new Intent(this, Detalle_Pedido.class);
        startActivity(DetalleHistorial);
    }

    public void VolverAlMenu(View view) {
        if(user.isCliente()){
            MenuCliente(view);
        }else{
            MenuComercio(view);
        }
    }

    public void MenuCliente(View view) {
        Intent MenuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(MenuCliente);
    }

    public void MenuComercio(View view) {
        Intent MenuComercio = new Intent(this, MenuComercio.class);
        startActivity(MenuComercio);
    }
}