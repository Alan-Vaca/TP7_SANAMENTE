package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import BaseDeDatos.Conexion;
import Entidad.Notificacion;
import Entidad.Usuario;

public class MisNotificaciones extends AppCompatActivity {

    Switch pedidos,productos,ofertas;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_notificaciones);

        pedidos = (Switch)findViewById(R.id.switch_pedidos);
        productos = (Switch)findViewById(R.id.switch_productos);
        ofertas = (Switch)findViewById(R.id.switch_ofertas);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();

        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);

                new MisNotificaciones.cargarNotificaciones().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();

                Toast toast = Toast.makeText(MisNotificaciones.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }else{

            Toast toast = Toast.makeText(MisNotificaciones.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }

    private class cargarNotificaciones extends AsyncTask<Usuario, Void, Notificacion> {
        @Override
        protected Notificacion doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Notificacion notificacion = new Notificacion();
            try {
                notificacion = con.obtenerNotificaciones(user[0]);
                return notificacion;
            } catch (Exception e) {
                e.printStackTrace();
                return notificacion;
            }
        }

        @Override
        protected void onPostExecute(Notificacion notificacion) {
            if (notificacion.getIdNotificacion() > 0) {
;               pedidos.setChecked(notificacion.isPedidos());
                ofertas.setChecked(notificacion.isOfertas());
                productos.setChecked(notificacion.isProductos());
            } else {

                Toast toast = Toast.makeText(MisNotificaciones.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }

    public void ModificarNotificaciones(View view){
        Notificacion notificacion = new Notificacion();
        notificacion.setProductos(productos.isChecked());
        notificacion.setOfertas(ofertas.isChecked());
        notificacion.setPedidos(pedidos.isChecked());

        notificacion.setUsuarioAsociado(user);
        new MisNotificaciones.NotificacionesGrabar().execute(notificacion);
    }

    public void MenuCliente(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent menuCliente = new Intent(this, MenuAdmin.class);
            startActivity(menuCliente);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            Intent menuCliente = new Intent(this, Menu_Cliente.class);
            startActivity(menuCliente);
        }
    }

    private class NotificacionesGrabar extends AsyncTask<Notificacion, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Notificacion... notificacion) {
            Conexion con = new Conexion();
            try {
                return con.ModificarNotificacion(notificacion[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){

                Toast toast = Toast.makeText(MisNotificaciones.this, "NOTIFICACIONES GRABADAS CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
                // Recuperar el booleano isAdmin de SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

                if(isAdmin) {
                    // El usuario es un administrador, realiza las acciones correspondientes
                    Intent menuCliente = new Intent(MisNotificaciones.this, MenuAdmin.class);
                    startActivity(menuCliente);
                } else {
                    // El usuario no es un administrador, realiza las acciones correspondientes
                    Intent menuCliente = new Intent(MisNotificaciones.this, Menu_Cliente.class);
                    startActivity(menuCliente);
                }
            } else {

                Toast toast = Toast.makeText(MisNotificaciones.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }
}