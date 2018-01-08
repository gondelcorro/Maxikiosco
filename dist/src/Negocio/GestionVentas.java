package Negocio;

import Controladores.ArtVenJpaController;
import Controladores.StockJpaController;
import Controladores.VentasJpaController;
import Modelo.ArtVen;
import Modelo.Articulos;
import Modelo.Stock;
import Modelo.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GestionVentas {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void cargarTablaVentas(JTable tablaVentas) {
        new GestionTablas().limparTabla(tablaVentas, (DefaultTableModel) tablaVentas.getModel());
       // VentasJpaController ventaControlador = new VentasJpaController(emf);
        EntityManager em = emf.createEntityManager();
        Query consulta = em.createNamedQuery("Ventas.findByVenEstado");
        consulta.setParameter("venEstado", 1);
        List<Ventas> listaDeVentas = consulta.getResultList();
        DefaultTableModel dtmArticulos = (DefaultTableModel) tablaVentas.getModel();
        String datos[] = new String[dtmArticulos.getColumnCount()];
        for (int i = 0; i < listaDeVentas.size(); i++) {
            Ventas ventas = listaDeVentas.get(i);
            datos[0] = String.valueOf(ventas.getVenId());
            datos[1] = String.valueOf(ventas.getVenMonto());
            if (ventas.getVenformaPago() == 0) {
                datos[2] = "Efectivo";
            }
            if (ventas.getVenformaPago() == 1) {
                datos[2] = "Credito";
            }
            if (ventas.getVenformaPago() == 2) {
                datos[2] = "Debito";
            }
            if (ventas.getVenformaPago() == 3) {
                datos[2] = "Por mayor";
            }
            datos[3] = ventas.getVenFecha();
            datos[4] = String.valueOf(ventas.getVenCaja());
            if (datos[4].equals("null")) {
                datos[4] = "-";
            }
            datos[5] = String.valueOf(ventas.getVenUsuario());
            if (datos[5].equals("null")) {
                datos[5] = "-";
            }
            dtmArticulos.addRow(datos);
        }
    }

    public void registrarArtPorVentaYActStock(JTable tablaCarrito, Ventas venta) {
        int i, j, f, k;
        f = tablaCarrito.getRowCount();
        long codigo = 0;
        float precio = 0;
        int cant;
        Boolean esta = false;
        long[] articulosEnCarro = new long[f];
        // float[] precioArticulosEnCarro = new float[f];
        ArtVenJpaController artVenControlador = new ArtVenJpaController(emf);
        ArtVen artVen = new ArtVen();
        if (f > 0) {
            for (i = 0; i < f; i++) { // COMIENZO A RECORRER LA TABLA CARRITO
                codigo = Long.parseLong(String.valueOf(tablaCarrito.getModel().getValueAt(i, 0)));
                precio = Float.parseFloat(String.valueOf(tablaCarrito.getModel().getValueAt(i, 3)));
                for (k = 0; k <= articulosEnCarro.length; k++) {
                    try {
                        if (articulosEnCarro[k] == codigo) {
                            esta = true;
                            break;
                        } else {
                            esta = false;
                        }
                    } catch (Exception e) {
                        //Excepcion de puntero nulo porq el primer articulo no esta cargado
                    }
                }
                if (esta == false) {
                    articulosEnCarro[i] = codigo;
                    // precio = Float.parseFloat(String.valueOf(tablaCarrito.getModel().getValueAt(i, 3)));
                    cant = 0;
                    for (j = 0; j < f; j++) { // SUMO LA CANT VENDIDA DE ESA ARTICULO
                        if (codigo == Long.parseLong(String.valueOf(tablaCarrito.getModel().getValueAt(j, 0)))) {
                            cant = cant + Integer.parseInt(String.valueOf(tablaCarrito.getModel().getValueAt(j, 4)));
                        }
                    }
                    EntityManager em = emf.createEntityManager();
                    Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
                    consulta.setParameter("artCodigo", codigo);
                    Articulos articulo = (Articulos) consulta.getSingleResult();
                    System.out.println("ART COD: "+codigo);
                    artVen.setCant(cant);
                    artVen.setArtId(articulo);
                    artVen.setPrecio(articulo.getArtPrecioVenta());
                    artVen.setArtVenEstado(1);
                    Ventas regVenta = new VentasJpaController(emf).findVentas(venta.getVenId());
                    artVen.setVenId(regVenta);
                    artVenControlador.create(artVen);
                    actualizarStock(articulo, cant);
                }
            }
        }
    }

    private Stock actualizarStock(Articulos articulo, int cantVendida) {
        StockJpaController stockJpaController = new StockJpaController(emf);
        EntityManager em = emf.createEntityManager();
        Stock stock = new Stock();
        try {
            Query consulta = em.createNamedQuery("Stock.findByStoCodigoArt");
            consulta.setParameter("stoCodigoArt", articulo.getArtCodigo());
            List<Stock> listaStock = consulta.getResultList();
            if (listaStock.size() > 0) {
                stock = listaStock.get(listaStock.size() - 1); //TOMO EL ULTIMO ELEM D LA LISTA XQ ES EL Q TIENE EL STOCK
                int cantDisp = stock.getStocantActual();
                cantDisp = cantDisp - cantVendida;
                stock.setStoCodigoArt(articulo.getArtCodigo());
                stock.setStocantActual(cantDisp);
                stock.setStocantVendida(cantVendida);
                stockJpaController.create(stock);
            } else {
                stock.setStoCodigoArt(articulo.getArtCodigo());
                int cantDisp = stock.getStocantActual();
                cantDisp = cantDisp - cantVendida;
                stock.setStocantActual(cantDisp);
                stock.setStocantVendida(cantVendida);
                stockJpaController.create(stock);
            }
        } catch (NullPointerException e) { // SI EL RESULTADO DE CONSULTA NO DEVUELVE NINGUNA ENTIDAD STOCK
            int cantDisp = stock.getStocantActual();
            cantDisp = cantDisp - cantVendida;
            stock.setStoCodigoArt(articulo.getArtCodigo());
            stock.setStocantActual(cantDisp);
            stock.setStocantVendida(cantVendida);
            stockJpaController.create(stock);
            System.out.println("" + e);
            System.out.println("NO CARGUE NUNCA EL STOCK Y VENDI IGUAL...");
        } finally {
            em.close();
        }
        return stock;
    }

}
