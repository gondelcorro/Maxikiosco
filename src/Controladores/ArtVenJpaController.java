/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.NonexistentEntityException;
import Modelo.ArtVen;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Modelo.Ventas;
import Modelo.Articulos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author DelKo
 */
public class ArtVenJpaController implements Serializable {

    public ArtVenJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ArtVen artVen) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ventas venId = artVen.getVenId();
            if (venId != null) {
                venId = em.getReference(venId.getClass(), venId.getVenId());
                artVen.setVenId(venId);
            }
            Articulos artId = artVen.getArtId();
            if (artId != null) {
                artId = em.getReference(artId.getClass(), artId.getArtId());
                artVen.setArtId(artId);
            }
            em.persist(artVen);
            if (venId != null) {
                venId.getArtVenList().add(artVen);
                venId = em.merge(venId);
            }
            if (artId != null) {
                artId.getArtVenList().add(artVen);
                artId = em.merge(artId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ArtVen artVen) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArtVen persistentArtVen = em.find(ArtVen.class, artVen.getArtVenId());
            Ventas venIdOld = persistentArtVen.getVenId();
            Ventas venIdNew = artVen.getVenId();
            Articulos artIdOld = persistentArtVen.getArtId();
            Articulos artIdNew = artVen.getArtId();
            if (venIdNew != null) {
                venIdNew = em.getReference(venIdNew.getClass(), venIdNew.getVenId());
                artVen.setVenId(venIdNew);
            }
            if (artIdNew != null) {
                artIdNew = em.getReference(artIdNew.getClass(), artIdNew.getArtId());
                artVen.setArtId(artIdNew);
            }
            artVen = em.merge(artVen);
            if (venIdOld != null && !venIdOld.equals(venIdNew)) {
                venIdOld.getArtVenList().remove(artVen);
                venIdOld = em.merge(venIdOld);
            }
            if (venIdNew != null && !venIdNew.equals(venIdOld)) {
                venIdNew.getArtVenList().add(artVen);
                venIdNew = em.merge(venIdNew);
            }
            if (artIdOld != null && !artIdOld.equals(artIdNew)) {
                artIdOld.getArtVenList().remove(artVen);
                artIdOld = em.merge(artIdOld);
            }
            if (artIdNew != null && !artIdNew.equals(artIdOld)) {
                artIdNew.getArtVenList().add(artVen);
                artIdNew = em.merge(artIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = artVen.getArtVenId();
                if (findArtVen(id) == null) {
                    throw new NonexistentEntityException("The artVen with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArtVen artVen;
            try {
                artVen = em.getReference(ArtVen.class, id);
                artVen.getArtVenId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artVen with id " + id + " no longer exists.", enfe);
            }
            Ventas venId = artVen.getVenId();
            if (venId != null) {
                venId.getArtVenList().remove(artVen);
                venId = em.merge(venId);
            }
            Articulos artId = artVen.getArtId();
            if (artId != null) {
                artId.getArtVenList().remove(artVen);
                artId = em.merge(artId);
            }
            em.remove(artVen);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ArtVen> findArtVenEntities() {
        return findArtVenEntities(true, -1, -1);
    }

    public List<ArtVen> findArtVenEntities(int maxResults, int firstResult) {
        return findArtVenEntities(false, maxResults, firstResult);
    }

    private List<ArtVen> findArtVenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ArtVen.class));
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

    public ArtVen findArtVen(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ArtVen.class, id);
        } finally {
            em.close();
        }
    }

    public int getArtVenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ArtVen> rt = cq.from(ArtVen.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
