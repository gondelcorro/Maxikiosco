package Negocio;

import java.sql.*;
import javax.swing.JOptionPane;

public class ScriptTablas {

    Connection conexion = null;
    Statement st = null;
    ResultSet rs = null;

    public void crearTablas() throws SQLException {

        try {
            conexion = Conexion.getConexion();
            st = conexion.createStatement();

            // LA FORMA DE ESCRIBIR ES SQL SERVER
            
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `articulos` ( "
                    + " `art_id` int(11) NOT NULL AUTO_INCREMENT,"
                    + " `art_codigo` bigint(20) NOT NULL," // UNIQUE PARA Q NO SE PUEDA DUPLICAR EL REGISTRO
                    + " `art_descripcion` varchar(255) NOT NULL,"
                    + " `art_marca` varchar(255), "
                    + " `art_precio_compra` float(10), "
                    + " `art_porc_util` float(10), "
                    + " `art_precio_venta` float(10), "
                    + " `art_rubro` varchar(50), "
                    + " `art_proveedor` varchar(50),"
                    + " `art_detalle` varchar(255),"                    
                    + " `art_estado` int(5),"
                    + " PRIMARY KEY (`art_id`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `stock` ( "
                    + " `sto_id` int(11) NOT NULL AUTO_INCREMENT,"
                    + " `sto_codigo_art` bigint(20) NOT NULL,"
                    + " `sto_cantActual` int(10) NOT NULL,"
                    + " `sto_cantRecarga` int(10),"
                    + " `sto_fechaRecarga` varchar(45), "
                    + " `sto_cantVendida` int(10) ,"
                    + " `sto_estado` int(5),"
                    + " PRIMARY KEY (`sto_id`)"
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS `ventas` ( "
                    + " `ven_id` int(11) NOT NULL AUTO_INCREMENT, "
                    + " `ven_fecha` varchar(45) NOT NULL, "
                    + " `ven_hora` varchar(45) NOT NULL, "
                    + " `ven_monto` float(10) NOT NULL, "
                    + " `ven_formaPago` int(10) NOT NULL, "
                    + " `ven_tipoTarjeta` varchar(50), "
                    + " `ven_turno` varchar(30) , "
                    + " `ven_usuario` varchar(45) , "
                    + " `ven_caja` int(11) NOT NULL, "
                    + " `ven_estado` int(5) , "
                    + " PRIMARY KEY (`ven_id`) "
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS `art_ven` ( "
                    + " `art_ven_id` int(11) NOT NULL AUTO_INCREMENT, "
                    + " `ven_id` int(11) NOT NULL, "
                    + " `art_id` int(11) NOT NULL, "
                    + " `cant` float(10) NOT NULL, "
                    + " `precio` float(10) NOT NULL, "
                    + " `art_ven_estado` int(5),"
                    + " PRIMARY KEY (`art_ven_id`), "
                    + " KEY `cf_art` (`art_id`), "
                    + " CONSTRAINT `cf_art` FOREIGN KEY (`art_id`) REFERENCES `articulos` (`art_id`) ON DELETE CASCADE ON UPDATE CASCADE, "
                    + " KEY `cf_ven` (`ven_id`), "
                    + " CONSTRAINT `cf_ven` FOREIGN KEY (`ven_id`) REFERENCES `ventas` (`ven_id`) ON DELETE CASCADE ON UPDATE CASCADE "
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ");
            
             st.executeUpdate("CREATE TABLE IF NOT EXISTS `caja` ( "
                    + " `caj_id` int(11) NOT NULL AUTO_INCREMENT, "
                    + " `caj_fecha` varchar(45) NOT NULL, "
                    + " `caj_hora` varchar(45) NOT NULL, "
                    + " `caj_turno` varchar(45) NOT NULL, "
                    + " `caj_encargado` varchar(45) NOT NULL, "
                    + " `caj_Inicial` float(10) NOT NULL, "
                    + " `caj_Ingresos` float(10), "
                    + " `caj_Egresos` float(10) , "
                    + " `caj_final` float(10) , "
                    + " `caj_hay` float(10) , "
                    + " `caj_estado` int(5) , "
                    + " PRIMARY KEY (`caj_id`) "
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS `usuario` ( "
                    + " `usu_id` int(11) NOT NULL AUTO_INCREMENT,"
                    + " `usu_nombre` varchar(255) NOT NULL UNIQUE,"
                    + " `usu_contrasenia` varchar(255) NOT NULL,"
                    + " `usu_tipo` int(11) NOT NULL,"
                    + " `usu_estado` int(5),"
                    + " PRIMARY KEY (`usu_id`)"
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");
            
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `proveedor` ( "
                    + " `pro_id` int(11) NOT NULL AUTO_INCREMENT,"
                    + " `pro_nombre` varchar(255) NOT NULL UNIQUE,"
                    + " `pro_estado` int(5),"
                    + " PRIMARY KEY (`pro_id`)"
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");
            
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `rubro` ( "
                    + " `rub_id` int(11) NOT NULL AUTO_INCREMENT,"
                    + " `rub_nombre` varchar(255) NOT NULL UNIQUE,"
                    + " `rub_estado` int(5),"
                    + " PRIMARY KEY (`rub_id`)"
                    + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        } finally {
            conexion.close();
            st.close();
        }
    }
}
