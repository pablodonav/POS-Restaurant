/**
 * Carta.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package modelo;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Carta de un restaurante.
 * 
 */
public class Carta {
    private Map<Integer, Plato> platos;
    private Localizacion local;
    
    public static final String RUTA_RECURSOS = "/modelo/recursos/";
    
    /**
     * Construye una carta de platos.
     * 
     */
    public Carta(Localizacion local) throws Exception {
        this.local = local;
        
        URL fichero = getClass().getResource(RUTA_RECURSOS 
                + local.devuelve(local.FICHERO_CARTA));
        cargar(fichero);
    }
    
    /**
     * Lee de fichero la carta del Restaurante.
     * 
     */            
    private void cargar(URL nombreFichero) throws Exception { 
        Scanner scanner = new Scanner(nombreFichero.openStream());
        
        platos = new HashMap<>();
        
        while(scanner.hasNext()) {
            Plato plato = new Plato(scanner, local);
            platos.put(plato.obtenerCodigo(),plato);
        }
        
        scanner.close();
    }
    
    /**
     * Devuelve el plato asociado a un codigo dado.
     * 
     */
    public Plato obtenerPlato(int codigo) {
        return platos.get(codigo);
    }
    
    /**
     * Convierte el mapa de Platos en un vector de objetos.
     * 
     */
    public Object[] toArray() {
        return platos.values().toArray();
    }
    
    /**
     * Sobreescribe toString.
     *
     */ 
    @Override
    public String toString() {
        String s = "";
        
        Collection<Plato> listaPlatos = platos.values();
        for(Plato plato : listaPlatos) {
            s = s + plato;
        }
        return s;
    }
}
