package Negocio;

import Controladores.UsuarioJpaController;
import Modelo.Usuario;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GestionUsuarios {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void cargarTablaUsuarios(JTable tablaUsuarios) {
        new GestionTablas().limparTabla(tablaUsuarios, (DefaultTableModel) tablaUsuarios.getModel());
        UsuarioJpaController usuarioControlador = new UsuarioJpaController(emf);
        List<Usuario> listaDeUsuarios = usuarioControlador.findUsuarioEntities();
        DefaultTableModel dtmUsuarios = (DefaultTableModel) tablaUsuarios.getModel();
        String datos[] = new String[dtmUsuarios.getColumnCount()];
        for (int i = 0; i < listaDeUsuarios.size(); i++) {
            Usuario usuario = listaDeUsuarios.get(i);
            datos[0] = String.valueOf(usuario.getUsuId());
            datos[1] = usuario.getUsuNombre();
            if (usuario.getUsuTipo() == 1) {
                datos[2] = "Administrador";
            }else{
                datos[2] = "Personal";
            }
            dtmUsuarios.addRow(datos);
        }
    }
}
