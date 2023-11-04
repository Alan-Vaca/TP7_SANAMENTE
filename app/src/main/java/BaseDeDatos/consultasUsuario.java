package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Pedido;
import Entidad.Restriccion;
import Entidad.Usuario;

public class consultasUsuario {


    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public Usuario obtenerUsuarioXlogin(Connection conn,Usuario user) {
        try {
            String query = "SELECT idUsuario,apellido,contraseña,direccion,dni,estado,nombre,nombreUsuario,"
            + "esCliente"
            + " from usuarios u where contraseña = '" + user.getContraseña() + "' and nombreUsuario = '" + user.getNombreUsuario() + "'"
            + " and estado = 1";

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
                        user.setCliente(rs.getBoolean("esCliente"));

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
    public Boolean registrarUsuario(Connection conn, Usuario user) throws SQLException {
        Boolean exito = false;
        PreparedStatement pstmt = null; // Declarar el PreparedStatement fuera del bloque try para que pueda cerrarse en el bloque finally.

        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO usuarios(" +
                        "apellido,contraseña,direccion,dni,estado,nombre,nombreUsuario,esCliente" +
                        ") VALUES (?,?,?,?,?,?,?,?)";

                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, user.getApellido());
                pstmt.setString(2, user.getContraseña());
                pstmt.setString(3, user.getDireccion());
                pstmt.setInt(4, user.getDNI());
                pstmt.setBoolean(5, true);
                pstmt.setString(6, user.getNombre());
                pstmt.setString(7, user.getNombreUsuario());
                pstmt.setBoolean(8, user.isCliente());
                pstmt.executeUpdate();

                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                pstmt.close(); // Cierra el PreparedStatement en el bloque finally.
            }
            if (conn != null) {
                conn.close(); // Cierra la conexión en el bloque finally después de todas las operaciones.
            }
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

    public void altaCliente(Connection conn, Usuario user) {
        try {
            if (conn != null) {
                String selectMaxIdQuery = "SELECT max(idUsuario) FROM usuarios";
                PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                ResultSet resultSet = selectMaxIdStmt.executeQuery();
                int maxId = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxId = resultSet.getInt(1);
                }

                String insertQuery = "INSERT INTO clientes(idUsuario,estado,fechaCreacion) VALUES (?,?,?)";
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1,maxId);
                pstmt.setBoolean(2, true);
                pstmt.setDate(3, fechaActual);

                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    public void altaComercio(Connection conn, Comercio comercio) throws SQLException {
        try {
            if (conn != null) {

                String selectMaxIdQuery = "SELECT max(idUsuario) FROM usuarios";
                PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                ResultSet resultSet = selectMaxIdStmt.executeQuery();
                int maxId = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxId = resultSet.getInt(1);
                }


                String insertQuery = "INSERT INTO comercios(idUsuario,estado,fechaCreacion,cuit,horarios,nombreComercio) VALUES (?,?,?,?,?,?)";
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1,maxId);
                pstmt.setBoolean(2, true);
                pstmt.setDate(3, fechaActual);
                pstmt.setInt(4,comercio.getCuit());
                pstmt.setString(5,comercio.getHorarios());
                pstmt.setString(6,comercio.getNombreComercio());

                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            conn.close();
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    public Comercio obtenerComercio(Connection conn, int idUsuario) {
        Comercio comercio = new Comercio();
        try {
            String query = "select nombreComercio,idComercio, horarios,cuit,estado from comercios where idUsuario = " + idUsuario;

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        comercio.setNombreComercio(rs.getString("nombreComercio"));
                        comercio.setIdComercio(rs.getInt("idComercio"));
                        comercio.setHorarios(rs.getString("horarios"));
                        comercio.setCuit(rs.getInt("cuit"));
                        comercio.setEstado(rs.getBoolean("estado"));
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

        return comercio;
    }

    public static void modificarComercio(Connection conn, Comercio comercio) {
        try {
            if (conn != null) {
                String updateQuery = "UPDATE comercios SET " +
                        "nombreComercio = ?,horarios = ?,cuit = ?,estado = ? " +
                        "WHERE idUsuario = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, comercio.getNombreComercio());
                pstmt.setString(2, comercio.getHorarios());
                pstmt.setInt(3, comercio.getCuit());
                pstmt.setBoolean(4, comercio.isEstado());

                pstmt.setInt(5, comercio.getUsuarioAsociado().getIdUsuario());

                int filas_modificadas= pstmt.executeUpdate();
                pstmt.close();
                conn.close();
            }
            else {
                Log.d("ERROR-DB", "NO hay conexion");
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    public Cliente obtenerClienteXid(Connection conn, int idCliente) {
        Cliente cliente = new Cliente();
        try {
            String query = "SELECT c.idCliente as idClienteC,u.idUsuario as idUsuarioU, u.apellido as apellidoU, u.nombre as nombreU, u.dni as dniU" +
                    " FROM usuarios u INNER JOIN clientes c on c.idUsuario = u.idUsuario where c.idCliente = " + idCliente;

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        Usuario user = new Usuario();
                        user.setIdUsuario(rs.getInt("idUsuarioU"));
                        user.setApellido(rs.getString("apellidoU"));
                        user.setNombre(rs.getString("nombreU"));
                        user.setDNI(rs.getInt("dniU"));

                        cliente.setUsuarioAsociado(user);
                        cliente.setIdCliente(rs.getInt("idClienteC"));
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

        return cliente;
    }

    public Comercio obtenerComercioXid(Connection conn, int idComercio) {
        Comercio comercio = new Comercio();
        try {
            String query = "select nombreComercio,idComercio, horarios,cuit,estado from comercios where idComercio = " + idComercio;

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        comercio.setNombreComercio(rs.getString("nombreComercio"));
                        comercio.setIdComercio(rs.getInt("idComercio"));
                        comercio.setHorarios(rs.getString("horarios"));
                        comercio.setCuit(rs.getInt("cuit"));
                        comercio.setEstado(rs.getBoolean("estado"));
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

        return comercio;
    }

    public Boolean BajaUsuario(Connection conn, Usuario usuario) throws SQLException {
        Boolean exito = false;
        PreparedStatement pstmt = null;

        try {
            if (conn != null) {

                String updateQuery = "UPDATE usuarios SET " +
                        "estado = 0 " +
                        "WHERE idUsuario = ?";

                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, usuario.getIdUsuario());

                pstmt.executeUpdate();


                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }

        if (pstmt != null) {
            pstmt.close();
        }
        if (conn != null) {
            conn.close();
        }
        return exito;
    }
}
