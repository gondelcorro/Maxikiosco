/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author DelKo
 */
@Entity
@Table(name = "articulos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Articulos.findAll", query = "SELECT a FROM Articulos a"),
    @NamedQuery(name = "Articulos.findByArtId", query = "SELECT a FROM Articulos a WHERE a.artId = :artId"),
    @NamedQuery(name = "Articulos.findByArtCodigo", query = "SELECT a FROM Articulos a WHERE a.artCodigo = :artCodigo"),
    @NamedQuery(name = "Articulos.findByArtDescripcion", query = "SELECT a FROM Articulos a WHERE a.artDescripcion = :artDescripcion"),
    @NamedQuery(name = "Articulos.findByArtMarca", query = "SELECT a FROM Articulos a WHERE a.artMarca = :artMarca"),
    @NamedQuery(name = "Articulos.findByArtPrecioCompra", query = "SELECT a FROM Articulos a WHERE a.artPrecioCompra = :artPrecioCompra"),
    @NamedQuery(name = "Articulos.findByArtPorcUtil", query = "SELECT a FROM Articulos a WHERE a.artPorcUtil = :artPorcUtil"),
    @NamedQuery(name = "Articulos.findByArtPrecioVenta", query = "SELECT a FROM Articulos a WHERE a.artPrecioVenta = :artPrecioVenta"),
    @NamedQuery(name = "Articulos.findByArtRubro", query = "SELECT a FROM Articulos a WHERE a.artRubro = :artRubro"),
    @NamedQuery(name = "Articulos.findByArtProveedor", query = "SELECT a FROM Articulos a WHERE a.artProveedor = :artProveedor"),
    @NamedQuery(name = "Articulos.findByArtDetalle", query = "SELECT a FROM Articulos a WHERE a.artDetalle = :artDetalle"),
    @NamedQuery(name = "Articulos.findByArtEstado", query = "SELECT a FROM Articulos a WHERE a.artEstado = :artEstado")})
public class Articulos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "art_id")
    private Integer artId;
    @Basic(optional = false)
    @Column(name = "art_codigo")
    private long artCodigo;
    @Basic(optional = false)
    @Column(name = "art_descripcion")
    private String artDescripcion;
    @Column(name = "art_marca")
    private String artMarca;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "art_precio_compra")
    private Float artPrecioCompra;
    @Column(name = "art_porc_util")
    private Float artPorcUtil;
    @Column(name = "art_precio_venta")
    private Float artPrecioVenta;
    @Column(name = "art_rubro")
    private String artRubro;
    @Column(name = "art_proveedor")
    private String artProveedor;
    @Column(name = "art_detalle")
    private String artDetalle;
    @Column(name = "art_estado")
    private Integer artEstado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artId")
    private List<ArtVen> artVenList;

    public Articulos() {
    }

    public Articulos(Integer artId) {
        this.artId = artId;
    }

    public Articulos(Integer artId, long artCodigo, String artDescripcion) {
        this.artId = artId;
        this.artCodigo = artCodigo;
        this.artDescripcion = artDescripcion;
    }

    public Integer getArtId() {
        return artId;
    }

    public void setArtId(Integer artId) {
        this.artId = artId;
    }

    public long getArtCodigo() {
        return artCodigo;
    }

    public void setArtCodigo(long artCodigo) {
        this.artCodigo = artCodigo;
    }

    public String getArtDescripcion() {
        return artDescripcion;
    }

    public void setArtDescripcion(String artDescripcion) {
        this.artDescripcion = artDescripcion;
    }

    public String getArtMarca() {
        return artMarca;
    }

    public void setArtMarca(String artMarca) {
        this.artMarca = artMarca;
    }

    public Float getArtPrecioCompra() {
        return artPrecioCompra;
    }

    public void setArtPrecioCompra(Float artPrecioCompra) {
        this.artPrecioCompra = artPrecioCompra;
    }

    public Float getArtPorcUtil() {
        return artPorcUtil;
    }

    public void setArtPorcUtil(Float artPorcUtil) {
        this.artPorcUtil = artPorcUtil;
    }

    public Float getArtPrecioVenta() {
        return artPrecioVenta;
    }

    public void setArtPrecioVenta(Float artPrecioVenta) {
        this.artPrecioVenta = artPrecioVenta;
    }

    public String getArtRubro() {
        return artRubro;
    }

    public void setArtRubro(String artRubro) {
        this.artRubro = artRubro;
    }

    public String getArtProveedor() {
        return artProveedor;
    }

    public void setArtProveedor(String artProveedor) {
        this.artProveedor = artProveedor;
    }

    public String getArtDetalle() {
        return artDetalle;
    }

    public void setArtDetalle(String artDetalle) {
        this.artDetalle = artDetalle;
    }

    public Integer getArtEstado() {
        return artEstado;
    }

    public void setArtEstado(Integer artEstado) {
        this.artEstado = artEstado;
    }

    @XmlTransient
    public List<ArtVen> getArtVenList() {
        return artVenList;
    }

    public void setArtVenList(List<ArtVen> artVenList) {
        this.artVenList = artVenList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artId != null ? artId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Articulos)) {
            return false;
        }
        Articulos other = (Articulos) object;
        if ((this.artId == null && other.artId != null) || (this.artId != null && !this.artId.equals(other.artId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Articulos[ artId=" + artId + " ]";
    }
    
}
