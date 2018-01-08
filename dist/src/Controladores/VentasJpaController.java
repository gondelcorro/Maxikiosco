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
import Modelo.Ventas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author DelKo
 */
public class VentasJpaController implements Serializable {

    public VentasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ventas ventas) {
        if (ventas.getArtVenList() == null) {
            ventas.setArtVenList(new ArrayList<ArtVen>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ArtVen> attachedArtVenList = new ArrayList<ArtVen>();
            for (ArtVen artVenListArtVenToAttach : ventas.getArtVenList()) {
                artVenListArtVenToAttach = em.getReference(artVenListArtVenToAttach.getClass(), artVenListArtVenToAttach.getArtVenId());
                attachedArtVenList.add(artVenListArtVenToAttach);
            }
            ventas.setArtVenList(attachedArtVenList);
            em.persist(ventas);
            for (ArtVen artVenListArtVen : ventas.getArtVenList()) {
                Ventas oldVenIdOfArtVenListArtVen = artVenListArtVen.getVenId();
                artVenListArtVen.setVenId(ventas);
                artVenListArtVen = em.merge(artVenListArtVen);
                if (oldVenIdOfArtVenListArtVen != null) {
                    oldVenIdOfArtVenListArtVen.getArtVenList().remove(artVenListArtVen);
                    oldVenIdOfArtVenListArtVen = em.merge(oldVenIdOfArtVenListArtVen);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ventas ventas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ventas persistentVentas = em.find(Ventas.class, ventas.getVenId());
            List<ArtVen> artVenListOld = persistentVentas.getArtVenList();
            List<ArtVen> artVenListNew = ventas.getArtVenList();
            List<String> illegalOrphanMessages = null;
            for (ArtVen artVenListOldArtVen : artVenListOld) {
                if (!artVenListNew.contains(artVenListOldArtVen)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ArtVen " + artVenListOldArtVen + " since its venId field is not nullable.");
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
            ventas.setArtVenList(artVenListNew);
            ventas = em.merge(ventas);
            for (ArtVen artVenListNewArtVen : artVenListNew) {
                if (!artVenListOld.contains(artVenListNewArtVen)) {
                    Ventas oldVenIdOfArtVenListNewArtVen = artVenListNewArtVen.getVenId();
                    artVenListNewArtVen.setVenId(ventas);
                    artVenListNewArtVen = em.merge(artVenListNewArtVen);
                    if (oldVenIdOfArtVenListNewArtVen != null && !oldVenIdOfArtVenListNewArtVen.equals(ventas)) {
                        oldVenIdOfArtVenListNewArtVen.getArtVenList().remove(artVenListNewArtVen);
                        oldVenIdOfArtVenListNewArtVen = em.merge(oldVenIdOfArtVenListNewArtVen);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ventas.getVenId();
                if (findVentas(id) == null) {
                    throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.");
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
            Ventas ventas;
            try {
                ventas = em.getReference(Ventas.class, id);
                ventas.getVenId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ArtVen> artVenListOrphanCheck = ventas.getArtVenList();
            for (ArtVen artVenListOrphanCheckArtVen : artVenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ventas (" + ventas + ") cannot be destroyed since the ArtVen " + artVenListOrphanCheckArtVen + " in its artVenList field has a non-nullable venId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ventas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ventas> findVentasEntities() {
        return findVentasEntities(true, -1, -1);
    }

    public List<Ventas> findVentasEntities(int maxResults, int firstResult) {
        return findVentasEntities(false, maxResults, firstResult);
    }

    private List<Ventas> findVentasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ventas.class));
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

    public Ventas findVentas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ventas.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ventas> rt = cq.from(Ventas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
