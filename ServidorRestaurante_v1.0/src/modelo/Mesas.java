/**
 * Mesas.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package modelo;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Mesas {
    private Map<Integer, Mesa> mesas;
    private Carta carta;
    
    public static String ANADIR_PLATO = "Nuevo plato"; 
    public static String ELIMINAR_PLATO = "Eliminar plato";
    public static String OCUPAR_MESA = "Ocupar mesa";
    public static String GENERAR_FACTURA = "Generar factura de mesa";
    
    private static final int CODIGO_INCORRECTO = -1;
    
    /**
     * Construye mesas.
     * 
     */
    public Mesas (String ficheroMesas, Carta carta) throws Exception {
        this.carta = carta;
        cargarMesas(ficheroMesas);
    }
    
    /**
     * Ocupa un cliente una mesa.
     * 
     */
    public boolean ocuparMesa(int codigoMesa){
        Mesa mesa = mesas.get(codigoMesa);

        if(mesa != null) {
            mesa.ocupar();
            return true;
        }
        return false;
    }
    
    /**
     * Libera un cliente una mesa.
     * 
     */
    public boolean liberarMesa(int codigoMesa) {
        Mesa mesa = mesas.get(codigoMesa);
        
        if(mesa != null) {
            mesa.liberar();
            return true;
        }
        return false;
    }
    
    /**
     * Añade un plato a una comanda.
     * 
     */
    public boolean anadirPlatoAComanda(int codigoMesa, 
            int codigoPlato) {
        Plato plato = null;
        Mesa mesa = null;
        if((mesa = mesas.get(codigoMesa)) != null) {
            if((plato = carta.obtenerPlato(
                    codigoPlato)) != null) {
                return mesa.anadirPlato(plato);
            } 
        }
        return false;
    }
    
    /**
     * Elimina un plato de una comanda.
     * 
     */
    public boolean eliminarPlatoDeComanda(int codigoMesa, 
            int codigoPlato){
        Mesa mesa = null;
        Plato plato = null;
        
        if((mesa = mesas.get(codigoMesa)) != null) {
            if((plato = carta.obtenerPlato(
                    codigoPlato)) != null) {
                return mesa.eliminarPlato(plato);
            }
        }
        return false;
    }
    
    /**
     * Genera factura de una mesa.
     * 
     */
    public boolean generarFactura(int codigoMesa,
            Localizacion local) throws Exception {
        Mesa mesa = mesas.get(codigoMesa);
        
        if(mesa != null){
            if(mesa.generarFactura(local)){
                return liberarMesa(codigoMesa);
            }
        }
        return false;
    }
    
    /**
     * Lee de fichero las mesas del Restaurante.
     * 
     */            
    private void cargarMesas(String ficheroMesas) 
            throws Exception {  
        Scanner scanner = new Scanner(
            new FileInputStream(ficheroMesas));
        
        mesas = new HashMap<>();
        
        while(scanner.hasNext()) {
            Mesa mesa = new Mesa(scanner);
            mesas.put(mesa.obtenerCodigo(), mesa);
        }
        
        scanner.close();
    }
    
    /**
     * Devuelve la comanda de una mesa.
     * 
     */
    public Comanda obtenerComandaMesa(int codigoMesa) {
        Mesa mesa = null;
        
        if((mesa = mesas.get(codigoMesa)) != null) {
            if(mesa.estaOcupada()) {
                if( ! mesa.obtenerComanda().
                        toString().equals("")){
                     return mesa.obtenerComanda();
                }
            } 
        }
        return null;
    }
    
    /**
     * Devuelve el número de platos de una mesa.
     * 
     */
    public Integer obtenerNumPlatos (int codigoMesa) {
        Mesa mesa = null;
        
        if((mesa = mesas.get(codigoMesa)) != null) {
            return mesa.obtenerNumPlatos();
        }
        return null;
    }
    
    /**
     *  Devuelve si una mesa esta ocupada.
     * 
     */ 
    public boolean estaOcupada (int codigoMesa) {
        Mesa mesa = null;
        
        if ((mesa = mesas.get(codigoMesa)) != null ) {
            return mesa.estaOcupada();
        }
        return false;
    }
    
    /**
     *  Busca si existe mesa con ubicación posx, posy.
     * 
     */ 
    public int buscarMesa(int posX, int posY) {
        int codigoMesa = CODIGO_INCORRECTO;
        Collection<Mesa> coleccionMesas = mesas.values();
        
        for(Mesa mesa : coleccionMesas) {
            if((mesa.obtenerCoordenadaX() == posX) && 
                    (mesa.obtenerCoordenadaY() == posY)) {
                codigoMesa = mesa.obtenerCodigo();
            }
        }
        return codigoMesa;
    }
    
    /**
     * Sobreescribe toString.
     *
     */
    @Override
    public String toString() {
        String s = "";
        
        Collection<Mesa> coleccionMesas = mesas.values();
        for(Mesa mesa : coleccionMesas) {
            if((mesa.estaOcupada())) {
                s = s + mesa + "\n";
            }
        }
        
        return s;
    }
}
