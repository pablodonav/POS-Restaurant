/**
 * Comanda.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Comanda de una mesa.
 * 
 */
public class Comanda {
    private List<Plato> platos;
    
    /**
     * Construye una comanda.
     * 
     */
    public Comanda(){
        this.platos = new ArrayList<Plato>();
    }
    
    /**
     * Añadir un plato a la comanda.
     * 
     */
    public boolean anadirPlato(Plato plato) {
        if(plato != null) {
            platos.add(plato);
            return true;
        }
        return false;
    }
    
    /**
     * Eliminar un plato de la comanda.
     * 
     */
    public boolean eliminarPlato(Plato plato){
        return platos.remove(plato);
    }
    
    /**
     * Devuelve los platos de una comanda.
     * 
     */
    public Plato[] obtenerPlatos() {
        Plato [] platosAux = new Plato[platos.size()];
        return platos.toArray(platosAux);
    }
    
    /**
     * Devuelve el número de platos de la comanda.
     * 
     */
    public Integer obtenerNumPlatos() {
        return platos.size();
    }
    
    /**
     * Devuelve verdad si y solo si hay comanda.
     * 
     */
    public boolean hayComanda() {
        return ! platos.isEmpty();
    }
    
    /**
     * Sobreescribe toString.
     *
     */
    @Override
    public String toString() {
        String s = "";
        
        Plato[] listaPlatos = obtenerPlatos();
        for(Plato plato : listaPlatos) {
            s = s + plato;
        }
        return s;
    }
}
