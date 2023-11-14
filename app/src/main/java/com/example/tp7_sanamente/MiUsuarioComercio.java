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
import Entidad.Comercio;
import Entidad.Usuario;

public class MiUsuarioComercio extends AppCompatActivity {

    Usuario user;
    Comercio comercio;

    TextView nombreComercio, cuit, direccion, dni, nombreApellido, horarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario_comercio);

        nombreApellido = (TextView)findViewById(R.id.mi_nombreApellido);
        dni = (TextView)findViewById(R.id.mi_dni);
        direccion = (TextView)findViewById(R.id.mi_direccion);
        horarios = (TextView)findViewById(R.id.mi_comercio_horas_);
        cuit = (TextView)findViewById(R.id.mi_cuit);
        nombreComercio = (TextView)findViewById(R.id.mi_nombreComercio);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        comercio = new Comercio();
        user = new Usuario();


        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                nombreApellido.setText(user.getApellido().toUpperCase() + ", " + user.getNombre().toUpperCase());
                String dniTxt = String.valueOf(user.getDNI());
                dni.setText("DNI" + '\n' + dniTxt);
                direccion.setText("DIRECCION" + '\n' + user.getDireccion());

                new MiUsuarioComercio.cargarComercio().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();

                Toast toast = Toast.makeText(MiUsuarioComercio.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();            }
        }else{

            Toast toast = Toast.makeText(MiUsuarioComercio.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }

    public void MenuComercio(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent menuComercio = new Intent(this, MenuAdmin.class);
            startActivity(menuComercio);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            Intent menuComercio = new Intent(this, MenuComercio.class);
            startActivity(menuComercio);
        }
    }

    public void MenuMi_Comercio(View view) {
        Intent MenuMi_Comercio = new Intent(this, Mi_Comercio.class);
        startActivity(MenuMi_Comercio);
    }



    private class cargarComercio extends AsyncTask<Usuario, Void, Comercio> {
        @Override
        protected Comercio doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Comercio comercio = new Comercio();
            try {
                comercio = con.obtenerComercio(user[0].getIdUsuario());
                return comercio;
            } catch (Exception e) {
                e.printStackTrace();
                return comercio;
            }
        }

        @Override
        protected void onPostExecute(Comercio comercio) {
            if (comercio.getIdComercio() > 0) {
                String cuits = String.valueOf(comercio.getCuit());
                cuit.setText("CUIT:" + '\n' + cuits);
                nombreComercio.setText(comercio.getNombreComercio().toString());
                horarios.setText("HORARIOS: " + comercio.getHorarios().replace("-:-","Hs A ") + "Hs");
            } else {

                Toast toast = Toast.makeText(MiUsuarioComercio.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }

    public void DarDeBaja(View view){
        if(user.getNombreUsuario().equals("admin") && (user.getContraseña().equals("123") || user.getContraseña().equals("321"))){

            Toast toast = Toast.makeText(MiUsuarioComercio.this, "NO ES POSIBLE DAR DE BAJA UN USUARIO ADMIN", Toast.LENGTH_LONG);
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

            mensajeConfirm.setText("¿ESTAS SEGURO QUE QUIERES DAR DE BAJA EL USUARIO COMERCIO?");

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
                    new MiUsuarioComercio.BajaUsuarioComercio().execute(user);
                    dialog.dismiss();
                }
            });

        }
    }

    private class BajaUsuarioComercio extends AsyncTask<Usuario, Void, Boolean> {
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

                Toast toast = Toast.makeText(MiUsuarioComercio.this, "COMERCIO DADO DE BAJA CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
                Intent volverAlLogin = new Intent(MiUsuarioComercio.this, MainActivity.class);
                startActivity(volverAlLogin);
            } else {

                Toast toast = Toast.makeText(MiUsuarioComercio.this, "ERROR AL DAR DE BAJA", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }

}