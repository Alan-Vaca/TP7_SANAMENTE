package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Entidad.Usuario;

public class Registrar_Usuario extends AppCompatActivity {

    Switch esComerciante;

    EditText user,pass,pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        esComerciante = (Switch)findViewById(R.id.swregistrarComoComerciante);

        user = (EditText)findViewById(R.id.rgUsuario);
        pass = (EditText)findViewById(R.id.rgContraseña);
        pass2 = (EditText)findViewById(R.id.rgContraseña2);
    }


    public void Continuar(View view) {
        boolean comerciante = esComerciante.isChecked();

        String userTxt = user.getText().toString();
        String passTxt = pass.getText().toString();
        String pass2Txt = pass2.getText().toString();

        Usuario userNew = new Usuario();

        if(userTxt.isEmpty()){
            Toast.makeText(Registrar_Usuario.this, "DEBE INGRESAR UN NOMBRE DE USUARIO", Toast.LENGTH_LONG).show();
            return;
        }
        if(passTxt.equals(pass2Txt)){
            userNew.setNombreUsuario(userTxt);
            userNew.setContraseña(passTxt);

            if (comerciante) {
                Intent registrarComerciante = new Intent(this, Registrar_Comercio.class);
                registrarComerciante.putExtra("usuarioInsert",userNew);
                startActivity(registrarComerciante);
            } else {
                Intent registrarCliente = new Intent(this, Registrar_Cliente.class);
                registrarCliente.putExtra("usuarioInsert",userNew);
                startActivity(registrarCliente);
            }
        }else{
            Toast.makeText(Registrar_Usuario.this, "LAS CONTRASEÑAS DEBEN COINCIDIR", Toast.LENGTH_LONG).show();
        }
    }


    public void CancelarRegistro(View view) {
        Intent menuPrincipal = new Intent(this, MainActivity.class);
        startActivity(menuPrincipal);
    }
}