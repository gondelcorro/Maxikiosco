package Negocio;

import Modelo.Articulos;
import Modelo.Stock;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GestionArticulos {

    private String tipoVenta;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void cargarTablaArticulos(JTable tablaArticulos) {
        try {
            new GestionTablas().limparTabla(tablaArticulos, (DefaultTableModel) tablaArticulos.getModel());
            DefaultTableModel dtmArticulos = (DefaultTableModel) tablaArticulos.getModel();
            EntityManager em = emf.createEntityManager();
            Query consulta = em.createNamedQuery("Articulos.findByArtEstado");
            consulta.setParameter("artEstado", 1);
            List<Articulos> listaArticulosActivos = consulta.getResultList();
            String datos[] = new String[dtmArticulos.getColumnCount()];
            float precioCompra = 0;
            float precioVenta = 0;
            for (int i = 0; i < listaArticulosActivos.size(); i++) {
                Articulos articulos = listaArticulosActivos.get(i);
                datos[0] = String.valueOf(articulos.getArtCodigo());
                datos[1] = articulos.getArtDescripcion();
                datos[2] = articulos.getArtMarca();
                if (articulos.getArtPrecioCompra() == null) {
                    datos[3] = String.valueOf(0);
                } else {
                    precioCompra = (float) ((int) (articulos.getArtPrecioCompra() * 100.0) / 100.0);
                    datos[3] = String.valueOf(precioCompra);
                }
                if (articulos.getArtPrecioVenta() == null) {
                    datos[4] = String.valueOf(0);
                } else {
                    precioVenta = (float) ((int) (articulos.getArtPrecioVenta() * 100.0) / 100.0);
                    datos[4] = String.valueOf(precioVenta);
                }
                datos[5] = String.valueOf(articulos.getArtRubro());
                datos[6] = String.valueOf(articulos.getArtProveedor());
                //datos[7] = String.valueOf(articulos.getArtDetalle()); LO MUESTRO CON BOTON DERECHO NO EN LA TABLA
                dtmArticulos.addRow(datos);
            }
        } catch (NullPointerException e) {
            System.err.println("error: " + e);
        }
    }

    public float añadirArtAlCarro(JTable tablaArticulos, JTable tablaCarrito, JLabel descArt) {
        float total = 0;
        int filaVista = tablaArticulos.getSelectedRow();
        int filaSeleccionada = tablaArticulos.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UN ARTICULO", "MENSAJE", 1);
        } else {
            TableModel dtm = (DefaultTableModel) tablaArticulos.getModel();
            Long codigo = Long.parseLong(String.valueOf(dtm.getValueAt(filaSeleccionada, 0)));
            total = buscarArticulo(codigo, tablaCarrito, descArt);
        }
        return total;
    }

    public float buscarArticulo(long codigo, JTable tablaCarrito, JLabel descArt) {
        float total = 0;
        EntityManager em = emf.createEntityManager();
        Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
        consulta.setParameter("artCodigo", codigo);
        Articulos articulo = null;
        try {
            articulo = (Articulos) consulta.getSingleResult();
            if (articulo != null) { // SI EXISTE EL ARTICULO
                try {
                    Query consultaPorArt = em.createNamedQuery("Stock.findByStoCodigoArt");
                    consultaPorArt.setParameter("stoCodigoArt", codigo);
                    List<Stock> listaStock = consultaPorArt.getResultList();
                    if (listaStock.get(listaStock.size() - 1).getStocantActual() > 0) { // VERIFICO EL STOCK (EL ULTIMO ELEM DE LA LISTA)
                        total = sumarArticulo(tablaCarrito, articulo);
                        descArt.setText(articulo.getArtDescripcion());
                    } else {
                      //  JOptionPane.showMessageDialog(null, "RECUERDE REPONER EL STOCK PARA EL ARTICULO");
                        total = sumarArticulo(tablaCarrito, articulo);
                        descArt.setText(articulo.getArtDescripcion());
                    }
                } catch (ArrayIndexOutOfBoundsException e) { // SI EL RESULTADO DE CONSULTA NO DEVUELVE NINGUNA ENTIDAD STOCK
                   // JOptionPane.showMessageDialog(null, "RECUERDE REPONER EL STOCK PARA EL ARTICULO");
                    total = sumarArticulo(tablaCarrito, articulo);
                    descArt.setText(articulo.getArtDescripcion());
                    total = actualizarTotal(tablaCarrito);
                } finally {
                    em.close();
                }
            } else {
                JOptionPane.showMessageDialog(null, "ARTICULO INEXISTENTE", "Atención", 1);
            }
        } catch (NonUniqueResultException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR ACCEDER. EL CODIGO DE ARTICULO DEBE SER UNICO", "ATENCION", 1);
        }
        return total;
    }

    private float sumarArticulo(JTable tablaCarrito, Articulos articulo) {

        Object datos[] = new Object[tablaCarrito.getColumnCount()];
        datos[0] = articulo.getArtCodigo();
        datos[1] = articulo.getArtDescripcion();
        datos[2] = articulo.getArtMarca();
        float precioVenta = (float) ((int) (articulo.getArtPrecioVenta() * 100.0) / 100.0);
        datos[3] = String.valueOf(precioVenta);
        datos[4] = 1; // CANTIDAD POR DEFECTO
        datos[5] = String.valueOf(precioVenta); // SUBTOTAL POR DEFECTO
        datos[6] = String.valueOf(tipoVenta);
        DefaultTableModel dtm = (DefaultTableModel) tablaCarrito.getModel();
        dtm.addRow(datos);
        tablaCarrito.setModel(dtm);
        float total = actualizarTotal(tablaCarrito);
        return total;
    }

    public Float actualizarTotal(JTable tablaCarrito) {

        int i, f;
        float suma = 0;
        f = tablaCarrito.getRowCount();
        if (f > 0) {
            for (i = 0; i < f; i++) {
                float subTotal = Float.parseFloat(String.valueOf(tablaCarrito.getModel().getValueAt(i, 5)));
                suma = suma + subTotal;
            }
            suma = (float) ((int) (suma * 100.0) / 100.0);
        }
        return suma;
    }

    /**
     * @return the tipoVenta
     */
    public String getTipoVenta() {
        return tipoVenta;
    }

    /**
     * @param tipoVenta the tipoVenta to set
     */
    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

}
