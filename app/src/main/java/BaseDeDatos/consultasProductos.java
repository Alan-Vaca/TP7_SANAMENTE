package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Comercio;
import Entidad.Producto;
import Entidad.Usuario;

public class consultasProductos {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------

    public ArrayList<Producto> obtenerListadoProductos(Connection conn, Usuario user) {
        ArrayList<Producto> listadoProducto = new ArrayList<Producto>();

        try {
            String query = "select p.idProducto productoID, nombreProducto, ingredientes, precio, stock, p.estado, p.idComercio, "
            +"(select avg(calificacion) as puntaje from calificaciones where idProducto = p.idProducto) as puntaje from productos p ";

            if(user.isCliente()){
                //Si es para el cliente mostrara todos los productos con estado activo
                query += "where estado = 1";
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
                        "nombreProducto, ingredientes, precio, stock, estado, idComercio" +
                        ") VALUES (?,?,?,?,?,?)";

                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, producto.getNombre());
                pstmt.setString(2,producto.getIngredientes());
                pstmt.setDouble(3,producto.getPrecio());
                pstmt.setInt(4,producto.getStock());
                pstmt.setBoolean(5,true);
                pstmt.setInt(6,comercio.getIdComercio());
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
            Connection conn, Usuario user,
            String nombre, String contiene, String noContiene,
            String ordenarPor, boolean hipertenso, boolean diabetico, boolean celiaco) {

        ArrayList<Producto> listadoFiltrado = obtenerListadoProductos(conn, user);

        // Aplica filtros según sea necesario
        listadoFiltrado = filtrarPorNombre(listadoFiltrado, nombre);
        listadoFiltrado = filtrarPorContiene(listadoFiltrado, contiene);
        listadoFiltrado = filtrarPorNoContiene(listadoFiltrado, noContiene);
        // Agrega más métodos de filtro según tus necesidades

        // Ordena la lista si es necesario
        ordenarListado(listadoFiltrado, ordenarPor);

        return listadoFiltrado;
    }

    private ArrayList<Producto> filtrarPorNombre(ArrayList<Producto> lista, String nombre) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        for (Producto producto : lista) {
            if (producto.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                listaFiltrada.add(producto);
            }
        }
        return listaFiltrada;
    }

    public ArrayList<Producto> filtrarPorContiene(ArrayList<Producto> lista, String contiene) {

        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        Log.d("PruebaLog", String.valueOf(contiene));

        if (!contiene.isEmpty()) {
            for (Producto producto : lista) {
                if (producto.getIngredientes().toLowerCase().contains(contiene.toLowerCase())) {
                    listaFiltrada.add(producto);
                }
            }
        } else {
            // Si la cadena contiene está vacía, devuelve la lista original sin filtrar
            listaFiltrada.addAll(lista);
        }
        Log.d("PruebaLog", String.valueOf(listaFiltrada));
        return listaFiltrada;
    }

    private ArrayList<Producto> filtrarPorNoContiene(ArrayList<Producto> lista, String noContiene) {
        // Implementa la lógica para filtrar por no contiene según tus necesidades
        return lista;
    }

    private void ordenarListado(ArrayList<Producto> lista, String ordenarPor) {
        // Implementa la lógica para ordenar según tus necesidades
        // Puedes utilizar Collections.sort() u otros métodos de ordenamiento
    }

}
