package Negocio;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class GestionReportes {

    final JProgressBar barraProgreso = new JProgressBar();
    String rutaReporte = "";

    public GestionReportes() {
    }

    public GestionReportes(JTree arbolReportes) {

        TreeState estadoArbol = new TreeState();
        estadoArbol.setTreeState(arbolReportes, true); // LLAMO A LA CLASE TREESTATE PARA EXPANDIR O COLAPSAR EL ARBOL
        arbolReportes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolReportes.expandRow(arbolReportes.getRowCount());
    }

    public class TreeState { // CLASE PARA EXPANDIR O COLAPSAR ARBOL

        public void setTreeState(JTree tree, boolean expanded) {
            Object root = tree.getModel().getRoot();
            setTreeState(tree, new TreePath(root), expanded);
        }

        public void setTreeState(JTree tree, TreePath path, boolean expanded) {
            Object lastNode = path.getLastPathComponent();
            for (int i = 0; i < tree.getModel().getChildCount(lastNode); i++) {
                Object child = tree.getModel().getChild(lastNode, i);
                TreePath pathToChild = path.pathByAddingChild(child);
                setTreeState(tree, pathToChild, expanded);
            }
            if (expanded) {
                tree.expandPath(path);
            } else {
                tree.collapsePath(path);
            }

        }
    }

    public void verReporteSeleccionado(JTree arbolReportes) {

        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbolReportes.getLastSelectedPathComponent();

        if (node == null) //Nothing is selected.     
        {
            return;
        }

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) { // SI EL NODO ES UNA HOJA

            mostrarDialogoCargando(false);
            if (nodeInfo.toString().equalsIgnoreCase("listado completo de articulos")) {
                rutaReporte = "src/reportes/listadoCompletoDeArticulos.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("informe de articulos por rubro")) {
                rutaReporte = "src/reportes/listadoDeArticulosPorRubro.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("informe de articulos por proveedor")) {
                rutaReporte = "src/reportes/listadoDeArticulosPorProveedor.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("listado de ventas mensual")) {
                rutaReporte = "src/reportes/informeDeVentasMensual.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("Liquidaciones")) {
                rutaReporte = "src/reportes/LiquidacionesPorEmpleado.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("informe de stock")) {
                rutaReporte = "src/reportes/informeDeStock.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("historial de reposiciones")) {
                rutaReporte = "src/reportes/historialDeReposiciones.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("planillas de caja")) {
                rutaReporte = "src/reportes/informePlanillasDeCaja.jrxml";
            }
            if (nodeInfo.toString().equalsIgnoreCase("estadistica mensual")) {
                rutaReporte = "src/reportes/informeEstadisticasMensual.jrxml";
            }
        } else {
            //  displayURL(helpURL); 
        }
    }

    public void mostrarDialogoCargando(final boolean conParametro) {

        final JDialog cuadroDialogo = new JDialog();
        HiloProgreso hilo;
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.white);
        JLabel imagen = new JLabel("");
        imagen.setIcon(new ImageIcon("src/Imagenes/preloader.gif"));
        panel.add(imagen);
        panel.add(barraProgreso);
        barraProgreso.setVisible(false);
        cuadroDialogo.add(panel);
        cuadroDialogo.setSize(310, 310);
        cuadroDialogo.setLocationRelativeTo(null);
        cuadroDialogo.setVisible(true);
        hilo = new HiloProgreso(barraProgreso);
        hilo.start();
        hilo = null;
        barraProgreso.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (barraProgreso.getValue() == 50) {
                    cuadroDialogo.dispose();
                    if (conParametro) {
                        reporteConParametro(rutaReporte);
                    } else {
                        reporteSinParametro(rutaReporte);
                    }
                }
            }
        });
    }

    public void reporteSinParametro(String urlReporte) {
        try {
            Conexion conexion = new Conexion();
            JasperReport reporte = JasperCompileManager.compileReport(urlReporte);
            JasperPrint print = JasperFillManager.fillReport(reporte, null, conexion.getConexion()); //NO TENGO PARAMETROS
            JasperViewer visor = new JasperViewer(print, false); // EL FALSE ES PARA QUE NO CIERRE LA APLICACION AL SALIR
            visor.setTitle(urlReporte.toUpperCase());
            visor.setZoomRatio((float) 0.75);
            visor.setVisible(true);

            // GUARDA UN ARCHIVO COMO EXCEL
          /*  JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setExporterInput(new SimpleExporterInput(print));
            xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput("D://reporte.xls"));
            SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
            xlsReportConfiguration.setOnePagePerSheet(false);
            xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(true);
            xlsReportConfiguration.setDetectCellType(false);
            xlsReportConfiguration.setWhitePageBackground(false);
            xlsExporter.setConfiguration(xlsReportConfiguration);
            xlsExporter.exportReport();*/

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "El reporte no está disponible" );
        }
    }

    public void reporteConParametro(String urlReporte) {
        try {
            Conexion conexion = new Conexion();
            JasperReport reporte = JasperCompileManager.compileReport(urlReporte);
            JasperPrint print = JasperFillManager.fillReport(reporte, null, conexion.getConexion()); //NO TENGO PARAMETROS
            JasperViewer visor = new JasperViewer(print, false); // EL FALSE ES PARA QUE NO CIERRE LA APLICACION AL SALIR
            visor.setTitle(urlReporte.toUpperCase());
            visor.setZoomRatio((float) 0.75);
            visor.setVisible(true);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "ERROR EN EL REPORTE: " + ex);
        }
    }
}
