package Negocio;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class EditorTablaConBoton extends AbstractCellEditor implements TableCellEditor, ActionListener {

    Boolean currentValue;
    JButton button;
    protected static final String EDIT = "edit";
    private JTable tabla;
    private DefaultTableModel dtm;
    JDialog dialogo;

    public EditorTablaConBoton(JTable jTable1, DefaultTableModel model, JDialog dialogo) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener((ActionListener) this);
        button.setBorderPainted(false);
        this.tabla = jTable1;
        this.dtm = model;
        this.dialogo = dialogo;
    }

    public void actionPerformed(ActionEvent e) {
        // mymodel t = (mymodel) jTable1.getModel();
        // t.addNewRecord();
        fireEditingStopped();
        dialogo.setAlwaysOnTop(false);
        dialogo.setModal(false);
        int filaVista = tabla.getSelectedRow();
        int filaSeleccionada = tabla.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {

            JOptionPane.showMessageDialog(null, "Seleccione un registro", "Atención", 1);
            
        } else if (tabla.getName().equalsIgnoreCase("tablaVentas")) {
            try {
                Connection conn = Conexion.getConexion();
                JasperReport reporte = JasperCompileManager.compileReport("src/Reportes/InformeArticulosPorVenta.jrxml");
                Map parametros = new HashMap();
                int idVenta = Integer.parseInt(String.valueOf(dtm.getValueAt(filaSeleccionada, 0)));
                parametros.put("paramVenta", idVenta);
                JasperPrint print = JasperFillManager.fillReport(reporte, parametros, conn);
                JasperViewer visor = new JasperViewer(print, false); // EL FALSE ES PARA QUE NO CIERRE LA APLICACION AL SALIR
                visor.setTitle("DETALLE DE VENTA");
                //visor.setZoomRatio((float) 0.75);
                visor.setVisible(true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(null, "ERROR: " + ex);
            }

        } else if (tabla.getName().equalsIgnoreCase("tablaCaja")) {

            try {
                Connection conn = Conexion.getConexion();
                JasperReport reporte = JasperCompileManager.compileReport("src/Reportes/InformePlanillaPorCaja.jrxml");
                Map parametros = new HashMap();
                int numCaja = Integer.parseInt(String.valueOf(dtm.getValueAt(filaSeleccionada, 0)));
                parametros.put("paramIdCaja", numCaja);
                JasperPrint print = JasperFillManager.fillReport(reporte, parametros, conn);
                JasperViewer visor = new JasperViewer(print, false); // EL FALSE ES PARA QUE NO CIERRE LA APLICACION AL SALIR
                visor.setTitle("PLANILLA DE CAJA");
                visor.setZoomRatio((float) 0.75);
                visor.setVisible(true);
            } catch (JRException ex1) {
                Logger.getLogger(EditorTablaConBoton.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentValue;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Va a mostrar el botón solo en la última fila.
        // de otra forma muestra un espacio en blanco.

        if (row <= table.getModel().getRowCount()) {
            currentValue = (Boolean) value;
            return button;
        }
        return new JLabel();
    }
}
