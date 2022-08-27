/**
 * Carta.java
 * Pablo Doñate y Adnana Dragut (05/2021) .
 *   
 */
package modelo;

import control.Restaurante;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import modelo.camarerosEnlinea.Cliente;
import modelo.camarerosEnlinea.OyenteServidor;
import modelo.camarerosEnlinea.PrimitivaComunicacion;
import vista.DebugVista;
import vista.Localizacion;

/**
 * Carta de un restaurante.
 * 
 */
public class Carta {
    private OyenteServidor oyenteServidor;
    private Localizacion local;
    private Restaurante restaurante;
    private Mesas mesas;
    private Cliente cliente;
    
        private static String FICHERO_CONFIG_ERRONEO = 
          "Fichero configuración erróneo."
            + " Usando parámetros por defecto";
    private Properties configuracion;
    private static final String FICHERO_CONFIG = "config.properties";
    private static String COMENTARIO_CONFIG = 
          Restaurante.VERSION + " configuración conexión Servidor";  
  
    public static final String URL_SERVIDOR = "URLServidor";
    private String URLServidor = "<ponAquiURLServidor>";
    public static final String PUERTO_SERVIDOR = "puertoServidor";
    private int puertoServidor = 15000; 
    
    
    /**
     * Construye una carta de platos.
     * 
     */
    public Carta(Restaurante rest, Mesas mesas){
        this.restaurante = rest;
        this.local = restaurante.devolverLocal();
        this.mesas = mesas;  
        
        leerConfiguracion();
        this.cliente = new Cliente(URLServidor, puertoServidor);
    }
    
     /**
     *  Lee configuración de carta.
     * 
     */ 
    private void leerConfiguracion() {
        try {
            configuracion = new Properties();
            configuracion.load(new FileInputStream(FICHERO_CONFIG));
      
            URLServidor = configuracion.getProperty(URL_SERVIDOR);
            puertoServidor = 
                Integer.parseInt(
                    configuracion.getProperty(PUERTO_SERVIDOR));
      
        } catch (Exception e) {
            configuracion.setProperty(URL_SERVIDOR, URLServidor);
            configuracion.setProperty(PUERTO_SERVIDOR, 
                Integer.toString(puertoServidor));
            guardarConfiguracion();

            if (restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                    FICHERO_CONFIG_ERRONEO, e);
            }
        }
    }
    
    /**
     *  Guarda configuración de carta.
     * 
     */
    private void guardarConfiguracion() {
        try {
            FileOutputStream fichero = new 
                FileOutputStream(FICHERO_CONFIG);
            configuracion.store(fichero, COMENTARIO_CONFIG);
            fichero.close();
        } catch(IOException e) {
            if (restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                    local.devuelve(
                        local.CONFIGURACION_NO_GUARDADA), e);
            }        
        }      
    }   
    
    /**
     * Se solicita al servidor la carta del restaurante.
     * 
     */
    public Object[] obtenerCarta() throws Exception {
         if ( ! mesas.camareroEstaConectado()) {
            return null;
        }
         
        List<String> resultados =  new ArrayList<>();
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_CARTA, 
                Cliente.TIEMPO_ESPERA_SERVIDOR,
                    null, resultados);
        
        if( resultados.isEmpty() || 
            respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
             Object[] carta = resultados.toArray(); 
             return carta;
        }
    }
}
