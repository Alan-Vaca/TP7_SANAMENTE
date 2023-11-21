package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import BaseDeDatos.consultasReportes;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Reporte;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Menu_Cliente extends AppCompatActivity {
    ImageView notificacionSI, notificacionNO;
    String noti_msj;
    Usuario user;
    ArrayList<Producto> listaProductosOfertas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);
        noti_msj = "";
        notificacionNO = findViewById(R.id.image_not_no);
        notificacionSI = findViewById(R.id.image_not_si);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            new Menu_Cliente.obtenerMSJNotificaciones().execute(user);
        }else{
            Toast toast = Toast.makeText(Menu_Cliente.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
        }



    }

    public void Notificaciones(View view) {
      Intent ingresarNotificaciones = new Intent(this, MisNotificaciones.class);
      startActivity(ingresarNotificaciones);
    }

    public void MenuMiUsuario(View view) {
        Intent menuMiUsuario = new Intent(this, MiUsuario.class);
        startActivity(menuMiUsuario);
    }

    public void MenuHistorial(View view) {
        Intent menuCliente = new Intent(this, MiHistorial.class);
        startActivity(menuCliente);
    }

    public void MenuCatalogo(View view) {
        Intent menuCatalogo = new Intent(this, Mis_Productos.class);
        startActivity(menuCatalogo);
    }


    public void MenuMiCarrito(View view) {
        Intent menuMiCarrito = new Intent(this, MiCarritoCompras.class);
        startActivity(menuMiCarrito);
    }


    public void MenuOfertas(View view) {
        new Menu_Cliente.obtenerOfertas().execute(user);

/*
        Toast toast = Toast.makeText(Menu_Cliente.this, "TUS OFERTAS:" + '\n' + "ITEM 1", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
        toast.show(); // Mostrar el Toast
*/
    }


    public void MenuPrincipal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        mensajeConfirm.setText("¿ESTAS SEGURO DE SALIR?");
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
                Intent menuPrincipal = new Intent(Menu_Cliente.this, MainActivity.class);
                startActivity(menuPrincipal);
                dialog.dismiss();
            }
        });


    }


    private class obtenerOfertas extends AsyncTask<Usuario, Void, ArrayList<Producto>> {
        @Override
        protected ArrayList<Producto> doInBackground(Usuario... usuario) {
            listaProductosOfertas = new ArrayList<>();

            Conexion con = new Conexion();
            try {
                listaProductosOfertas = con.obtenerListadoProductosOfertas(usuario[0]);
                Log.d("OFERTAS1", String.valueOf(listaProductosOfertas));

                return listaProductosOfertas;
            } catch (Exception e) {
                e.printStackTrace();
                return listaProductosOfertas;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Producto> listaProductosOfertas) {

            try {
                SharedPreferences preferencesOfertas = getSharedPreferences("mi_prefer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorOfertas = preferencesOfertas.edit();
                Gson gsonOfertas = new Gson();
                String listaComoJsonOfertas = gsonOfertas.toJson(listaProductosOfertas);
                editorOfertas.putString("listaProductosOfertasObtenida", listaComoJsonOfertas);
                editorOfertas.apply();

            }
            catch (Exception e) {
                Log.d("Filtro.enviar", e.toString());
            }

            Intent intent = new Intent(Menu_Cliente.this, Mis_Productos.class);
            startActivity(intent);
            finish();
        }

    }


    public void NotificacionesSI(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_notificaciones, null);
        builder.setView(dialogView);

        final EditText notificacionesMSJ = dialogView.findViewById(R.id.editTextNotificaciones);
        Button btnAceptarNotificaciones = dialogView.findViewById(R.id.btnAceptarNotificaciones);

        notificacionesMSJ.setText(noti_msj);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAceptarNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    public void NotificacionesNO(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_notificaciones, null);
        builder.setView(dialogView);

        final EditText notificacionesMSJ = dialogView.findViewById(R.id.editTextNotificaciones);
        Button btnAceptarNotificaciones = dialogView.findViewById(R.id.btnAceptarNotificaciones);

        notificacionesMSJ.setText(
                "NO HAY NOTIFICACIONES NUEVAS" + '\n' + "VERIFICA TU CONFIGURACION DE NOTIFICACIONES."
        );
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAceptarNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    private class obtenerMSJNotificaciones extends AsyncTask<Usuario, Void, String> {
        @Override
        protected String doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            try {
                return con.obtenerMSJNotificaciones(user[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String msj) {

            if (!msj.isEmpty()) {
                noti_msj = msj;
                notificacionSI.setVisibility(View.VISIBLE);
                notificacionNO.setVisibility(View.INVISIBLE);
            } else {
                notificacionNO.setVisibility(View.VISIBLE);
                notificacionSI.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void ConsultaOfertas(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu_Cliente.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_notificaciones, null);
        builder.setView(dialogView);

        final EditText notificacionesMSJ = dialogView.findViewById(R.id.editTextNotificaciones);
        Button btnAceptarNotificaciones = dialogView.findViewById(R.id.btnAceptarNotificaciones);

        String consulta = "OFERTAS!!" + '\n' + '\n';
        consulta += "Hemos seleccionado los productos mejor calificados y con mejores precios para vos" + '\n' + '\n';

        notificacionesMSJ.setText(consulta);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAceptarNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}