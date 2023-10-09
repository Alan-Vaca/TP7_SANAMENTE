package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import BaseDeDatos.Conexion;
import Entidad.Comercio;
import Entidad.Usuario;

public class Registrar_Comercio extends AppCompatActivity {

    TextView cuit,nombre,cierre,apertura, direccion, usuario_nombre, usuario_dni, usuario_apellido;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comercio);

        Usuario usuarioRecibido = getIntent().getParcelableExtra("usuarioInsert");
        user = usuarioRecibido;

        cuit = (EditText)findViewById(R.id.mod_c_usuario);
        nombre = (EditText)findViewById(R.id.r_com_nombre);
        cierre = (EditText)findViewById(R.id.r_com_cierre);
        apertura = (EditText)findViewById(R.id.r_com_apertura);
        direccion = (EditText)findViewById(R.id.r_com_direccion);

        usuario_nombre = (EditText)findViewById(R.id.r_com_nombre_user);
        usuario_dni = (EditText)findViewById(R.id.r_com_dni);
        usuario_apellido = (EditText)findViewById(R.id.r_com_apellido);

    }

    public void IngresarComercio(View view) {

        Comercio comercio = new Comercio();
        comercio.setCuit(Integer.parseInt(cuit.getText().toString()));
        comercio.setNombreComercio(nombre.getText().toString());
        String horarios = (apertura.getText().toString()) + "-:-" + (cierre.getText().toString());
        comercio.setHorarios(horarios);

        user.setDNI(Integer.parseInt((usuario_dni.getText().toString())));
        user.setNombre(usuario_nombre.getText().toString());
        user.setApellido(usuario_apellido.getText().toString());
        user.setDireccion(direccion.getText().toString());

        comercio.setUsuarioAsociado(user);
        if(validarComercio(comercio)) {
            new registrarComercio().execute(comercio);
        }
    }

    public boolean validarComercio(Comercio comercio){
        return true;
    }

    public void VolverRegistro(View view) {
        Intent VolverRegistro = new Intent(this, Registrar_Usuario.class);
        startActivity(VolverRegistro);
    }

    private class registrarComercio extends AsyncTask<Comercio, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Comercio... com) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.RegistrarUsuarioComercio(com[0]);

            } catch (Exception e) {
                e.printStackTrace();

            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {

            if(exito) {
                Toast.makeText(Registrar_Comercio.this, "COMERCIO AGREGADO CON EXITO", Toast.LENGTH_LONG).show();

                Intent IngresarComercio = new Intent(Registrar_Comercio.this, MainActivity.class);
                startActivity(IngresarComercio);

            }else {
                Toast.makeText(Registrar_Comercio.this, "HUBO UN ERROR", Toast.LENGTH_LONG).show();
            }
        }
    }
}