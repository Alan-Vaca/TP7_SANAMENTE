package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

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

public class Registrar_Usuario extends AppCompatActivity {

    Switch esComerciante;
    Boolean Existe;

    EditText user,pass,pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        esComerciante = (Switch)findViewById(R.id.swregistrarComoComerciante);

        user = (EditText)findViewById(R.id.rgUsuario);
        pass = (EditText)findViewById(R.id.rgContraseña);
        pass2 = (EditText)findViewById(R.id.rgContraseña2);
        Existe = false;
    }


    public void Continuar(View view) {
        boolean comerciante = esComerciante.isChecked();

        String userTxt = user.getText().toString();


        String passTxt = pass.getText().toString();
        String pass2Txt = pass2.getText().toString();

        Usuario userNew = new Usuario();

        if(userTxt.isEmpty()){

            Toast toast = Toast.makeText(Registrar_Usuario.this, "DEBE INGRESAR UN NOMBRE DE USUARIO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return;
        }
        else{
            new Registrar_Usuario.ExisteUsuario().execute(userTxt);
        }
        if(Existe){

            Toast toast = Toast.makeText(Registrar_Usuario.this, "EL USUARIO YA EXISTE, POR FAVOR ELIJA OTRO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }

    }


    public void CancelarRegistro(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }

    private class ExisteUsuario extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... user) {
            Conexion con = new Conexion();
            try {
                return con.ExisteUsuario(user[0]);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean existe) {


            if (existe) {
                Existe = true;
            }
            else{
                boolean comerciante = esComerciante.isChecked();
                String userTxt = user.getText().toString();
                String passTxt = pass.getText().toString();
                String pass2Txt = pass2.getText().toString();

                Usuario userNew = new Usuario();

                if(userTxt.equals(pass2Txt)){

                    Toast toast = Toast.makeText(Registrar_Usuario.this, "El usuario y la contraseña no pueden ser iguales", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    return;
                }
                if(passTxt.equals(pass2Txt)){
                    userNew.setNombreUsuario(userTxt);
                    userNew.setContraseña(passTxt);

                    if (comerciante) {
                        Intent registrarComerciante = new Intent(Registrar_Usuario.this, Registrar_Comercio.class);
                        registrarComerciante.putExtra("usuarioInsert",userNew);
                        startActivity(registrarComerciante);
                    } else {
                        Intent registrarCliente = new Intent(Registrar_Usuario.this, Registrar_Cliente.class);
                        registrarCliente.putExtra("usuarioInsert",userNew);
                        startActivity(registrarCliente);
                    }
                }else{

                    Toast toast = Toast.makeText(Registrar_Usuario.this, "LAS CONTRASEÑAS DEBEN COINCIDIR", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                }
            }
        }

    }
}