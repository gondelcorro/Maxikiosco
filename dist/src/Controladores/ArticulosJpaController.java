/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Modelo.ArtVen;
import Modelo.Articulos;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author DelKo
 */
public class ArticulosJpaController implements Serializable {

    public ArticulosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articulos articulos) {
        if (articulos.getArtVenList() == null) {
            articulos.setArtVenList(new ArrayList<ArtVen>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ArtVen> attachedArtVenList = new ArrayList<ArtVen>();
            for (ArtVen artVenListArtVenToAttach : articulos.getArtVenList()) {
                artVenListArtVenToAttach = em.getReference(artVenListArtVenToAttach.getClass(), artVenListArtVenToAttach.getArtVenId());
                attachedArtVenList.add(artVenListArtVenToAttach);
            }
            articulos.setArtVenList(attachedArtVenList);
            em.persist(articulos);
            for (ArtVen artVenListArtVen : articulos.getArtVenList()) {
                Articulos oldArtIdOfArtVenListArtVen = artVenListArtVen.getArtId();
                artVenListArtVen.setArtId(articulos);
                artVenListArtVen = em.merge(artVenListArtVen);
                if (oldArtIdOfArtVenListArtVen != null) {
                    oldArtIdOfArtVenListArtVen.getArtVenList().remove(artVenListArtVen);
                    oldArtIdOfArtVenListArtVen = em.merge(oldArtIdOfArtVenListArtVen);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articulos articulos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulos persistentArticulos = em.find(Articulos.class, articulos.getArtId());
            List<ArtVen> artVenListOld = persistentArticulos.getArtVenList();
            List<ArtVen> artVenListNew = articulos.getArtVenList();
            List<String> illegalOrphanMessages = null;
            for (ArtVen artVenListOldArtVen : artVenListOld) {
                if (!artVenListNew.contains(artVenListOldArtVen)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ArtVen " + artVenListOldArtVen + " since its artId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ArtVen> attachedArtVenListNew = new ArrayList<ArtVen>();
            for (ArtVen artVenListNewArtVenToAttach : artVenListNew) {
                artVenListNewArtVenToAttach = em.getReference(artVenListNewArtVenToAttach.getClass(), artVenListNewArtVenToAttach.getArtVenId());
                attachedArtVenListNew.add(artVenListNewArtVenToAttach);
            }
            artVenListNew = attachedArtVenListNew;
            articulos.setArtVenList(artVenListNew);
            articulos = em.merge(articulos);
            for (ArtVen artVenListNewArtVen : artVenListNew) {
                if (!artVenListOld.contains(artVenListNewArtVen)) {
                    Articulos oldArtIdOfArtVenListNewArtVen = artVenListNewArtVen.getArtId();
                    artVenListNewArtVen.setArtId(articulos);
                    artVenListNewArtVen = em.merge(artVenListNewArtVen);
                    if (oldArtIdOfArtVenListNewArtVen != null && !oldArtIdOfArtVenListNewArtVen.equals(articulos)) {
                        oldArtIdOfArtVenListNewArtVen.getArtVenList().remove(artVenListNewArtVen);
                        oldArtIdOfArtVenListNewArtVen = em.merge(oldArtIdOfArtVenListNewArtVen);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articulos.getArtId();
                if (findArticulos(id) == null) {
                    throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulos articulos;
            try {
                articulos = em.getReference(Articulos.class, id);
                articulos.getArtId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articulos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ArtVen> artVenListOrphanCheck = articulos.getArtVenList();
            for (ArtVen artVenListOrphanCheckArtVen : artVenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulos (" + articulos + ") cannot be destroyed since the ArtVen " + artVenListOrphanCheckArtVen + " in its artVenList field has a non-nullable artId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(articulos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Articulos> findArticulosEntities() {
        return findArticulosEntities(true, -1, -1);
    }

    public List<Articulos> findArticulosEntities(int maxResults, int firstResult) {
        return findArticulosEntities(false, maxResults, firstResult);
    }

    private List<Articulos> findArticulosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articulos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Articulos findArticulos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articulos.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticulosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Articulos> rt = cq.from(Articulos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
