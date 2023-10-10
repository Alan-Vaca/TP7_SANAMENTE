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

public class consultasProductos {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------

    public ArrayList<Producto> obtenerListadoProductos(Connection conn,boolean xCliente,int idComercio) {
        ArrayList<Producto> listadoProducto = new ArrayList<Producto>();

        try {
            String query = "select idProducto, nombreProducto, ingredientes, precio, stock, estado from productos where 1 = 1 ";
            if(xCliente){
                //Si es para el cliente mostrara todos los productos con estado activo
                query += "and estado = 1";
            }else {
                //si es para el comerciante mostrara todos los productos que cargo sin importar el estado
                query += "and idComercio = " + idComercio;
            }


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Producto producto = new Producto();

                        producto.setIdProducto(rs.getInt("idProducto"));
                        producto.setNombre(rs.getString("nombreProducto"));
                        producto.setIngredientes(rs.getString("ingredientes"));
                        producto.setPrecio(rs.getFloat("precio"));
                        producto.setStock(rs.getInt("stock"));
                        producto.setEstado(rs.getBoolean("estado"));

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

}
