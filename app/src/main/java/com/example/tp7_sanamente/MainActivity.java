
package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class MainActivity extends AppCompatActivity {

    boolean isAdmin;
    EditText contraseña;
    EditText usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAdmin = false;

        contraseña = (EditText)findViewById(R.id.ContraseñaLogin);
        usuario = (EditText)findViewById(R.id.UsuarioLogin);

        //En caso de haber tenido un error anteriormente
        new cerrarConexion().execute(true);


        usuario.addTextChangedListener(new TextWatcher() {
            // ...

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validarUsuarioYContraseña();
            }
        });

        contraseña.addTextChangedListener(new TextWatcher() {
            // ...

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validarUsuarioYContraseña();
            }
        });

        Button btnExit = findViewById(R.id.btnExit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual
            }
        });
    }

    private void validarUsuarioYContraseña() {
        String user = usuario.getText().toString();
        String pass = contraseña.getText().toString();

        if (user.equals("admin") && pass.equals("admin")) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }
    }


    public void Registrarse(View view){
        Intent registrarse = new Intent(this, Registrar_Usuario.class);
        startActivity(registrarse);
    }

    public void Ingresar(View view) {


        String user;
        String pass;
        user = usuario.getText().toString();
        pass = contraseña.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(user);
        usuario.setContraseña(pass);



        //INICIO DEL BLOQUE PARA MOSTRAR UN MENSAJE DE CONFIRMACION PERSONALIZADO
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        //SETEO EL MENSAJE QUE QUIERO PREGUNTAR
        mensajeConfirm.setText("¿ESTAS SEGURO QUE TE QUIERES LOGUEAR? SOY UN MENSAJE DE PRUEBA LUEGO ELIMINAME");
        //ABRO EL POPUP
        final AlertDialog dialog = builder.create();
        dialog.show();

        //QUE HAGO EN CASO DE CANCELAR
        btnCancelarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EN ESTE CASO NO HAGO NADA
                Toast.makeText(MainActivity.this, "ELEGISTE" + "\n" + "CANCELAR", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        //QUE HAGO EN CASO DE CONFIRMAR
        btnConfirmarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EN ESTE CASO ME LOGUEO COMO EJEMPLO DE UNA ACCION
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAdmin", isAdmin);
                editor.apply();
                if(!isAdmin){
                    new obtenerUsuarioXloginTask().execute(usuario);
                }
                else{

                    Intent ingresarAdmin = new Intent(MainActivity.this, MenuAdmin.class);
                    startActivity(ingresarAdmin);
                }
                dialog.dismiss();
            }
        });


    }



    private class cerrarConexion extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... bool) {
            Conexion con = new Conexion();
            try {
                con.cerrarConexion();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
        }
    }

    private class obtenerUsuarioXloginTask extends AsyncTask<Usuario, Void, Usuario> {
        @Override
        protected Usuario doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Usuario us = new Usuario();
            try {
                us = con.obtenerUsuarioXlogin(user[0]);

                return us;
            } catch (Exception e) {
                e.printStackTrace();
                return us;
            }
        }

        @Override
        protected void onPostExecute(Usuario user) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (user.getIdUsuario() > 0) {
                ArrayList<pedidoXproducto> listadoCarrito = new ArrayList<pedidoXproducto>();

                SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorCarrito = preferences.edit();
                Gson gsonCarrito = new Gson();
                String listaComoJson = gsonCarrito.toJson(listadoCarrito);
                editorCarrito.putString("listadoCarrito", listaComoJson);
                editorCarrito.apply();


                ArrayList<Producto> listaFiltrada = new ArrayList<Producto>();
                SharedPreferences preferencesFiltro = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltro = preferencesFiltro.edit();
                Gson gsonFiltro = new Gson();
                String listaComoJsonFiltrada = gsonFiltro.toJson(listaFiltrada);
                editorFiltro.putString("listadoProductosFiltrados", listaComoJsonFiltrada);
                editorFiltro.apply();


                ArrayList<Historial> listadoHistorialesFiltrado = new ArrayList<Historial>();
                SharedPreferences preferencesFiltradoHistorial = getSharedPreferences("mi_prefHistorial", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltradoHistorial = preferencesFiltradoHistorial.edit();
                Gson gsonFiltradoHistorial = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();
                String listaComoJsonFiltradosHistorial = gsonFiltradoHistorial.toJson(listadoHistorialesFiltrado);
                editorFiltradoHistorial.putString("listadoHistorialesFiltrado", listaComoJsonFiltradosHistorial);
                editorFiltradoHistorial.apply();


                ArrayList<Pedido> listadPedidosFiltrado = new ArrayList<Pedido>();
                SharedPreferences preferencesFiltradoPedido = getSharedPreferences("mi_prefPedido", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorFiltradoPedido = preferencesFiltradoPedido.edit();

                Gson gsonFiltradoPedido = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();
                String listaComoJsonFiltradosPedido = gsonFiltradoPedido.toJson(listadPedidosFiltrado);
                editorFiltradoPedido.putString("listadoPedidosFiltrado", listaComoJsonFiltradosPedido);
                editorFiltradoPedido.apply();




                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String usuarioJson = gson.toJson(user);
                editor.putString("usuarioLogueado", usuarioJson);
                editor.apply();

                if (user.isCliente()) {
                    Intent ingresarcliente = new Intent(MainActivity.this, Menu_Cliente.class);
                    startActivity(ingresarcliente);
                } else {
                    Intent ingresarcomercio = new Intent(MainActivity.this, MenuComercio.class);
                    startActivity(ingresarcomercio);
                }
            } else {
                Toast.makeText(MainActivity.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES " + "\n" + "O ASEGURECE DE NO ESTAR DADO DE BAJA", Toast.LENGTH_LONG).show();
            }
        }

    }

}