package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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
    Switch sw_hipertenso2, sw_celiaco2, sw_diabetico2;
    Boolean aptoHipertenso, aptoCeliaco,aptoDiabetico;
    Producto producto;

    boolean Existe;

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

        aptoHipertenso = true;
        aptoCeliaco = true;
        aptoDiabetico = true;

        sw_hipertenso2 = (Switch)findViewById(R.id.sw_hipertenso2);
        sw_celiaco2 = (Switch)findViewById(R.id.sw_celiaco2);
        sw_diabetico2 = (Switch)findViewById(R.id.sw_diabetico2);

        Existe = false;

        producto = new Producto();

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

                Toast toast = Toast.makeText(AgregarProducto.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
                toast.show(); // Mostrar el Toast
            }
        }else{
            Toast toast = Toast.makeText(AgregarProducto.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
        }

        etiquetado1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                validarRestricciones(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        etiquetado2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                validarRestricciones(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        etiquetado3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                validarRestricciones(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }

    private void validarRestricciones(int position) {

        int idEtiquetado = 0;

        aptoCeliaco = true;
        aptoHipertenso = true;
        aptoDiabetico = true;

        idEtiquetado = etiquetado1.getSelectedItemPosition();
        if(idEtiquetado <= 5 && idEtiquetado > 0) aptoCeliaco = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4) aptoHipertenso = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4 || idEtiquetado == 1) aptoDiabetico = false;

        idEtiquetado = etiquetado2.getSelectedItemPosition();
        if(idEtiquetado <= 5 && idEtiquetado > 0) aptoCeliaco = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4) aptoHipertenso = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4 || idEtiquetado == 1) aptoDiabetico = false;

        idEtiquetado = etiquetado3.getSelectedItemPosition();
        if(idEtiquetado <= 5 && idEtiquetado > 0) aptoCeliaco = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4) aptoHipertenso = false;
        if(idEtiquetado >= 6 || idEtiquetado == 4 || idEtiquetado == 1) aptoDiabetico = false;

        sw_hipertenso2.setChecked(aptoHipertenso);
        sw_celiaco2.setChecked(aptoCeliaco);
        sw_diabetico2.setChecked(aptoDiabetico);



            String ingredientess = ingredientes.getText().toString();

            // Lista de ingredientes no aptos para celíacos
            String[] ingredientesNoAptos = {"Harina","Trigo", "Cebada", "Centeno", "Avena", "Malta", "Extracto de Malta", "Harina de espelta", "Triticale", "Seitan", "Bulgur", "Graham", "Kamut", "Couscous", "Farro", "Pan de pita", "Sémola", "Harina de matzá"};

            // Verificar si los ingredientes contienen alguno de los ingredientes no aptos
            if (ingredientess != null) {
                for (String ingredienteNoApto : ingredientesNoAptos) {
                    if (ingredientess.contains(ingredienteNoApto)) {
                        sw_celiaco2.setChecked(false);
                        break; // Si se encuentra un ingrediente no apto, salir del bucle
                    }
                }
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

            }
        }

    }



    public void AgregarProductoCancelar(View view) {
        Intent agregarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(agregarProductoCancelar);
    }

    public void AgregarProductoAgregar(View view) {
        // Código para manejar la lógica de agregar producto...
        producto = new Producto();
        producto.setNombre(nombreProducto.getText().toString());
        producto.setIngredientes(ingredientes.getText().toString());
        producto.setStock(Integer.parseInt(inventario.getText().toString()));
        producto.setPrecio(Float.parseFloat(precio.getText().toString()));

        idEtiquetado1 = etiquetado1.getSelectedItemPosition();
        idEtiquetado2 = etiquetado2.getSelectedItemPosition();
        idEtiquetado3 = etiquetado3.getSelectedItemPosition();

        if (producto.getNombre().isEmpty()) {
            Toast toast = Toast.makeText(AgregarProducto.this, "Por favor, ingrese el nombre del producto.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
        } else {
            new ExisteProducto().execute(producto.getNombre());
        }

    }

    public boolean validarProducto(Producto producto, int id1, int id2, int id3){
        boolean isValid = true;
        String ingredientsTxt = ingredientes.getText().toString();
        int price = 0;
        int stock = 0;




        // Validar que contenga al menos un ingrediente
        if (ingredientsTxt.isEmpty()) {
            isValid = false;
            Toast toast = Toast.makeText(AgregarProducto.this, "Por favor, ingrese al menos un ingrediente.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
        }else {
            String ingredientess = ingredientes.toString();

            // Lista de ingredientes no aptos para celíacos
            String[] ingredientesNoAptos = {"Harina","Trigo", "Cebada", "Centeno", "Avena", "Malta", "Extracto de Malta", "Harina de espelta", "Triticale", "Seitan", "Bulgur", "Graham", "Kamut", "Couscous", "Farro", "Pan de pita", "Sémola", "Harina de matzá"};

            // Verificar si los ingredientes contienen alguno de los ingredientes no aptos
            if (ingredientess != null) {
                for (String ingredienteNoApto : ingredientesNoAptos) {
                    if (ingredientess.contains(ingredienteNoApto)) {
                        sw_celiaco2.setChecked(false);
                        break; // Si se encuentra un ingrediente no apto, salir del bucle
                    }
                }
            }
        }

        // Validar que el precio sea mayor a cero
        try {
            price = Integer.parseInt(precio.getText().toString());
            if (price <= 0) {
                isValid = false;
                Toast toast = Toast.makeText(AgregarProducto.this, "El precio debe ser mayor a cero.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
                toast.show(); // Mostrar el Toast
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast toast = Toast.makeText(AgregarProducto.this, "Por favor, ingrese un precio válido.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
        }

        // Validar que el stock sea mayor o igual a cero
        try {
            stock = Integer.parseInt(inventario.getText().toString());
            if (stock < 0) {
                isValid = false;
                Toast toast = Toast.makeText(AgregarProducto.this, "El stock no puede ser negativo.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
                toast.show(); // Mostrar el Toast
            }
        } catch (NumberFormatException e) {
            isValid = false;
            Toast toast = Toast.makeText(AgregarProducto.this, "Por favor, ingrese un valor de inventario válido.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
            toast.show(); // Mostrar el Toast
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
            }
        }
    }


    private class ExisteProducto extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... nombreProducto) {
            Conexion con = new Conexion();
            try {
                return con.ExisteProducto(nombreProducto[0], comercio);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean existe) {
            if (existe) {

                Toast toast = Toast.makeText(AgregarProducto.this, "EL PRODUCTO YA EXISTE, POR FAVOR ELIJA OTRO NOMBRE.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            } else {
                if (validarProducto(producto, idEtiquetado1, idEtiquetado2, idEtiquetado3)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AgregarProducto.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
                    builder.setView(dialogView);

                    final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
                    Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
                    Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

                    mensajeConfirm.setText("¿ESTÁS SEGURO DE AGREGAR EL ARTÍCULO?");
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
                            new AgregarProducto.AgregarProductoTask().execute(producto);
                            dialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    private class AgregarProductoTask extends AsyncTask<Producto, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Producto... producto) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.altaProducto(producto[0], comercio, idEtiquetado1, idEtiquetado2, idEtiquetado3);
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
                    Toast toast = Toast.makeText(AgregarProducto.this, "PRODUCTO AGREGADO CON EXITO", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    Intent volverAlmenu = new Intent(AgregarProducto.this, MenuAdmin.class);
                    startActivity(volverAlmenu);
                } else {
                    // El usuario no es un administrador, realiza las acciones correspondientes
                    Toast toast = Toast.makeText(AgregarProducto.this, "PRODUCTO AGREGADO CON EXITO", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    Intent volverAlmenu = new Intent(AgregarProducto.this, MenuComercio.class);
                    startActivity(volverAlmenu);
                }
            } else {

                Toast toast = Toast.makeText(AgregarProducto.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200); // Establecer la posición del Toast
                toast.show(); // Mostrar el Toast
            }
        }
    }


    public void ConsultaEtiquetado(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarProducto.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_notificaciones, null);
        builder.setView(dialogView);

        final EditText notificacionesMSJ = dialogView.findViewById(R.id.editTextNotificaciones);
        Button btnAceptarNotificaciones = dialogView.findViewById(R.id.btnAceptarNotificaciones);

        String consulta = "ETIQUETADO FRONTAL" + '\n' + '\n' + "El etiquetado frontal de un producto determina si es apto o no para un cliente con alguna restriccion." + '\n' + '\n';
        consulta += "ETIQUETADOS:" + '\n' + '\n';
        consulta += "NO APTO PARA CELIACOS" + '\n' + '\n';
        consulta += "-Exceso en azucares." + '\n' + "-Exceso en grasas totales." + '\n' + "-Exceso en grasas saturadas." + '\n' + "-Exceso en calorias." + '\n' + "-El producto contiene exceso en sodio." + '\n';
        consulta += '\n' + "NO APTO PARA DIABETICOS" + '\n' + '\n' + "-El producto contiene exceso en sodio." + '\n' + "-Este producto contiene edulcorante." + '\n' + "-Este producto contiene cafeína." + '\n';
        consulta += '\n' + "NO APTO PARA HIPERTENSOS" + '\n' + '\n' + "-Este producto contiene edulcorante." + '\n' + "-Este producto contiene cafeína." + '\n';

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