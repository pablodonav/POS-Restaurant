/**
 * Localizacion.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package modelo;

import control.ServidorCamareros;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.UIManager;

/**
 * Localizacion recursos de la vista.
 * 
 */
public class Localizacion {
    private ResourceBundle recursos;
    
    public static final String FICHERO_LOCALIZACION = 
        "localizacion"; 
    public static final String FICHERO_IDIOMAS = "idiomas.txt";
    public static final String FICHERO_CARTA = "CARTA";
     
    public static String LENGUAJE_ESPANOL = "es";
    public static String PAIS_ESPANA = "ES";
    public static String LENGUAJE_INGLES = "en";
    public static String PAIS_USA = "US";   
    
    /** Identificadores de textos dependientes del idioma */   
    /* Factura */
    public static final String FACTURA = "FACTURA";
    public static final String TITULO = "TITULO";
    public static final String MESA_FACTURA = "MESA_FACTURA";
    public static final String IVA_FACTURA = "IVA_FACTURA";
    public static final String TOTAL_FACTURA = "TOTAL_FACTURA";
    public static final String UNIDAD_MONETARIA = 
        "UNIDAD_MONETARIA";
    public static final String FICHERO_FACTURA = 
        "FICHERO_FACTURA";
    
    /* DebugVista */
    public static final String CONFIGURACION_NO_GUARDADA = 
            "CONFIGURACION_NO_GUARDADA";
    public static final String 
        FICHERO_CONFIGURACION_NO_ENCONTRADO_O_ERRONEO = 
            "Config file not found or wrong";
    public static final String 
        FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO = 
            "Locale file not found or wrong";

     public static final String ERROR_CONEXION_SERVIDOR = 
        "ERROR_CONEXION_SERVIDOR";
     public static final String ERROR_PUERTO_SERVIDOR =
        "ERROR_PUERTO_SERVIDOR";
     public static final String ERROR_OCUPAR_MESA =
        "ERROR_OCUPAR_MESA";
    
    /* MensajesError */
    public static String FICHERO_CONFIG_WRONG = 
        "FICHERO_CONFIG_WRONG";
    public static String FICHERO_FACTURA_WRONG =
        "FICHERO_FACTURA_WRONG";
    public static String FICHERO_MESAS_WRONG = 
        "FICHERO_MESAS_WRONG";
    public static String FICHERO_CARTA_WRONG = 
        "FICHERO_CARTA_WRONG";
    public static String FICHERO_IDIOMAS_WRONG = 
        "FICHERO_IDIOMAS_WRONG";
    
    /* Encoding*/
    private static String FORMATO_ENCODING = "UTF-8";
    
    /* identificadores sistema */
    String[] sistema = {
            "OptionPane.cancelButtonText", 
            "OptionPane.okButtonText",
            "OptionPane.noButtonText",
            "OptionPane.yesButtonText",
            "OptionPane.yesButtonText" };
    
    /**
    * Construye localizacion
    * 
    */
    public Localizacion(String lenguaje, String pais) { 
        try {   
            Locale locale = new Locale(lenguaje, pais);        
            recursos = ResourceBundle.getBundle(Carta.RUTA_RECURSOS
                    .substring(1).replace('/', '.') + 
                    FICHERO_LOCALIZACION, locale);       
            
            // localiza textos sistema si no son los de defecto
            if (! locale.equals(Locale.getDefault())) {   
                for (int i = 0; i < sistema.length; i++) { 
                    UIManager.put(sistema[i], 
                        recursos.getString(sistema[i]));    
                }
            }
        
        } catch (MissingResourceException e) {
            if (ServidorCamareros.esModoDebug()) {
                System.out.println(
                    FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO);
            }     
        }
    }   
    
    /**
     * Localiza recurso
     * 
     */    
    public String devuelve(String texto) {
        return recursos.getString(texto);    
    }  
}
