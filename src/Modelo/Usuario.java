/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DelKo
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByUsuId", query = "SELECT u FROM Usuario u WHERE u.usuId = :usuId"),
    @NamedQuery(name = "Usuario.findByUsuNombre", query = "SELECT u FROM Usuario u WHERE u.usuNombre = :usuNombre"),
    @NamedQuery(name = "Usuario.findByUsuContrasenia", query = "SELECT u FROM Usuario u WHERE u.usuContrasenia = :usuContrasenia"),
    @NamedQuery(name = "Usuario.findByUsuTipo", query = "SELECT u FROM Usuario u WHERE u.usuTipo = :usuTipo"),
    @NamedQuery(name = "Usuario.findByUsuEstado", query = "SELECT u FROM Usuario u WHERE u.usuEstado = :usuEstado")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "usu_id")
    private Integer usuId;
    @Basic(optional = false)
    @Column(name = "usu_nombre")
    private String usuNombre;
    @Basic(optional = false)
    @Column(name = "usu_contrasenia")
    private String usuContrasenia;
    @Basic(optional = false)
    @Column(name = "usu_tipo")
    private int usuTipo;
    @Column(name = "usu_estado")
    private Integer usuEstado;

    public Usuario() {
    }

    public Usuario(Integer usuId) {
        this.usuId = usuId;
    }

    public Usuario(Integer usuId, String usuNombre, String usuContrasenia, int usuTipo) {
        this.usuId = usuId;
        this.usuNombre = usuNombre;
        this.usuContrasenia = usuContrasenia;
        this.usuTipo = usuTipo;
    }

    public Integer getUsuId() {
        return usuId;
    }

    public void setUsuId(Integer usuId) {
        this.usuId = usuId;
    }

    public String getUsuNombre() {
        return usuNombre;
    }

    public void setUsuNombre(String usuNombre) {
        this.usuNombre = usuNombre;
    }

    public String getUsuContrasenia() {
        return usuContrasenia;
    }

    public void setUsuContrasenia(String usuContrasenia) {
        this.usuContrasenia = usuContrasenia;
    }

    public int getUsuTipo() {
        return usuTipo;
    }

    public void setUsuTipo(int usuTipo) {
        this.usuTipo = usuTipo;
    }

    public Integer getUsuEstado() {
        return usuEstado;
    }

    public void setUsuEstado(Integer usuEstado) {
        this.usuEstado = usuEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuId != null ? usuId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.usuId == null && other.usuId != null) || (this.usuId != null && !this.usuId.equals(other.usuId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Usuario[ usuId=" + usuId + " ]";
    }
    
}
