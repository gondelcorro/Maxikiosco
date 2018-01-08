package Negocio;

import java.sql.*;
import javax.swing.JOptionPane;

public class Conexion {

    public static final Conexion instance = new Conexion();
    static String bd = "comercio";
    static String login = "root";
    static String password = "escorpio";
    static String url = "jdbc:mysql://localhost/" + bd;

    Conexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "ERROR: No se encuentra el Driver jdbc");
        }
    }

    private Connection crearConexion() {
        Connection conexion = null;
        try {
            conexion= DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR: No se pudo conectar a la BD");
        }
        return conexion;
    }

    public static Connection getConexion() {
        return instance.crearConexion();
    }

}
