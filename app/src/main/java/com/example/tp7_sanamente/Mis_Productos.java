package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Alergia;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.Restriccion;
import Entidad.Usuario;
import Entidad.pedidoXproducto;
public class Mis_Productos extends AppCompatActivity {

    Usuario user;
    TextView txt_StockCantidad, cantidadTxt, detalle, puntaje;
    ListView lv_Catalogo;
    Button btnAdd, btnFiltros,btnVERMOD;
    ArrayList<Producto> listaProductos, listaProductosOfertas;
    Restriccion restriccion;
    ArrayList<pedidoXproducto> listadoCarrito;
    Producto productoSeleccionado;

    ArrayList<Etiquetado> listaEtiquetados;
    boolean listaCargada;
    boolean listaProductosConFiltro, listaProductosConOfertas;

    ArrayList<Alergia> listaAlergiasUsuario;

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
        btnFiltros = (Button)findViewById(R.id.filtrosProductos);
        btnVERMOD = (Button)findViewById(R.id.buttonVER_MODIFICAR);
        listaCargada = false;

        listaEtiquetados = new ArrayList<Etiquetado>();
        listaAlergiasUsuario = new ArrayList<Alergia>();


        SharedPreferences preferences = getSharedPreferences("mi_pref", Context.MODE_PRIVATE);
        Gson gsonCarrito = new Gson();
        String listaComoJson = preferences.getString("listadoCarrito", "");
        Type type = new TypeToken<ArrayList<pedidoXproducto>>(){}.getType();
        listadoCarrito = gsonCarrito.fromJson(listaComoJson, type);

        if (listadoCarrito != null && !listadoCarrito.isEmpty()) {
            listaCargada = true;
        }


        try {
            SharedPreferences preferencesFiltrado = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
            Gson gsonFiltrado = new Gson();
            String listaComoJsonFiltrados = preferencesFiltrado.getString("listadoProductosFiltrados", "");
            Type typeFiltrado = new TypeToken<ArrayList<Producto>>() {
            }.getType();


            listaProductos = gsonFiltrado.fromJson(listaComoJsonFiltrados, typeFiltrado);


        }   catch (Exception e) {
            Log.d("Filtro.obtener", e.toString());
        }


        if (listaProductos != null && !listaProductos.isEmpty()) {
            listaProductosConFiltro = true;
        }

        /* OBTENGO LAS OFERTAS */

        try {
            SharedPreferences preferencesOfertas = getSharedPreferences("mi_prefer", Context.MODE_PRIVATE);
            Gson gsonOfertas = new Gson();
            String listaComoJsonOfertas = preferencesOfertas.getString("listaProductosOfertasObtenida", "");
            Type typeOfertas = new TypeToken<ArrayList<Producto>>() {
            }.getType();

            listaProductosOfertas = gsonOfertas.fromJson(listaComoJsonOfertas, typeOfertas);
        }   catch (Exception e) {
            Log.d("Filtro.obtener", e.toString());
        }


        if (listaProductosOfertas != null && !listaProductosOfertas.isEmpty()) {
            listaProductosConOfertas = true;
            btnFiltros.setVisibility(View.INVISIBLE);
        }else{
            btnFiltros.setVisibility(View.VISIBLE);
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
                btnVERMOD.setText("VER");
                new Mis_Productos.cargarRestricciones().execute(user);
                new Mis_Productos.cargarAlergias().execute(user);
            }else{
                btnVERMOD.setText("MODIFICAR");
                btnAdd.setText("AGREGAR UN NUEVO PRODUCTO");
                txt_StockCantidad.setText("STOCK REGISTRADO:");
                cantidadTxt.setText("0");
            }




            if(listaProductosConOfertas){
                if (listaProductosOfertas.size() > 0) {
                    listaProductos = listaProductosOfertas;

                    ArrayAdapter<Producto> adapter = new ArrayAdapter<>(Mis_Productos.this, android.R.layout.simple_spinner_dropdown_item, listaProductos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    lv_Catalogo.setAdapter(adapter);

                }
            }else if (!listaProductosConFiltro ) {
                new Mis_Productos.obtenerListadoProducto().execute(user);
            }else{
                if (listaProductos.size() > 0) {

                    ArrayAdapter<Producto> adapter = new ArrayAdapter<>(Mis_Productos.this, android.R.layout.simple_spinner_dropdown_item, listaProductos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    lv_Catalogo.setAdapter(adapter);

                }
            }
        }else{

            Toast toast = Toast.makeText(Mis_Productos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }


        lv_Catalogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                productoSeleccionado = new Producto();
                productoSeleccionado = listaProductos.get(position);

                detalle.setText(productoSeleccionado.getIdProducto() + " - " + productoSeleccionado.getNombre() + " - $" + productoSeleccionado.getPrecio());
                if(productoSeleccionado.getPuntaje() > 0) {
                    puntaje.setText("POSEE " + productoSeleccionado.getPuntaje() + " ESTRELLAS DE 5");
                }else{
                    puntaje.setText("AUN NO POSEE UNA CALIFICACION DE ESTRELLAS");
                }
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

                if(user.isCliente()) {
                    new Mis_Productos.obtenerEtiquetadosXproducto().execute(productoSeleccionado);
                }

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

            }
        }

    }

    public void CatalogoVerProducto(View view) {
        Intent catalogoVerProducto = new Intent(this, ModificarProducto.class);
        startActivity(catalogoVerProducto);
    }

    public void CatalogoFiltros(View view) {
        try {
            Intent catalogoFiltros = new Intent(this, Filtros.class);
            catalogoFiltros.putParcelableArrayListExtra("listaProductos", listaProductos);
            startActivity(catalogoFiltros);
        }catch (Exception e){
            Intent catalogoFiltros = new Intent(this, Filtros.class);

            startActivity(catalogoFiltros);
        }
    }

    public void CatalogoAgregarNuevoProducto(View view) {
        if(user.isCliente()){
            int cantidadSolicitada = Integer.parseInt(cantidadTxt.getText().toString());
            if(!cantidadTxt.getText().toString().isEmpty() && cantidadSolicitada >0) {

                pedidoXproducto ProductoCarrito = new pedidoXproducto();
                ProductoCarrito.setProducto(productoSeleccionado);
                ProductoCarrito.setCantidad(cantidadSolicitada);

                if(ProductoCarrito.getCantidad() <= 0 ){

                    Toast toast = Toast.makeText(Mis_Productos.this, "SE DEBE INGRESAR UNA CANTIDAD MAYOR A 0", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    return;
                }

                if(ProductoCarrito.getCantidad() > productoSeleccionado.getStock()){

                    Toast toast = Toast.makeText(Mis_Productos.this, "LA CANTIDAD EXCEDE EL STOCK", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    return;
                }

                /*if(!restriccion.getAlergico().trim().isEmpty())

                if(productoSeleccionado.getIngredientes().toUpperCase().trim().contains(restriccion.getAlergico().trim().toUpperCase())){

                Toast toast = Toast.makeText(Mis_Productos.this, "No es posible comprar por su seguridad ya que es alérgico a uno de sus ingredientes.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
                return;
                }*/

                SharedPreferences sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE);
                String mensajeAdvertencia = sharedPref.getString("test", null);
                if (mensajeAdvertencia != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Mis_Productos.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_notificaciones, null);
                    builder.setView(dialogView);

                    final EditText notificacionesMSJ = dialogView.findViewById(R.id.editTextNotificaciones);
                    Button btnAceptarNotificaciones = dialogView.findViewById(R.id.btnAceptarNotificaciones);
                    Button btnCancelarNotificaciones = dialogView.findViewById(R.id.btnCancelarNotificaciones);

                    btnCancelarNotificaciones.setVisibility(View.VISIBLE);

                    notificacionesMSJ.setText(mensajeAdvertencia);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    btnCancelarNotificaciones.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btnAceptarNotificaciones.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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


                            Intent agregarAlcarrito = new Intent(Mis_Productos.this, MiCarritoCompras.class);
                            startActivity(agregarAlcarrito);
                            dialog.dismiss();
                        }
                    });
                }


            }
            else {

                Toast toast = Toast.makeText(Mis_Productos.this, "SELECCIONE LA CANTIDAD A COMPRAR PARA AGREGAR AL CARRITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }else{
            Intent catalogoAgregarNuevoProducto = new Intent(this, AgregarProducto.class);
            startActivity(catalogoAgregarNuevoProducto);
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

                Toast toast = Toast.makeText(Mis_Productos.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }

    public void VolverMenu(View view) {


        listaProductosConFiltro = false;
        listaProductosConOfertas = false;
        listaProductosOfertas = null;
        listaProductos = null;


        SharedPreferences preferencesOfertas = getSharedPreferences("mi_prefer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorOfertas = preferencesOfertas.edit();
        editorOfertas.clear();
        editorOfertas.apply();


        SharedPreferences preferences = getSharedPreferences("mi_prefe", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();




        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent MenuCliente = new Intent(this, MenuAdmin.class);
            startActivity(MenuCliente);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            if(user.isCliente()) {
                Intent MenuCliente = new Intent(this, Menu_Cliente.class);
                startActivity(MenuCliente);
            }else {
                Intent MenuComercio = new Intent(this, MenuComercio.class);
                startActivity(MenuComercio);
            }
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

            try {

                if (listadoEtiquetado.size() > 0) {


                    String msjAdvertencias = "";
                    Boolean NoAptoCeliaco = false;
                    Boolean NoAptoHipertenso = false;
                    Boolean NoAptoDiabetico = false;

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 1 || listadoEtiquetado.get(1).getIdEtiquetado() == 1 || listadoEtiquetado.get(2).getIdEtiquetado() == 1) && (restriccion.isCeliaco() || restriccion.isDiabetico())){

                        msjAdvertencias += "El producto contiene exceso en azucares." + '\n';
                        NoAptoDiabetico = true;
                        NoAptoCeliaco = true;
                        //Toast toast = Toast.makeText(Mis_Productos.this, "El producto contiene exceso en azúcares. No es apto para celíacos Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 2 || listadoEtiquetado.get(1).getIdEtiquetado() == 2 || listadoEtiquetado.get(2).getIdEtiquetado() == 2) && restriccion.isCeliaco()){

                        msjAdvertencias += "El producto contiene exceso en grasas totales." + '\n';
                        NoAptoCeliaco = true;
                        //Toast toast = Toast.makeText(Mis_Productos.this, "El producto contiene exceso en grasas totales. No es apto para celíacos Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 3 || listadoEtiquetado.get(1).getIdEtiquetado() == 3 || listadoEtiquetado.get(2).getIdEtiquetado() == 3) && restriccion.isCeliaco()){

                        msjAdvertencias += "El producto contiene exceso en grasas saturadas." + '\n';
                        NoAptoCeliaco = true;
                        //Toast toast = Toast.makeText(Mis_Productos.this, "El producto contiene exceso en grasas saturadas. No es apto para celíacos Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 4 || listadoEtiquetado.get(1).getIdEtiquetado() == 4 || listadoEtiquetado.get(2).getIdEtiquetado() == 4) && (restriccion.isCeliaco() || restriccion.isDiabetico() || restriccion.isHipertenso())){

                        msjAdvertencias += "El producto contiene exceso en sodio." + '\n';
                        NoAptoHipertenso = true;
                        NoAptoCeliaco = true;
                        NoAptoDiabetico = true;
                        //Toast toast = Toast.makeText(Mis_Productos.this, "El producto contiene exceso en sodio. No es apto para celíacos o diabéticos Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 5 || listadoEtiquetado.get(1).getIdEtiquetado() == 5 || listadoEtiquetado.get(2).getIdEtiquetado() == 5) && restriccion.isCeliaco()){

                        msjAdvertencias += "El producto contiene exceso en calorías." + '\n';
                        NoAptoCeliaco = true;
                        //Toast toast = Toast.makeText(Mis_Productos.this, "El producto contiene exceso en calorías. No es apto para celíacos Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 6 || listadoEtiquetado.get(1).getIdEtiquetado() == 6 || listadoEtiquetado.get(2).getIdEtiquetado() == 6) && (restriccion.isDiabetico() || restriccion.isHipertenso())){

                        NoAptoDiabetico = true;
                        NoAptoHipertenso = true;
                        msjAdvertencias += "Este producto contiene edulcorante." + '\n';
                        //Toast toast = Toast.makeText(Mis_Productos.this, "Este producto contiene edulcorante. Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();
                    }

                    if((listadoEtiquetado.get(0).getIdEtiquetado() == 7 || listadoEtiquetado.get(1).getIdEtiquetado() == 7 || listadoEtiquetado.get(2).getIdEtiquetado() == 7) && (restriccion.isDiabetico() || restriccion.isHipertenso())){

                        NoAptoDiabetico = true;
                        NoAptoHipertenso = true;
                        msjAdvertencias += "Este producto contiene cafeína." + '\n';
                        //Toast toast = Toast.makeText(Mis_Productos.this, "Este producto contiene cafeína. Consuma bajo responsabilidad", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.TOP, 0, 200);
                        //toast.show();

                    }

                    boolean alergico = false;

                    if (listaAlergiasUsuario.size() > 0) {
                        for (Alergia alergia : listaAlergiasUsuario) {
                            String[] arregloIngredientes = alergia.getIngredientesAlergicos().toUpperCase().split(",");
                            for (String ingrediente : arregloIngredientes) {
                                if(!ingrediente.isEmpty())
                                if (productoSeleccionado.getIngredientes().toUpperCase().contains(ingrediente)) {

                                    msjAdvertencias += '\n' + " POR SU SEGURIDAD RECOMENDAMOS NO COMPRAR ESTE PRODUCTO YA QUE ES ALERGICO (" + alergia.getDescripcionAlergia() + ")"  + '\n';
                                    alergico = true;
                                    break;
                                }

                            }
                            if(alergico){break;}
                        }
                    }

                    /*
                    if(!restriccion.getAlergico().trim().isEmpty())
                        if(productoSeleccionado.getIngredientes().toUpperCase().trim().contains(restriccion.getAlergico().trim().toUpperCase())){

                            msjAdvertencias += '\n' + " POR SU SEGURIDAD RECOMENDAMOS NO COMPRAR ESTE PRODUCTO YA QUE ES ALERGICO. (" + restriccion.getAlergico().trim() + ")" + '\n';
                        }

                     */

                    if (restriccion.isCeliaco()) {
                        String[] ingredientesNoAptos = {"Harina","Trigo", "Cebada", "Centeno", "Avena", "Malta", "Extracto de Malta", "Harina de espelta", "Triticale", "Seitan", "Bulgur", "Graham", "Kamut", "Couscous", "Farro", "Pan de pita", "Sémola", "Harina de matzá"};

                        String ingredientesProducto = productoSeleccionado.getIngredientes().toUpperCase();

                        for (String ingredienteNoApto : ingredientesNoAptos) {
                            if (ingredientesProducto.contains(ingredienteNoApto.toUpperCase())) {
                                alergico = true;
                                // Puedes agregar un mensaje de advertencia si lo necesitas
                                msjAdvertencias += '\n' + "POR SU SEGURIDAD, RECOMENDAMOS NO COMPRAR ESTE PRODUCTO, YA QUE CONTIENE UN INGREDIENTE NO APTO PARA CELIACOS" + '\n';
                                break;  // Si encuentras un ingrediente no apto, puedes salir del bucle
                            }
                        }
                    }

                    if(NoAptoDiabetico || NoAptoCeliaco || NoAptoHipertenso || alergico){
                        msjAdvertencias = "ADVERTENCIA" + '\n' + '\n' + msjAdvertencias + '\n';
                        if(NoAptoDiabetico){
                            msjAdvertencias += "NO APTO PARA DIABETICOS" + '\n';
                        }
                        if(NoAptoCeliaco){
                            msjAdvertencias += "NO APTO PARA CELIACOS" + '\n';
                        }
                        if(NoAptoHipertenso){
                            msjAdvertencias += "NO APTO PARA HIPERTENSOS" + '\n';
                        }

                        msjAdvertencias += '\n' + "CONSUMA BAJO SU RESPONSABILIDAD.";



                        SharedPreferences preferencesAdvertencias = getSharedPreferences("test", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorAdvertencias = preferencesAdvertencias.edit();
                        editorAdvertencias.putString("test", msjAdvertencias);
                        editorAdvertencias.apply();

                    }
                }

            }catch(Exception e) {
                e.printStackTrace();
                //return listaEtiquetados;
            }

        }
        }

    private class cargarAlergias extends AsyncTask<Usuario, Void, ArrayList<Alergia>> {
        @Override
        protected ArrayList<Alergia> doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            try {
                listaAlergiasUsuario = con.obtenerListadoAlergiasXusuario(user[0].getIdUsuario());
                return listaAlergiasUsuario;
            } catch (Exception e) {
                e.printStackTrace();
                return listaAlergiasUsuario;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Alergia> listaAlergiasUsuarios) {
            listaAlergiasUsuario = listaAlergiasUsuarios;
        }
    }


    public void QuitarFiltrosRefrescar(View view) {
        Filtros filtrosActivity = new Filtros();
        filtrosActivity.QuitarFiltros(view);
    }
    }
