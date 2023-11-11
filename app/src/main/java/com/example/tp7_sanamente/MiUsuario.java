package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import BaseDeDatos.Conexion;
import Entidad.Cliente;
import Entidad.Producto;
import Entidad.Usuario;

public class MiUsuario extends AppCompatActivity {

    TextView usuario,nombre,apellido,dni;
    Button MiUsuarioDarDeBaja;
    Usuario user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario);

        usuario = findViewById(R.id.fc_tv_tituloCheckbox);
        nombre = findViewById(R.id.miNombre);
        apellido = findViewById(R.id.fc_tv_noContiene);
        dni = findViewById(R.id.miDni);
        //MiUsuarioDarDeBaja = (Button)findViewById(R.id.Mi);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

         user = new Usuario();

        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                usuario.setText(user.getNombreUsuario());
                nombre.setText(user.getNombre());
                apellido.setText(user.getApellido());
                String dniTxt = String.valueOf(user.getDNI());
                dni.setText(dniTxt);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();

                Toast toast = Toast.makeText(MiUsuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }else{

            Toast toast = Toast.makeText(MiUsuario.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }

    public void MenuModificarUsuario(View view) {
        Intent menuModificarUsuario = new Intent(this, Modificar_Usuario.class);
        startActivity(menuModificarUsuario);
    }

    public void MenuCliente(View view) {
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

    public void DarDeBaja(View view){
        if(user.getNombreUsuario().equals("admin") && (user.getContraseña().equals("123") || user.getContraseña().equals("321"))){

            Toast toast = Toast.makeText(MiUsuario.this, "NO ES POSIBLE DAR DE BAJA UN USUARIO ADMIN", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
            builder.setView(dialogView);
            final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
            Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
            Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

            mensajeConfirm.setText("¿ESTAS SEGURO QUE QUIERES DAR DE BAJA EL USUARIO CLIENTE?");

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
                    Cliente cliente = new Cliente();
                    new MiUsuario.BajaUsuario().execute(user);
                    dialog.dismiss();
                }
            });
        }
    }

    private class BajaUsuario extends AsyncTask<Usuario, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Usuario... usuario) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.BajaUsuario(usuario[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return exito;
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {

                Toast toast = Toast.makeText(MiUsuario.this, "USUARIO DADO DE BAJA CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
                Intent volverAlLogin = new Intent(MiUsuario.this, MainActivity.class);
                startActivity(volverAlLogin);
            } else {

                Toast toast = Toast.makeText(MiUsuario.this, "ERROR AL DAR DE BAJA", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }


}