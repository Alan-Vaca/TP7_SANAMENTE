package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Etiquetado;
import Entidad.Producto;

public class consultasEtiquetados {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public ArrayList<Etiquetado> obtenerListadoEtiquetado(Connection conn) {
        ArrayList<Etiquetado> listadoEtiquetado = new ArrayList<Etiquetado>();

        try {
            String query = "select idEtiquetado, descripcion, estado from etiquetados";
            Etiquetado etiquetadoInicial = new Etiquetado();
            etiquetadoInicial.setDescripcion("Seleccione...");
            etiquetadoInicial.setIdEtiquetado(0);
            etiquetadoInicial.setEstado(false);
            listadoEtiquetado.add(etiquetadoInicial);

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Etiquetado etiquetado = new Etiquetado();

                        etiquetado.setIdEtiquetado(rs.getInt("idEtiquetado"));
                        etiquetado.setDescripcion(rs.getString("descripcion"));
                        etiquetado.setEstado(rs.getBoolean("estado"));

                        listadoEtiquetado.add(etiquetado);
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

        return listadoEtiquetado;
    }

    public ArrayList<Etiquetado> obtenerListadoEtiquetadoXproducto(Connection conn, Producto producto) {
        ArrayList<Etiquetado> listadoEtiquetado = new ArrayList<Etiquetado>();

        try {
            String query = "select e.idEtiquetado as PidEtiquetado, e.descripcion as Pdescripcion, e.estado as Pestado from etiquetados e "+
                    "inner join productoXetiquetado ep on e.idEtiquetado = ep.idEtiquetado where ep.idProducto = " + producto.getIdProducto();


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Etiquetado etiquetado = new Etiquetado();

                        etiquetado.setIdEtiquetado(rs.getInt("PidEtiquetado"));
                        etiquetado.setDescripcion(rs.getString("Pdescripcion"));
                        etiquetado.setEstado(rs.getBoolean("Pestado"));

                        listadoEtiquetado.add(etiquetado);
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

        return listadoEtiquetado;
    }


    public void agregarProductoXetiquetado(Connection conn, int idEtiquetado) {
        try {
            if (conn != null) {
                String selectMaxIdQuery = "SELECT max(idProducto) FROM productos";
                PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                ResultSet resultSet = selectMaxIdStmt.executeQuery();
                int maxIdProducto = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxIdProducto = resultSet.getInt(1);
                }

                String insertQuery = "INSERT INTO productoXetiquetado(" +
                        "idProducto, idEtiquetado" +
                        ") VALUES (?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, maxIdProducto);
                pstmt.setInt(2, idEtiquetado);
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------


    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------

}
