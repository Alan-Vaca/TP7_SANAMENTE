package com.example.tp7_sanamente;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Comercio;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.Usuario;

public class AgregarProducto extends AppCompatActivity {

    EditText nombreProducto, ingredientes, precio, inventario;
    Spinner etiquetado1, etiquetado2, etiquetado3;
    ArrayList<Etiquetado> listaEtiquetados;
    Usuario user;
    Comercio comercio;

    int idEtiquetado1,idEtiquetado2,idEtiquetado3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        nombreProducto = (EditText)findViewById(R.id.ap_et_nombreDelProducto);
        ingredientes = (EditText)findViewById(R.id.ap_et_ingredientes);
        precio = (EditText)findViewById(R.id.ap_n_precio);
        inventario = (EditText)findViewById(R.id.ap_n_stock);

        etiquetado1 = (Spinner)findViewById(R.id.ap_sp_item1);
        etiquetado2 = (Spinner)findViewById(R.id.ap_sp_item2);
        etiquetado3 = (Spinner)findViewById(R.id.ap_sp_item3);

        idEtiquetado1 = 0;
        idEtiquetado2 = 0;
        idEtiquetado3 = 0;

        listaEtiquetados = new ArrayList<Etiquetado>();

        new AgregarProducto.obtenerListadoEtiquetado().execute();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        comercio = new Comercio();
        user = new Usuario();


        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                new AgregarProducto.cargarComercio().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(AgregarProducto.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AgregarProducto.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    private class obtenerListadoEtiquetado extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {

            Conexion con = new Conexion();
            try {
                listaEtiquetados = con.obtenerListadoEtiquetado();

                return listaEtiquetados;
            } catch (Exception e) {
                e.printStackTrace();
                return listaEtiquetados;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Etiquetado> listadoEtiquetado) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listadoEtiquetado.size() > 0) {

                ArrayAdapter<Etiquetado> adapter = new ArrayAdapter<>(AgregarProducto.this, android.R.layout.simple_spinner_dropdown_item, listadoEtiquetado);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                etiquetado1.setAdapter(adapter);
                etiquetado2.setAdapter(adapter);
                etiquetado3.setAdapter(adapter);

            } else {
                Toast.makeText(AgregarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
            }
        }

    }



    public void AgregarProductoCancelar(View view) {
        Intent agregarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(agregarProductoCancelar);
    }

    public void AgregarProductoAgregar(View view) {

        //if(confirmar("DESEA AGREGAR UN NUEVO PRODUCTO?")) {

            Producto producto = new Producto();

            producto.setNombre(nombreProducto.getText().toString());
            producto.setIngredientes(ingredientes.getText().toString());
            producto.setStock(Integer.parseInt(inventario.getText().toString()));
            producto.setPrecio(Float.parseFloat(precio.getText().toString()));

            idEtiquetado1 = etiquetado1.getSelectedItemPosition();
            idEtiquetado2 = etiquetado2.getSelectedItemPosition();
            idEtiquetado3 = etiquetado3.getSelectedItemPosition();

            if (validarProducto(producto, idEtiquetado1, idEtiquetado2, idEtiquetado3)) {
                new AgregarProducto.agregarProducto().execute(producto);
            }
        //}
    }

    public boolean validarProducto(Producto producto, int id1, int id2, int id3){
        boolean isValid = true;
        String productNameTxt = nombreProducto.getText().toString();
        String ingredientsTxt = ingredientes.getText().toString();
        int price = 0;
        int stock = 0;


        // Validar nombre de producto
        if (productNameTxt.isEmpty()) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese el nombre del producto.", Toast.LENGTH_LONG).show();
        }

        // Validar que contenga al menos un ingrediente
        if (ingredientsTxt.isEmpty()) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese al menos un ingrediente.", Toast.LENGTH_LONG).show();
        }

        // Validar que el precio sea mayor a cero
        try {
            price = Integer.parseInt(precio.getText().toString());
            if (price <= 0) {
                isValid = false;
                Toast.makeText(AgregarProducto.this, "El precio debe ser mayor a cero.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese un precio válido.", Toast.LENGTH_LONG).show();
        }

        // Validar que el stock sea mayor o igual a cero
        try {
            stock = Integer.parseInt(inventario.getText().toString());
            if (stock < 0) {
                isValid = false;
                Toast.makeText(AgregarProducto.this, "El stock no puede ser negativo.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast.makeText(AgregarProducto.this, "Por favor, ingrese un valor de inventario válido.", Toast.LENGTH_LONG).show();
        }


        return isValid;
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
        protected void onPostExecute(Comercio comercioObtenido) {
            if (comercioObtenido.getIdComercio() > 0) {
                comercio = comercioObtenido;
                comercio.setUsuarioAsociado(user);
            } else {
                Toast.makeText(AgregarProducto.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class agregarProducto extends AsyncTask<Producto, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Producto... producto) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.altaProducto(producto[0],comercio,idEtiquetado1,idEtiquetado2,idEtiquetado3);
            } catch (Exception e) {
                e.printStackTrace();
                return exito;
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                // Recuperar el booleano isAdmin de SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

                if(isAdmin) {
                    // El usuario es un administrador, realiza las acciones correspondientes
                    Toast.makeText(AgregarProducto.this, "PRODUCTO AGREGADO CON EXITO", Toast.LENGTH_LONG).show();
                    Intent volverAlmenu = new Intent(AgregarProducto.this, MenuAdmin.class);
                    startActivity(volverAlmenu);
                } else {
                    // El usuario no es un administrador, realiza las acciones correspondientes
                    Toast.makeText(AgregarProducto.this, "PRODUCTO AGREGADO CON EXITO", Toast.LENGTH_LONG).show();
                    Intent volverAlmenu = new Intent(AgregarProducto.this, MenuComercio.class);
                    startActivity(volverAlmenu);
                }
            } else {
                Toast.makeText(AgregarProducto.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }


    public Boolean confirmar(String mensaje){
        final boolean[] resultado = {false}; // Utilizamos un array para poder modificar el valor desde el diálogo

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultado[0] = true;
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultado[0] = false;
                    }
                });
        builder.create().show();

        return resultado[0];
    }
}