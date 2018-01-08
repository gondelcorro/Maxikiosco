package Negocio;

import Controladores.ArticulosJpaController;
import Controladores.StockJpaController;
import Modelo.Articulos;
import Modelo.Stock;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GestionStock {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void cargarTablaStock(JTable tablaStock) {
        new GestionTablas().limparTabla(tablaStock, (DefaultTableModel) tablaStock.getModel());
        ArticulosJpaController articuloControlador = new ArticulosJpaController(emf);
        List<Articulos> listaArticulos = articuloControlador.findArticulosEntities();
        EntityManager em = emf.createEntityManager();
        DefaultTableModel dtmStock = (DefaultTableModel) tablaStock.getModel();
        String datos[] = new String[dtmStock.getColumnCount()];
        for (int j = 0; j < listaArticulos.size(); j++) {
            Articulos articulo = listaArticulos.get(j);
            if (articulo.getArtEstado() == 1) {
                try {
                    // SELECCIONA EL ULTIMO REGISTRO DE CADA ARTICULO
                    Query consulta = em.createQuery("SELECT max(s.stoId) FROM Stock s WHERE s.stoCodigoArt = :stoCodigoArt");
                    consulta.setParameter("stoCodigoArt", articulo.getArtCodigo());
                    int ultimoStock = Integer.parseInt(String.valueOf(consulta.getSingleResult()));
                    StockJpaController stockControlador = new StockJpaController(emf);
                    Stock stock = stockControlador.findStock(ultimoStock);
                    datos[0] = String.valueOf(articulo.getArtCodigo());
                    datos[1] = articulo.getArtDescripcion();
                    datos[2] = articulo.getArtMarca();
                    datos[3] = String.valueOf(stock.getStocantActual());
                    dtmStock.addRow(datos);

                } catch (NumberFormatException e) { // SI EL RESULTADO DE CONSULTA NO DEVUELVE NINGUNA ENTIDAD STOCK

                    datos[0] = String.valueOf(articulo.getArtCodigo());
                    datos[1] = articulo.getArtDescripcion();
                    datos[2] = articulo.getArtMarca();
                    datos[3] = String.valueOf(0);
                    dtmStock.addRow(datos);
                }
            }
        }
    }

    public void cargarTablaHistorialStock(JTable tablaHistorialStock) {
        new GestionTablas().limparTabla(tablaHistorialStock, (DefaultTableModel) tablaHistorialStock.getModel());
        StockJpaController stockControlador = new StockJpaController(emf);
        List<Stock> listaHistorial = stockControlador.findStockEntities();
        ArticulosJpaController articuloControlador = new ArticulosJpaController(emf);
        List<Articulos> listaArticulos = articuloControlador.findArticulosEntities();
        DefaultTableModel dtmStock = (DefaultTableModel) tablaHistorialStock.getModel();
        String datos[] = new String[dtmStock.getColumnCount()];
        for (int j = 0; j < listaArticulos.size(); j++) {
            Articulos articulo = listaArticulos.get(j);
            for (int i = 0; i < listaHistorial.size(); i++) {
                Stock stock = listaHistorial.get(i);
                if (articulo.getArtCodigo() == stock.getStoCodigoArt()) {

                    datos[0] = String.valueOf(articulo.getArtCodigo());
                    datos[1] = articulo.getArtDescripcion();
                    datos[2] = articulo.getArtMarca();
                    datos[3] = String.valueOf(stock.getStocantActual());
                    if (stock.getStocantRecarga() != null) {
                        datos[4] = String.valueOf(stock.getStocantRecarga());
                    } else {
                        datos[4] = "-";
                    }
                    if (stock.getStocantRecarga() != null) {
                        datos[5] = String.valueOf(stock.getStofechaRecarga());
                    } else {
                        datos[5] = "-";
                    }
                    datos[6] = String.valueOf(stock.getStocantVendida());
                    if (datos[6].equals("null")) {
                        datos[6] = "-";
                    } else {
                        datos[4] = "-"; // SI SE VENDIO ENTONCOS MUESTRO - EN LA RECARGA
                    }
                    dtmStock.addRow(datos);
                }
            }
        }
    }

    public void reponerStock(JTable tablaArticulos) {
        int filaVista = tablaArticulos.getSelectedRow();
        int filaSeleccionada = tablaArticulos.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UN ARTICULO", "MENSAJE", 1);
        } else {
            try {
                TableModel dtm = (DefaultTableModel) tablaArticulos.getModel();
                Long codigo = Long.parseLong(String.valueOf(dtm.getValueAt(filaSeleccionada, 0)));
                EntityManager em = emf.createEntityManager();
                Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
                consulta.setParameter("artCodigo", codigo);
                Articulos articulo = (Articulos) consulta.getSingleResult();
                if (articulo != null) { // SI EXISTE EL ARTICULO
                    try {
                        Query consultaPorArticulo = em.createNamedQuery("Stock.findByStoCodigoArt");
                        consultaPorArticulo.setParameter("stoCodigoArt", codigo);
                        List<Stock> listaStock = consultaPorArticulo.getResultList();
                        int cantActual = listaStock.get(listaStock.size() - 1).getStocantActual(); //TOMO EL ULTIMO ELEM D LA LISTA XQ ES EL Q TIENE EL STOCK (EJ: tamaño 6, poscicion del ult elem 5)
                        int cant = Integer.parseInt(JOptionPane.showInputDialog("CANTIDAD A REPONER:"));
                        FechaYHora fecYHora = new FechaYHora();
                        String fecha = fecYHora.fechaActual();
                        Stock miStock = new Stock();
                        miStock.setStoCodigoArt(codigo);
                        miStock.setStocantActual(cantActual + cant);
                        miStock.setStocantRecarga(cant);
                        miStock.setStofechaRecarga(fecha);
                        StockJpaController stockControlador = new StockJpaController(emf);
                        stockControlador.create(miStock);

                    } catch (ArrayIndexOutOfBoundsException e) { // SI EL RESULTADO DE CONSULTA NO DEVUELVE NINGUNA ENTIDAD STOCK LA CANT ACTUAL VA A SER LA CANT INGRESADA RECIENadmin
                        Query consultaPorArticulo = em.createNamedQuery("Stock.findByStoCodigoArt");
                        consultaPorArticulo.setParameter("stoCodigoArt", codigo);
                        int cant = Integer.parseInt(JOptionPane.showInputDialog("CANTIDAD A REPONER:"));
                        FechaYHora fecYHora = new FechaYHora();
                        String fecha = fecYHora.fechaActual();
                        Stock miStock = new Stock();
                        miStock.setStoCodigoArt(codigo);
                        miStock.setStocantActual(cant);
                        miStock.setStocantRecarga(cant);
                        miStock.setStofechaRecarga(fecha);
                        StockJpaController stockControlador = new StockJpaController(emf);
                        stockControlador.create(miStock);
                    } finally {
                        em.close();
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "EL VALOR NO ES VÁLIDO");
            }

        }
    }

}
