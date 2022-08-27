/**
 * Plato.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package modelo;

import java.util.Scanner;
import vista.Localizacion;

/**
 * Plato de un restaurante.
 * 
 */
public class Plato {
    private int identificador;
    private String nombre;
    private double precio;
    private Localizacion local;
    
    /**
     * Construye un plato.
     * 
     */
    public Plato(Scanner fichero, Localizacion local) throws Exception {
        this.local = local;
        this.identificador = Integer.parseInt(fichero.nextLine());
        this.nombre = fichero.nextLine();
        this.precio = Double.parseDouble(fichero.nextLine());
    }
    
    /**
     * Devuelve el identificador de un plato.
     * 
     */
    public int obtenerCodigo() {
        return identificador;
    }
    
    /**
     * Devuelve el precio de un plato.
     * 
     */
    public double obtenerPrecio() {
        return precio;
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
        
        Plato tmp = (Plato)obj;
        return (identificador == tmp.identificador);
    }
    
    /**
     * Sobreescribe hashCode.
     *
     */
    @Override
    public int hashCode() {
        int result = 17;
        return 37 * result + identificador;
    }
    
    /**
     * Sobreescribe toString.
     *
     */
    @Override
    public String toString(){
        String s = identificador + " " + nombre + " " + precio + " " + 
                local.devuelve(local.UNIDAD_MONETARIA) + "\n";
        return s;
    }
      
}
