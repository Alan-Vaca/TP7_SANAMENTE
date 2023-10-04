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
            String query = "SELECT idUsuario,apellido,contraseña,direccion,dni,estado,nombre,nombreUsuario,"
            + "(select count(c.idCliente) from clientes c where c.idUsuario = idUsuario) as esCliente"
            + " from usuarios where contraseña = '" + user.getContraseña() + "' and nombreUsuario = '" + user.getNombreUsuario() + "'";

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        user.setIdUsuario(rs.getInt("idUsuario"));
                        user.setApellido(rs.getString("apellido"));
                        user.setContraseña(rs.getString("contraseña"));
                        user.setDireccion(rs.getString("direccion"));
                        user.setDNI(rs.getInt("dni"));
                        user.setEstado(rs.getBoolean("estado"));
                        user.setNombre(rs.getString("nombre"));
                        user.setNombreUsuario(rs.getString("nombreUsuario"));

                        int esCliente = 0;
                        esCliente = (rs.getInt("esCliente"));
                        if(esCliente > 0){
                            user.setCliente(true);
                        }else{
                            user.setCliente(false);
                        }
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
                String insertQuery = "INSERT INTO usuarios(" +
                "apellido,contraseña,direccion,dni,estado,nombre,nombreUsuario" +
                 ") VALUES (?,?,?,?,?,?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, user.getApellido());
                pstmt.setString(2, user.getContraseña());
                pstmt.setString(3, user.getDireccion());
                pstmt.setInt(4, user.getDNI());
                pstmt.setBoolean(5, true);
                pstmt.setString(6, user.getNombre());
                pstmt.setString(7, user.getNombreUsuario());
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
                String updateQuery = "UPDATE usuarios SET " +
                "apellido = ?,contraseña = ?,direccion = ?,dni = ?,estado = ?,nombre = ?,nombreUsuario = ?" +
                "WHERE idUsuario = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, user.getApellido());
                pstmt.setString(2, user.getContraseña());
                pstmt.setString(3, user.getDireccion());
                pstmt.setInt(4, user.getDNI());
                pstmt.setBoolean(5, user.isEstado());
                pstmt.setString(6, user.getNombre());
                pstmt.setString(7, user.getNombreUsuario());

                pstmt.setInt(8, user.getIdUsuario());

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
