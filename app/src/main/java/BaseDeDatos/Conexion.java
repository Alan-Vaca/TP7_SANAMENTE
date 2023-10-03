package BaseDeDatos;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

import Entidad.Usuario;


public class Conexion extends AsyncTask<String,Void, String> {

    //CREDENCIALES DE CONEXION PARA BASE DE DATOS REMOTA
    public static String ConexionHOST = "3306";
    public static String ConexionUSER = "sql10648327";
    public static String ConexionPASS = "9cyMPtn3zQ";
    public static String ConexionURL = "jdbc:mysql://sql10.freesqldatabase.com:" + ConexionHOST + "/" + ConexionUSER;

    //CLASES PARA LAS CONSULTAS
    public static consultasUsuario consultasUsuario;


    //CONEXION
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(ConexionURL, ConexionUSER, ConexionPASS);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
            return null;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    //CONSULTAS - A LA BASE DE DATOS

    //--------------------------------------------------------------------------------------
    //USUARIOS
    //--------------------------------------------------------------------------------------

    public Usuario obtenerUsuarioXlogin(Usuario user) {
        try {
            user = consultasUsuario.obtenerUsuarioXlogin(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return user;
    }

    public boolean RegistrarUsuario(Usuario user) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.registrarUsuario(getConnection(),user);

            if(exito && user.isCliente()){
                //-ALTA NOTIFICACIONES (ID USUARIO) SE DEBE SETEAR DE MANERA DEFAULT
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean ModificarUsuario(Usuario user) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.modificarUsuario(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //NOTIFICACIONES
    //--------------------------------------------------------------------------------------

    //-ALTA NOTIFICACIONES (ID USUARIO, NOTIFICACIONES) SE DEBE SETEAR DE MANERA DEFAULT
    //-OBTENER NOTIFICACIONES (ID USUARIO)
    //-MODIFICAR NOTIFICACIONES (ID USUARIO, NOTIFICACIONES)

    //--------------------------------------------------------------------------------------
    //RESTRICCIONES
    //--------------------------------------------------------------------------------------

    //-ALTA RESTRICCIONES (ID USUARIO, ID USUARIO)
    //-OBTENER RESTRICCIONES (ID USUARIO)
    //-MODIFICAR RESTRICCIONES (ID USUARIO, RESTRICCIONES)

    //--------------------------------------------------------------------------------------
    //COMERCIOS
    //--------------------------------------------------------------------------------------

    //-ALTA COMERCIO (ID USUARIO, COMERCIO)
    //-OBTENER COMERCIO (ID USUARIO)
    //-MODIFICAR COMERCIO (ID USUARIO, COMERCIO)

    //--------------------------------------------------------------------------------------
    //PRODUCTOS
    //--------------------------------------------------------------------------------------

    //-ALTA PRODUCTO
    //-OBTENER PRODUCTO (ID PRODUCTO)
    //-MODIFICAR PRODUCTO (ID COMERCIO, PRODUCTO)
    //-LISTAR PRODUCTOS (FILTROS) DEBERA INCLUIR EL NOMBRE DE LOS COMERCIOS ASOCIADOS

    //--------------------------------------------------------------------------------------
    //ETIQUETADO
    //--------------------------------------------------------------------------------------

    //-ALTA ETIQUETADO (ID PRODUCTO)
    //-OBTENER ETIQUETADO (ID PRODUCTO)
    //-MODIFICAR ETIQUETADO (ID PRODUCTO, ETIQUETADO)

    //--------------------------------------------------------------------------------------
    //PEDIDOS
    //--------------------------------------------------------------------------------------

    //-ALTA PEDIDO (LISTADO DE PRODUCTOS, IDCLIENTE) A TODOS LOS PRODUCTOS SE DEBERA RESTAR LA CANTIDAD EN EL STOCK
    //-DETALLE PEDIDO (ID PEDIDO)
    //-ESTADO PEDIDO (ID PEDIDO, ESTADO) SE CAMBIARA EL ESTADO (entregado, cancelado, confirmado)
    //-LISTAR PEDIDOS (FILTROS)

    //--------------------------------------------------------------------------------------
    //CALIFICACIONES
    //--------------------------------------------------------------------------------------

    //-EXISTE CALIFICACION (IDPEDIDO, IDCLIENTE) DEBERA VERIFICAR SI EXISTE PARA SABER SI ES UN ALTA O UN UPDATE
    //-CALIFICAR (ID PEDIDO, IDCLIENTE) DEBERA APLICAR LA CALIFICACION A TODOS LOS PRODUCTOS DE ESE PEDIDO
    //-MODIFICAR CALIFICACION (ID PEDIDO, IDCLIENTE) DEBERA APLICAR LA CALIFICACION A TODOS LOS PRODUCTOS DE ESE PEDIDO
    //-OBTENER PROMEDIO DE CALIFICACION (ID PRODUCTO) DEBERA HACER UNA MEDIA/PROMEDIO DE TODAS LAS CALIFICACIONES

    //--------------------------------------------------------------------------------------
    //HISTORIAL
    //--------------------------------------------------------------------------------------

    //-CONSULTAR HISTORIAL (FILTROS, IDCLIENTE) SE SUMARA LOS PEDIDOS Y CALIFICACION PERSONAL EN CASO DE TENERLA

    //--------------------------------------------------------------------------------------
    //REPORTE
    //--------------------------------------------------------------------------------------

    //-REALIZAR REPORTE (ID COMERCIO) SE REALIZARA UN REPORTE CON TODOS LOS POSIBLES DATOS CALCULABLES Y CONVENIENTES PARA EL COMERCIO
    //---ejemplos: TOTAL SEMANAL, PROMEDIO DIARIO, MAYOR VENTA (FECHA Y MONTO), MENOR VENTA (FECHA Y MONTO), PRODUCTOS MAS VENDIDOS (5), PRODUCTOS MENOS VENDIDOS (5)
}
