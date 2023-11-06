package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.Restriccion;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class ModificarProducto extends AppCompatActivity {

    Restriccion restriccion;
    Spinner etiquetado_1, etiquetado_2,etiquetado_3;
    ArrayList<Etiquetado> listaEtiquetados;
    TextView nombreProducto, ingredienteProducto, precioProducto, stockProducto, detalleStock, TxtEstadoTitulo,TxtEstado;
    int idEtiquetado1,idEtiquetado2,idEtiquetado3;
    Button btnModificarAgregarCarrito,btnEstado;
    Usuario user;
    Producto productoSeleccionado;
    Toolbar toolbarModificar;
    ArrayList<pedidoXproducto> listadoCarrito;
    boolean listaCargada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);

        etiquetado_1 = (Spinner)findViewById(R.id.ap_sp_item1);
        etiquetado_2 = (Spinner)findViewById(R.id.ap_sp_item2);
        etiquetado_3 = (Spinner)findViewById(R.id.ap_sp_item3);

        nombreProducto = (TextView)findViewById(R.id.ap_et_nombreDelProductoMod);
        ingredienteProducto = (TextView)findViewById(R.id.ap_et_ingredientes);
        precioProducto = (TextView)findViewById(R.id.ap_n_precio);
        stockProducto = (TextView)findViewById(R.id.ap_n_stock);

        detalleStock = (TextView)findViewById(R.id.txtInfoStock);
        btnModificarAgregarCarrito = (Button)findViewById(R.id.btnModAddCart);

        TxtEstadoTitulo = (TextView)findViewById(R.id.txtEstadoT);
        TxtEstado = (TextView)findViewById(R.id.txtEstadoE);
        btnEstado = (Button)findViewById(R.id.btnEstadoB);

        //toolbarModificar = (Toolbar)findViewById(R.id.toolbarMod);

        listaCargada = false;
        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        Gson gsonCarrito = new Gson();
        String listaComoJson = preferences.getString("listadoCarrito", "");
        Type type = new TypeToken<ArrayList<pedidoXproducto>>(){}.getType();
        listadoCarrito = gsonCarrito.fromJson(listaComoJson, type);

        if (listadoCarrito != null && !listadoCarrito.isEmpty()) {
            listaCargada = true;
        }

        listaEtiquetados = new ArrayList<Etiquetado>();

        new ModificarProducto.obtenerListadoEtiquetado().execute();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            if(user.isCliente()){
                nombreProducto.setEnabled(false);
                ingredienteProducto.setEnabled(false);
                precioProducto.setEnabled(false);
                etiquetado_1.setEnabled(false);
                etiquetado_2.setEnabled(false);
                etiquetado_3.setEnabled(false);
                //toolbarModificar.setTitle("DETALLES DEL PRODUCTO");
                TxtEstadoTitulo.setText("");
                TxtEstado.setText("");
                btnEstado.setText("");
                btnEstado.setEnabled(false);

                btnModificarAgregarCarrito.setText("AGREGAR AL CARRITO");
                detalleStock.setText("CANTIDAD A COMPRAR:");
                new ModificarProducto.cargarRestricciones().execute(user);

            }else{
                detalleStock.setText("STOCK:");
                btnModificarAgregarCarrito.setText("MODIFICAR PRODUCTO");
            }

        }else{
            Toast.makeText(ModificarProducto.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


        SharedPreferences sharedPreferencesProducto = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String productoJson = sharedPreferencesProducto.getString("productoSeleccionado", "");

        productoSeleccionado = new Producto();
        if (!productoJson.isEmpty()) {
            Gson gson = new Gson();
            productoSeleccionado = gson.fromJson(productoJson, Producto.class);

            nombreProducto.setText(productoSeleccionado.getNombre());
            ingredienteProducto.setText(productoSeleccionado.getIngredientes());
            String precioUnitario = String.valueOf(productoSeleccionado.getPrecio());
            precioProducto.setText(precioUnitario);


            if(user.isCliente()){
                stockProducto.setText("0");
            }else {
                String stockdelProducto = String.valueOf(productoSeleccionado.getStock());
                stockProducto.setText(stockdelProducto);
                TxtEstado.setText(productoSeleccionado.EstadoString());
                if(productoSeleccionado.isEstado()){
                    btnEstado.setText("DAR DE BAJA");
                }else{
                    btnEstado.setText("REACTIVAR");
                }
            }
            new ModificarProducto.obtenerEtiquetadosXproducto().execute(productoSeleccionado);

        }else{
            Toast.makeText(ModificarProducto.this, "PRODUCTO NO SELECCIONADO", Toast.LENGTH_LONG).show();
        }
    }

    private class obtenerListadoEtiquetado extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {
            ArrayList<Etiquetado> listadoXproducto = new ArrayList<Etiquetado>();
            Conexion con = new Conexion();
            try {
                listadoXproducto = con.obtenerListadoEtiquetado();

                return listadoXproducto;
            } catch (Exception e) {
                e.printStackTrace();
                return listadoXproducto;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Etiquetado> listadoEtiquetado) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();
            ArrayAdapter<Etiquetado> adapter = new ArrayAdapter<>(ModificarProducto.this, android.R.layout.simple_spinner_dropdown_item, listadoEtiquetado);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            etiquetado_1.setAdapter(adapter);
            etiquetado_2.setAdapter(adapter);
            etiquetado_3.setAdapter(adapter);

        }

    }


    private class obtenerEtiquetadosXproducto extends AsyncTask<Producto, Void, ArrayList<Etiquetado>> {
        @Override
        protected ArrayList<Etiquetado> doInBackground(Producto... producto) {

            Conexion con = new Conexion();
            try {
                listaEtiquetados = con.obtenerListadoEtiquetadoXproducto(producto[0]);

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



                if (listadoEtiquetado.size() > 0) {
                    etiquetado_1.setSelection(listadoEtiquetado.get(0).getIdEtiquetado());
                    if (listadoEtiquetado.size() > 1)
                        etiquetado_2.setSelection(listadoEtiquetado.get(1).getIdEtiquetado());
                    if (listadoEtiquetado.size() > 2)
                        etiquetado_3.setSelection(listadoEtiquetado.get(2).getIdEtiquetado());
                } else {
                    Toast.makeText(ModificarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(ModificarProducto.this, "HUBO UN ERROR AL CONSULTAR LOS ETIQUETADOS", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void ModificarProductoESTADO(View view) {
        Producto producto = new Producto();
        producto = productoSeleccionado;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
        Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
        Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

        mensajeConfirm.setText("¿ESTAS SEGURO EN DAR DE BAJA ESTE PRODUCTO?");
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancelarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Producto Producto = producto;
        btnConfirmarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Producto.setEstado(!Producto.isEstado());
                new ModificarProducto.modificarProducto().execute(Producto);
                dialog.dismiss();
            }
        });


    }

    public void ModificarProductoCancelar(View view) {
        Intent modificarProductoCancelar = new Intent(this, Mis_Productos.class);
        startActivity(modificarProductoCancelar);
    }

    public void ModificarProductoModificar(View view) {
        Producto producto = new Producto();

        producto = productoSeleccionado;
        producto.setNombre(nombreProducto.getText().toString());
        producto.setIngredientes(ingredienteProducto.getText().toString());
        if(!user.isCliente()) {
            producto.setStock(Integer.parseInt(stockProducto.getText().toString()));
        }
        if (!precioProducto.getText().toString().isEmpty()) {  producto.setPrecio(Float.parseFloat(precioProducto.getText().toString()));  }


        idEtiquetado1 = etiquetado_1.getSelectedItemPosition();
        idEtiquetado2 = etiquetado_2.getSelectedItemPosition();
        idEtiquetado3 = etiquetado_3.getSelectedItemPosition();

        if(!user.isCliente()){
            if(validarProductoXmodificar(producto,idEtiquetado1,idEtiquetado2,idEtiquetado3)){


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
                builder.setView(dialogView);
                final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
                Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
                Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

                mensajeConfirm.setText("¿ESTAS SEGURO QUE QUIERES MODIFICARLO?");

                final AlertDialog dialog = builder.create();
                dialog.show();

                btnCancelarConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Producto Producto = producto;
                btnConfirmarConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ModificarProducto.modificarProducto().execute(Producto);
                        dialog.dismiss();
                    }
                });



            }
        }else{
            if(validarProductoXagregarCarrito(producto,idEtiquetado1,idEtiquetado2,idEtiquetado3)){


                int cantidadSolicitada = Integer.parseInt(stockProducto.getText().toString());
                if(!stockProducto.getText().toString().isEmpty() && cantidadSolicitada >0) {

                    pedidoXproducto ProductoCarrito = new pedidoXproducto();
                    ProductoCarrito.setProducto(productoSeleccionado);
                    ProductoCarrito.setCantidad(cantidadSolicitada);

                    if (listaCargada) {
                        int index = -1;
                        for (int i = 0; i < listadoCarrito.size(); i++) {
                            if (listadoCarrito.get(i).getProducto().getIdProducto() == productoSeleccionado.getIdProducto()) {
                                index = i;
                                break;
                            }
                        }

                        if (index != -1) {
                            pedidoXproducto productoExistente = listadoCarrito.get(index);
                            int nuevaCantidad = productoExistente.getCantidad() + cantidadSolicitada;
                            productoExistente.setCantidad(nuevaCantidad);
                        } else {
                            pedidoXproducto ProductoXCarrito = new pedidoXproducto();
                            ProductoXCarrito.setProducto(productoSeleccionado);
                            ProductoXCarrito.setCantidad(cantidadSolicitada);
                            listadoCarrito.add(ProductoXCarrito);
                        }
                    } else {
                        // Si la lista no está cargada, simplemente agrega el nuevo producto
                        pedidoXproducto ProductoXCarrito = new pedidoXproducto();
                        ProductoXCarrito.setProducto(productoSeleccionado);
                        ProductoXCarrito.setCantidad(cantidadSolicitada);
                        listadoCarrito.add(ProductoXCarrito);
                    }

                    SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String listaComoJson = gson.toJson(listadoCarrito);
                    editor.putString("listadoCarrito", listaComoJson);
                    editor.apply();


                    Intent agregarAlcarrito = new Intent(this, MiCarritoCompras.class);
                    startActivity(agregarAlcarrito);
                }
                else {
                    Toast.makeText(ModificarProducto.this, "SELECCIONE LA CANTIDAD A COMPRAR PARA AGREGAR AL CARRITO", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public boolean validarProductoXmodificar(Producto producto, int id1, int id2, int id3){
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Complete o corrija los siguientes campos:\n");

        //DEBE VALIDAR TODOS LOS DATOS INGRESADOS A MODIFICAR
        if (producto == null ) {
            isValid = false;
            errorMessage.append("- NO SE SELECCIONÓ UN PRODUCTO\n");

        }

        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            isValid = false;
            errorMessage.append("- NOMBRE\n");

        }

        if(producto.getIngredientes() == null && producto.getIngredientes().isEmpty()){
            isValid = false;
            errorMessage.append("- INGREDIENTES\n");

        }
        else if(producto.getIngredientes().replaceAll("[^a-zA-Z]", "").length() < 3){
            isValid = false;
            errorMessage.append("- INGREDIENTES debe contener al menos 3 caracteres\n");
        }

        if(producto.getPrecio() <= 0){
            isValid = false;
            errorMessage.append("- Precio\n");

        }

        if(producto.getStock() <= 0){
            isValid = false;
            errorMessage.append("- Stock\n");

        }


        if (!isValid) {
            Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG).show();
        }
        return isValid;

    }

    public boolean validarProductoXagregarCarrito(Producto producto, int id1, int id2, int id3){
        //DEBE VALIDAR LA CANTIDAD SOLICITA Y QUE EL CLIENTE SEA APTO PARA COMPRAR DEPENDIENDO SUS RESTRICCIONES


        if((id1 == 1 || id2 == 1 || id3 == 1) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene exceso en azúcares. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 2 || id2 == 2 || id3 == 2) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene exceso en grasas totales. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 3 || id2 == 3 || id3 == 3) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene exceso en grasas saturadas. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 4 || id2 == 4 || id3 == 4) && restriccion.isCeliaco() || restriccion.isDiabetico()){
            Toast.makeText(ModificarProducto.this, "El producto contiene exceso en sodio. No es apto para celíacos o diabéticos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 5 || id2 == 5 || id3 == 5) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene exceso en calorías. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 6 || id2 == 6 || id3 == 6) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene edulcorante. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 7 || id2 == 7 || id3 == 74) && restriccion.isCeliaco()){
            Toast.makeText(ModificarProducto.this, "El producto contiene cafeína. No es apto para celíacos", Toast.LENGTH_LONG).show();
            return false;
        }

        if((id1 == 6 || id2 == 6 || id3 == 6) && restriccion.isDiabetico() || restriccion.isHipertenso()){
            Toast.makeText(ModificarProducto.this, "Este producto contiene edulcorante. Consuma bajo responsabilidad", Toast.LENGTH_LONG).show();
        }

        if((id1 == 7 || id2 == 7 || id3 == 7) && restriccion.isDiabetico() || restriccion.isHipertenso()){
            Toast.makeText(ModificarProducto.this, "Este producto contiene cafeína. Consuma bajo responsabilidad", Toast.LENGTH_LONG).show();
        }

        if(producto.getIngredientes().toUpperCase().contains(restriccion.getAlergico().toUpperCase())){
            Toast.makeText(ModificarProducto.this, "No es posible comprar por su seguridad ya que es alérgico a uno de sus ingredientes.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (producto.getStock() <= 0) {
            Toast.makeText(ModificarProducto.this, "Ingrese la cantidad a comprar para agregar al carrito.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private class modificarProducto extends AsyncTask<Producto, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Producto... producto) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.modificarProducto(producto[0],idEtiquetado1,idEtiquetado2,idEtiquetado3);
            } catch (Exception e) {
                e.printStackTrace();
                return exito;
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                Toast.makeText(ModificarProducto.this, "PRODUCTO MODIFICADO CON EXITO", Toast.LENGTH_LONG).show();
                Intent volverAlCatalogo = new Intent(ModificarProducto.this, Mis_Productos.class);
                startActivity(volverAlCatalogo);
            } else {
                Toast.makeText(ModificarProducto.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class cargarRestricciones extends AsyncTask<Usuario, Void, Restriccion> {
        @Override
        protected Restriccion doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Restriccion res = new Restriccion();
            try {
                res = con.obtenerRestriccion(user[0].getIdUsuario());
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return res;
            }
        }

        @Override
        protected void onPostExecute(Restriccion res) {
            if (res.getIdRestriccion() > 0) {
                restriccion = new Restriccion();
                restriccion = res;
            } else {
                Toast.makeText(ModificarProducto.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }

}