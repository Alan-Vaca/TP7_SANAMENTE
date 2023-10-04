package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Entidad.Usuario;

public class consultasUsuario {
    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public Usuario obtenerUsuarioXlogin(Connection conn,Usuario user) {
        try {
            String query = "SELECT idUsuario from usuarios where contraseña = '" + user.getContraseña() + "' and nombreUsuario = '" + user.getNombreUsuario() + "'";

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        user.setIdUsuario(rs.getInt("idUsuario"));
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

        return user;
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------
    public Boolean registrarUsuario(Connection conn, Usuario user) {
        Boolean exito = false;
        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO USUARIOS(" +
                        "idUsuario " +
                        ") VALUES (?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, user.getIdUsuario());
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
                exito = true;
            }
        } catch (Exception e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------
    public Boolean modificarUsuario(Connection conn, Usuario user) {
        try {
            if (conn != null) {
                String updateQuery = "UPDATE USUARIOS SET " +
                        "idUsuario = ? " +
                        "WHERE idUsuario = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, user.getIdUsuario());
                pstmt.setInt(2, user.getIdUsuario());

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
