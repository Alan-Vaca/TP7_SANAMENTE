package BaseDeDatos;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Notificacion;
import Entidad.Restriccion;
import Entidad.Usuario;


public class Conexion extends AsyncTask<String,Void, String> {

    //CREDENCIALES DE CONEXION PARA BASE DE DATOS REMOTA
    public static String ConexionHOST = "3306";
    public static String ConexionUSER = "sql10650827";
    public static String ConexionPASS = "X5VEc5C8D5";
    public static String ConexionURL = "jdbc:mysql://sql10.freesqldatabase.com:" + ConexionHOST + "/" + ConexionUSER;

    //CLASES PARA LAS CONSULTAS
    public static consultasUsuario consultasUsuario = new consultasUsuario();
    public static consultasNotificaciones consultasNotificaciones = new consultasNotificaciones();
    public static consultasRestricciones consultasRestricciones = new consultasRestricciones();


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
            Connection con = getConnection();
            user = consultasUsuario.obtenerUsuarioXlogin(con,user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return user;
    }

    public boolean RegistrarUsuarioCliente(Restriccion res) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.registrarUsuario(getConnection(),res.getClienteAsociado().getUsuarioAsociado());
            if(exito) {
                altaNotificacion(res.getClienteAsociado().getUsuarioAsociado().getIdUsuario());
                altaCliente(res.getClienteAsociado().getUsuarioAsociado());
                altaRestricciones(res);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean ModificarUsuarioCliente(Restriccion res, Notificacion not) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.modificarUsuario(getConnection(),res.getClienteAsociado().getUsuarioAsociado());
            if(exito) {
                ModificarRestriccion(res);
                ModificarNotificacion(not);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean RegistrarUsuarioComercio(Comercio com) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.registrarUsuario(getConnection(),com.getUsuarioAsociado());
            if(exito) {
                //alta comercio
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean ModificarUsuarioComercio(Comercio com) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.modificarUsuario(getConnection(),com.getUsuarioAsociado());
            if(exito) {
                //Modificar comercio
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //NOTIFICACIONES
    //--------------------------------------------------------------------------------------

    public void altaNotificacion(int idUsuario) {
        try {
            consultasNotificaciones.altaNotificacion(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    public Notificacion obtenerNotificaciones(Usuario usuario) {
        Notificacion not = new Notificacion();
        try {
            Connection con = getConnection();
            not = consultasNotificaciones.obtenerNotificaciones(getConnection(),usuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return not;
    }
    public boolean ModificarNotificacion(Notificacion not) {
        Boolean exito = false;
        try {
            exito = consultasNotificaciones.modificarNotificacion(getConnection(),not);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //CLIENTE
    //--------------------------------------------------------------------------------------

    public void altaCliente(Usuario user) {
        try {
            consultasUsuario.altaCliente(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    //--------------------------------------------------------------------------------------
    //RESTRICCIONES
    //--------------------------------------------------------------------------------------

    public void altaRestricciones(Restriccion rest) {
        try {
            consultasRestricciones.altaRestriccion(getConnection(),rest);
            consultasRestricciones.altaRestriccionXcliente(getConnection());
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }
    public Restriccion obtenerRestriccion(int idUsuario) {
        Restriccion res = new Restriccion();
        try {
            Connection con = getConnection();
            res = consultasRestricciones.obtenerRestriccion(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return res;
    }
    public void ModificarRestriccion(Restriccion rest) {
        try {
            consultasRestricciones.modificarRestriccion(getConnection(),rest);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

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
