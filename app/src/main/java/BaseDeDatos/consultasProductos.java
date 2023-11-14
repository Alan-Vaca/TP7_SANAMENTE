package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import Entidad.Comercio;
import Entidad.Etiquetado;
import Entidad.Producto;
import Entidad.Usuario;

public class consultasProductos {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------

    public ArrayList<Producto> obtenerListadoProductosOfertas(Connection conn, Usuario user) {
        ArrayList<Producto> listadoProducto = new ArrayList<Producto>();

        try {
            String query = "SELECT p.idProducto, p.nombreProducto, p.ingredientes, p.stock, p.estado, " +
                    "p.precio, AVG(c.calificacion) prom " +
                    "FROM productos p " +
                    "INNER JOIN calificaciones c ON p.idProducto = c.idProducto " +
                    "WHERE p.estado = 1 " +
                    "GROUP BY p.idProducto, p.nombreProducto, p.ingredientes, p.stock, p.estado, p.precio " +
                    "HAVING AVG(c.calificacion) >= 2.5 " +
                    "ORDER BY p.precio, AVG(c.calificacion)";

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Producto producto = new Producto();
                        producto.setIdProducto(rs.getInt("p.idProducto"));
                        producto.setNombre(rs.getString("p.nombreProducto"));
                        producto.setIngredientes(rs.getString("p.ingredientes"));
                        producto.setPrecio(rs.getFloat("p.precio"));
                        producto.setStock(rs.getInt("p.stock"));
                        producto.setEstado(rs.getBoolean("p.estado"));
                        //producto.setIdComercio(rs.getInt("idComercio"));
                        producto.setPuntaje(rs.getFloat("prom"));

                        listadoProducto.add(producto);

                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return listadoProducto;
    }



    public ArrayList<Producto> obtenerListadoProductos(Connection conn, Usuario user) {
        ArrayList<Producto> listadoProducto = new ArrayList<Producto>();

        try {
            String query = "select p.idProducto productoID, nombreProducto, ingredientes, precio, stock, p.estado, p.idComercio, p.fechaAlta as fechaAltaProducto,"
            +"(select avg(calificacion) as puntaje from calificaciones where idProducto = p.idProducto) as puntaje from productos p ";

            if(user.isCliente()){
                //Si es para el cliente mostrara todos los productos con estado activo
                query += "where estado = 1 and stock > 0";
            }else {
                //si es para el comerciante mostrara todos los productos que cargo sin importar el estado
                query += "inner join comercios c on c.IdComercio = p.idComercio where c.idUsuario = " + user.getIdUsuario() ;
            }


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Producto producto = new Producto();
                        producto.setIdProducto(rs.getInt("productoID"));
                        producto.setNombre(rs.getString("nombreProducto"));
                        producto.setIngredientes(rs.getString("ingredientes"));
                        producto.setPrecio(rs.getFloat("precio"));
                        producto.setStock(rs.getInt("stock"));
                        producto.setEstado(rs.getBoolean("estado"));
                        producto.setIdComercio(rs.getInt("idComercio"));
                        producto.setPuntaje(rs.getFloat("puntaje"));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());
                        java.sql.Date fechaAlta = rs.getDate("fechaAltaProducto");

                        listadoProducto.add(producto);
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return listadoProducto;
    }





    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------
    public Boolean agregarProducto(Connection conn, Producto producto, Comercio comercio) throws SQLException {
        Boolean exito = false;
        PreparedStatement pstmt = null;

        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO productos(" +
                        "nombreProducto, ingredientes, precio, stock, estado, idComercio, fechaAlta" +
                        ") VALUES (?,?,?,?,?,?,?)";

                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, producto.getNombre());
                pstmt.setString(2,producto.getIngredientes());
                pstmt.setDouble(3,producto.getPrecio());
                pstmt.setInt(4,producto.getStock());
                pstmt.setBoolean(5,true);
                pstmt.setInt(6,comercio.getIdComercio());
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());
                pstmt.setDate(7, (Date) fechaActual);
                pstmt.executeUpdate();

                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return exito;

    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------

    public Boolean modificarProducto(Connection conn, Producto producto) throws SQLException {
        try {
            if (conn != null) {
                String updateQuery = "UPDATE productos SET " +
                        "nombreProducto = ?,ingredientes = ?,precio = ?,stock = ?,estado = ? " +
                        "WHERE idProducto = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, producto.getNombre());
                pstmt.setString(2, producto.getIngredientes());
                pstmt.setFloat(3, producto.getPrecio());
                pstmt.setInt(4, producto.getStock());
                pstmt.setBoolean(5, producto.isEstado());

                pstmt.setInt(6, producto.getIdProducto());

                int filas_modificadas= pstmt.executeUpdate();
                pstmt.close();
                conn.close();
                return filas_modificadas > 0;
            }
            else {
                Log.d("ERROR-DB", "NO hay conexion");
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
        return false;

    }


    //--------------------------------------------------------------------------------------
    //FILTRO
    //--------------------------------------------------------------------------------------
    public ArrayList<Producto> obtenerListadoProductosFiltrados(
            ArrayList<Producto> lista,
            String nombre, String contiene, String noContiene,
            String ordenarPor, boolean hipertenso, boolean diabetico, boolean celiaco) {


        ArrayList<Producto> listadoFiltrado = lista;


        // Filtros
        listadoFiltrado = filtrarPorNombre(listadoFiltrado, nombre);
        listadoFiltrado = filtrarPorContiene(listadoFiltrado, contiene);
        listadoFiltrado = filtrarPorNoContiene(listadoFiltrado, noContiene);



        if (celiaco) {
            listadoFiltrado = filtrarPorNoContiene(listadoFiltrado, "Harina");
        }

        if (diabetico) {
            listadoFiltrado = filtrarPorEtiquetado(listadoFiltrado, "Exceso en azucares");
        }

        if (hipertenso) {
            listadoFiltrado = filtrarPorEtiquetado(listadoFiltrado, "Exceso en sodio");
            listadoFiltrado = filtrarPorEtiquetado(listadoFiltrado, "contiene cafeina");
        }


        ordenarListado(listadoFiltrado, ordenarPor);

        return listadoFiltrado;
    }

    public ArrayList<Producto> filtrarPorNombre(ArrayList<Producto> lista, String nombre) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();

        if (!nombre.isEmpty()) {
            String nombreBuscadoSinTilde = quitarTildes(nombre.toLowerCase());
            for (Producto producto : lista) {
                String nombreSinTilde = quitarTildes(producto.getNombre().toLowerCase());
                if (nombreSinTilde.contains(nombreBuscadoSinTilde)) {
                    listaFiltrada.add(producto);
                }
            }
            } else {
            // Si la cadena contiene está vacía, devuelve la lista original sin filtrar
            listaFiltrada.addAll(lista);
        }
        return listaFiltrada;
    }

    public ArrayList<Producto> filtrarPorContiene(ArrayList<Producto> lista, String contiene) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();

        if (!contiene.isEmpty()) {
            String contieneSinTildes = quitarTildes(contiene.toLowerCase());
            for (Producto producto : lista) {
                String ingredientesSinTildes = quitarTildes(producto.getIngredientes().toLowerCase());
                if (ingredientesSinTildes.contains(contieneSinTildes)) {
                    listaFiltrada.add(producto);
                }
            }
        } else {
            // Si la cadena contiene está vacía, devuelve la lista original sin filtrar
            listaFiltrada.addAll(lista);
        }
        return listaFiltrada;
    }

    public ArrayList<Producto> filtrarPorNoContiene(ArrayList<Producto> lista, String noContiene) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();

        if (!noContiene.isEmpty()) {
            String noContieneSinTildes = quitarTildes(noContiene.toLowerCase());
            for (Producto producto : lista) {
                String ingredientesSinTildes = quitarTildes(producto.getIngredientes().toLowerCase());
                if (!ingredientesSinTildes.contains(noContieneSinTildes)) {
                    listaFiltrada.add(producto);
                }
            }
        } else {
            // Si la cadena contiene está vacía, devuelve la lista original sin filtrar
            listaFiltrada.addAll(lista);
        }
        return listaFiltrada;
    }

    public void ordenarListado(ArrayList<Producto> lista, String ordenarPor) {
        switch (ordenarPor) {
            case "calificaciones":
                Collections.sort(lista, (p1, p2) -> Float.compare(p2.getPuntaje(), p1.getPuntaje()));
                break;
            case "precio":
                Collections.sort(lista, Comparator.comparing(Producto::getPrecio));
                break;
            case "reciente":
                Collections.sort(lista, (p1, p2) -> Integer.compare(p2.getIdProducto(), p1.getIdProducto()));
                break;
            default:
                // No se aplica ordenamiento, cb sin selección
                break;
        }
    }

/*
    private ArrayList<Producto> contieneIngrediente(ArrayList<Producto> lista, String ingrediente) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();

        for (Producto producto : lista) {
            if (!producto.getIngredientes().toLowerCase().contains(ingrediente.toLowerCase())) {
                listaFiltrada.add(producto);
            }
        }

        return listaFiltrada;
    }
*/

    private ArrayList<Producto> filtrarPorEtiquetado(ArrayList<Producto> lista, String etiqueta) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        Log.d("Filtro", "Estoy en filtrarPorEtiquetado");

        for (Producto producto : lista) {
            if (!tieneEtiqueta(producto, etiqueta)) {
                listaFiltrada.add(producto);
            }
        }

        return listaFiltrada;


    }

    private boolean tieneEtiqueta(Producto producto, String etiqueta) {
        Log.d("Filtro", "Estoy en tieneEtiqueta");
        Conexion consultaEtiquetados = new Conexion();
        ArrayList<Etiquetado> etiquetasProducto = consultaEtiquetados.obtenerListadoEtiquetadoXproducto(producto);

        for (Etiquetado etiquetado : etiquetasProducto) {
            if (etiquetado.getDescripcion().equalsIgnoreCase(etiqueta)) {
                return true;
            }
        }

        return false;
    }

    private String quitarTildes(String palabra) {
        String normalized = Normalizer.normalize(palabra, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }


    public Boolean ExisteProducto(Connection conn, String nombreProducto, Comercio comercio) {
        boolean existe = false;
        String query = "SELECT COUNT(idProducto) as cantidadProductos FROM productos WHERE nombreProducto = ?";
        query += " AND idComercio = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreProducto);
            stmt.setInt(2, comercio.getIdComercio());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cantidadProductos = rs.getInt("cantidadProductos");
                if (cantidadProductos > 0) {
                    existe = true;
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí puedes manejar la excepción de manera adecuada, como lanzar una excepción personalizada o registrar el error
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return existe;
    }
}

