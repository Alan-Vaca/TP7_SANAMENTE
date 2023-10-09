package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Entidad.Notificacion;
import Entidad.Usuario;

public class consultasNotificaciones {
    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------
    public void altaNotificacion(Connection conn, int idUsuario) {
        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO notificaciones(" +
                        "idUsuario,ofertas,pedidos,productos" +
                        ") VALUES ((select max(idUsuario) from usuarios),?,?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setBoolean(1, false);
                pstmt.setBoolean(2, false);
                pstmt.setBoolean(3, false);
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
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public Notificacion obtenerNotificaciones(Connection conn, Usuario usuario) {
        Notificacion not = new Notificacion();
        try {
            String query = "SELECT idNotificacion, ofertas, pedidos, productos"
                    + " from notificaciones where idUsuario = " + usuario.getIdUsuario();

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        not.setUsuarioAsociado(usuario);
                        not.setIdNotificacion(rs.getInt("idNotificacion"));
                        not.setOfertas(rs.getBoolean("ofertas"));
                        not.setPedidos(rs.getBoolean("pedidos"));
                        not.setProductos(rs.getBoolean("productos"));
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
        return  not;
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------
    public Boolean modificarNotificacion(Connection conn, Notificacion not) {
        try {
            if (conn != null) {
                String updateQuery = "UPDATE notificaciones SET " +
                        "ofertas = ?,pedidos = ?,productos = ? " +
                        "WHERE idUsuario = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setBoolean(1, not.isOfertas());
                pstmt.setBoolean(2, not.isPedidos());
                pstmt.setBoolean(3, not.isProductos());
                pstmt.setInt(4, not.getUsuarioAsociado().getIdUsuario());

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

}
