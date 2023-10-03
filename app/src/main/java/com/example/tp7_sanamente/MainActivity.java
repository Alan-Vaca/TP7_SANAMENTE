package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import BaseDeDatos.Conexion;
import Entidad.Usuario;

public class MainActivity extends AppCompatActivity {

    Switch esComerciante;
    EditText contraseña;
    EditText usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        esComerciante = (Switch)findViewById(R.id.sw_comerciante);
        contraseña = (EditText)findViewById(R.id.ContraseñaLogin);
        usuario = (EditText)findViewById(R.id.UsuarioLogin);
    }


    public void Registrarse(View view){
        Intent registrarse = new Intent(this, Registrar_Cliente.class);
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

        new obtenerUsuarioXloginTask().execute(usuario);

        /*LUEGO ESTE BLOQUE SE DEBE ELIMINAR (INICIO)*/
        boolean comerciante = esComerciante.isChecked();


        if (!comerciante) {
            Intent ingresarcliente = new Intent(this, Menu_Cliente.class);
            startActivity(ingresarcliente);
        } else {
            Intent ingresarcomercio = new Intent(this, MenuComercio.class);
            startActivity(ingresarcomercio);
        }
        /*LUEGO ESTE BLOQUE SE DEBE ELIMINAR (FINAL)*/
    }



    private class obtenerUsuarioXloginTask extends AsyncTask<Usuario, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            try {
                con.obtenerUsuarioXlogin(user[0]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exito) {
            if (exito) {
                boolean comerciante = esComerciante.isChecked();
                if (!comerciante) {
                    Intent ingresarcliente = new Intent(MainActivity.this, Menu_Cliente.class);
                    startActivity(ingresarcliente);
                } else {
                    Intent ingresarcomercio = new Intent(MainActivity.this, MenuComercio.class);
                    startActivity(ingresarcomercio);
                }
            } else {
                Toast.makeText(MainActivity.this, "Error al ingresar. Verifique sus credenciales.", Toast.LENGTH_LONG).show();
            }
        }

    }



}