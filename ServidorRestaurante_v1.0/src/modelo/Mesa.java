/**
 * Mesa.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package modelo;

import control.Factura;
import java.util.Scanner;

/**
 * Mesa de un restaurante.
 * 
 */
public class Mesa {
    private int codigo;
    private Localizacion local;
    private int coordenada_x;
    private int coordenada_y;
    private Comanda comanda;
    private Factura factura;
    private boolean ocupada;
    
    /**
     * Construye una mesa.
     * 
     */
    public Mesa(Scanner fichero) throws Exception {
        this.codigo = fichero.nextInt();
        this.coordenada_x = fichero.nextInt();
        this.coordenada_y = fichero.nextInt();
    }
    
    /**
     * Ocupa un cliente una mesa.
     * 
     */
    public void ocupar(){
        ocupada = true;
        comanda = new Comanda();
    }
    
    /**
     * Libera un cliente una mesa.
     * 
     */
    public void liberar(){
        ocupada = false;
        comanda = null;
    }
    
    /**
     * Añade un plato a su comanda.
     * 
     */
    public boolean anadirPlato(Plato plato){
        if(plato != null) {
            if(comanda != null) {
                return comanda.anadirPlato(plato);
            }
        }
        return false;
    }
    
    /**
     * Elimina un plato de su comanda.
     * 
     */
    public boolean eliminarPlato(Plato plato){
        if(plato != null) {
            if(comanda != null) {
                return comanda.eliminarPlato(plato);
            }
        }
        return false;
    }
    
    /**
     * Devuelve el número de platos de su comanda.
     * 
     */
    public Integer obtenerNumPlatos() {
        if(comanda != null) {
            return comanda.obtenerNumPlatos();
        }
        return null;
    }
    
    /**
     * Generar factura de una mesa.
     * 
     */
    public boolean generarFactura(Localizacion local) 
        throws Exception {
        factura = new Factura(this);
        this.local = local;
        return factura.generar(local);
    }
    
    /**
     * Devuelve el codigo de una mesa.
     * 
     */
    public int obtenerCodigo() {
        return codigo;
    }
    
    /**
     * Devuelve la coordenada x de una mesa.
     * 
     */
    public int obtenerCoordenadaX() {
        return coordenada_x;
    }
    
    /**
     * Devuelve la coordenada y de una mesa.
     * 
     */
    public int obtenerCoordenadaY() {
        return coordenada_y;
    }
    
    /**
     * Devuelve el estado de una mesa.
     * 
     */
    public boolean estaOcupada() {
        return ocupada;
    }
    
    /**
     * Devuelve la comanda de una mesa.
     * 
     */
    public Comanda obtenerComanda() {
        return comanda;
    }
    
    /**
     * Sobreescribe equals.
     *
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if( ! (obj instanceof Plato)) {
            return false;
        }
        
        Mesa tmp = (Mesa)obj;
        return (codigo == tmp.codigo);
    }
    
    /**
     * Sobreescribe hashCode.
     *
     */
    @Override
    public int hashCode() {
        int result = 17;
        return 37 * result + codigo;
    }
    
    /**
     * Sobreescribe toString.
     *
     */  
    @Override
    public String toString() {
        String s = "";
        
        if(comanda != null) {
            s = local.devuelve(local.MESA_FACTURA) + codigo + "\n";
            s = s + comanda.toString();
        }
        return s;
    }
}
