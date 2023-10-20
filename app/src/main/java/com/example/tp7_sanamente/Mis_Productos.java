package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Mis_Productos extends AppCompatActivity {

    Usuario user;
    TextView txt_StockCantidad, cantidadTxt, detalle, puntaje;
    ListView lv_Catalogo;
    Button btnAdd;
    ArrayList<Producto> listaProductos;
    ArrayList<pedidoXproducto> listadoCarrito;
    Producto productoSeleccionado;

    boolean listaCargada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);
        lv_Catalogo = (ListView)findViewById(R.id.grd_catalogo);
        btnAdd = (Button)findViewById(R.id.btnAgregarProducto);
        txt_StockCantidad = (TextView)findViewById(R.id.txtStockCantidad);
        cantidadTxt = (TextView)findViewById(R.id.catalogoCantidad);
        detalle = (TextView)findViewById(R.id.catalogoDetalle);
        puntaje = (TextView)findViewById(R.id.txtPuntajeCatalogo);
        listaCargada = false;

        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        Gson gsonCarrito = new Gson();
        String listaComoJson = preferences.getString("listadoCarrito", "");
        Type type = new TypeToken<ArrayList<pedidoXproducto>>(){}.getType();
        listadoCarrito = gsonCarrito.fromJson(listaComoJson, type);

        if (listadoCarrito != null && !listadoCarrito.isEmpty()) {
            listaCargada = true;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");
        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            if(user.isCliente()){
                btnAdd.setText("AGREGAR PRODUCTO AL CARRITO");
                txt_StockCantidad.setText("CANTIDAD A LLEVAR:");
                cantidadTxt.setText("0");
            }else{
                btnAdd.setText("AGREGAR UN NUEVO PRODUCTO");
                txt_StockCantidad.setText("STOCK REGISTRADO:");
                cantidadTxt.setText("0");
            }

            new Mis_Productos.obtenerListadoProducto().execute(user);
        }else{
            Toast.makeText(Mis_Productos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

        lv_Catalogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                productoSeleccionado = new Producto();
                productoSeleccionado = listaProductos.get(position);

                detalle.setText(productoSeleccionado.getIdProducto() + " - " + productoSeleccionado.getNombre() + " - $" + productoSeleccionado.getPrecio());
                puntaje.setText("POSEE " + productoSeleccionado.getPuntaje() + " ESTRELLAS DE 5");
                if(user.isCliente()){
                    cantidadTxt.setText("0");
                }else{
                    String stock = String.valueOf(productoSeleccionado.getStock());
                    cantidadTxt.setText(stock);
                }

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String productoJson = gson.toJson(productoSeleccionado);
                editor.putString("productoSeleccionado", productoJson);
                editor.apply();
            }
        });
    }

    private class obtenerListadoProducto extends AsyncTask<Usuario, Void, ArrayList<Producto>> {
        @Override
        protected ArrayList<Producto> doInBackground(Usuario... usuario) {

            Conexion con = new Conexion();
            try {
                listaProductos = con.obtenerListadoProductos(usuario[0]);

                return listaProductos;
            } catch (Exception e) {
                e.printStackTrace();
                return listaProductos;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Producto> listaProductosObtenido) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listaProductosObtenido.size() > 0) {

                listaProductos = listaProductosObtenido;
                ArrayAdapter<Producto> adapter = new ArrayAdapter<>(Mis_Productos.this, android.R.layout.simple_spinner_dropdown_item, listaProductos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                lv_Catalogo.setAdapter(adapter);

            } else {
                Toast.makeText(Mis_Productos.this, "HUBO UN ERROR AL CONSULTAR LOS PRODUCTO", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void CatalogoVerProducto(View view) {
        Intent catalogoVerProducto = new Intent(this, ModificarProducto.class);
        startActivity(catalogoVerProducto);
    }

    public void CatalogoFiltros(View view) {
        Intent catalogoFiltros = new Intent(this, Filtros.class);
        startActivity(catalogoFiltros);
    }

    public void CatalogoAgregarNuevoProducto(View view) {
        if(user.isCliente()){
            int cantidadSolicitada = Integer.parseInt(cantidadTxt.getText().toString());
            if(!cantidadTxt.getText().toString().isEmpty() && cantidadSolicitada >0) {

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
                Toast.makeText(Mis_Productos.this, "SELECCIONE LA CANTIDAD A COMPRAR PARA AGREGAR AL CARRITO", Toast.LENGTH_LONG).show();
            }
        }else{
            Intent catalogoAgregarNuevoProducto = new Intent(this, AgregarProducto.class);
            startActivity(catalogoAgregarNuevoProducto);
        }

    }

    public void VolverMenu(View view) {
        if(user.isCliente()) {

            Intent MenuCliente = new Intent(this, Menu_Cliente.class);
            startActivity(MenuCliente);
        }else {
            Intent MenuComercio = new Intent(this, MenuComercio.class);
            startActivity(MenuComercio);
        }
    }
}