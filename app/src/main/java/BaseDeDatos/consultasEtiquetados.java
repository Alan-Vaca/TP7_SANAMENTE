package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Etiquetado;

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

    public void agregarProductoXetiquetado(Connection conn, int idEtiquetado) {
        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO productoXetiquetado(" +
                        "idProducto, idEtiquetado" +
                        ") VALUES ((select max(idProducto) from productos),?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, idEtiquetado);
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
