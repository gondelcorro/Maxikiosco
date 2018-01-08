package Negocio;

import Modelo.Articulos;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class DetectarCodigosRepetidos {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void detectarCodigosRepetidos() {
        try {
            EntityManager em = emf.createEntityManager();
            Query consulta = em.createNamedQuery("Articulos.findAll");
            List<Articulos> listaArticulos = consulta.getResultList();
            List<Articulos> listaDuplicados = new ArrayList<Articulos>();
            for (int i = 0; i < listaArticulos.size(); i++) {
                Articulos articuloOrig = listaArticulos.get(i);
                for (int j = 0; j < listaArticulos.size(); j++) {
                    Articulos articulo = listaArticulos.get(j);
                    if (articuloOrig.getArtCodigo() == articulo.getArtCodigo()) {
                        if (i == j) {
                        } else {
                            listaDuplicados.add(articulo);
                        }
                    }
                }
            }
            mostrarListaDuplicados(listaDuplicados);
        } catch (NullPointerException e) {
            System.err.println("error: " + e);
        }

    }

    private void mostrarListaDuplicados(List<Articulos> listaDuplicados) {

        Articulos articulo = null;
        for (int k = 0; k < listaDuplicados.size(); k++) {
            articulo = listaDuplicados.get(k);
            System.out.println("ARTICULO DUPLICADO: " + articulo.getArtCodigo() + " EN POSICION BD: " + articulo.getArtId());
        }
    }

}
