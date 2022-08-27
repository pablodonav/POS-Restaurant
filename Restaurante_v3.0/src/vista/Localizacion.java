/**
 * Localizacion.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package vista;

import control.Restaurante;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.swing.UIManager;

/**
 * Localizacion recursos de la vista.
 * 
 */
public class Localizacion {
    private static Localizacion instancia = null; // es singleton
    private ResourceBundle recursos;
    
    public static final String FICHERO_LOCALIZACION = "localizacion"; 
    public static final String FICHERO_IDIOMAS = "idiomas.txt";
     
    public static String LENGUAJE_ESPANOL = "es";
    public static String PAIS_ESPANA = "ES";
    public static String LENGUAJE_INGLES = "en";
    public static String PAIS_USA = "US";   
    
    /** Identificadores de textos dependientes del idioma */
    /* RestauranteVista */
    public static final String TITULO = "TITULO";
    public static final String MENU_ITEM_LENGUAJE = 
        "MENU_ITEM_LENGUAJE";
    
    public static final String ETIQUETA_COMANDA = 
        "ETIQUETA_COMANDA";
    public static final String TITULO_VENTANA_PLATOS = 
        "TITULO_VENTANA_PLATOS";
    public static final String TITULO_VENTANA_IDIOMAS = 
        "TITULO_VENTANA_IDIOMAS";
  
    public static final String OCUPAR_MESA = "OCUPAR_MESA";
    public static final String GENERAR_FACTURA = "GENERAR_FACTURA";
    public static final String COMANDA_ANADIR_PLATO = 
        "COMANDA_ANADIR_PLATO";
    public static final String COMANDA_ELIMINAR_PLATO = 
        "COMANDA_ELIMINAR_PLATO";
    public static final String DEBUG = "DEBUG";
    public static final String SALIR = "SALIR";
    
    public static final String ESTADO_CONECTADO = "ESTADO_CONECTADO";
    public static final String ESTADO_DESCONECTADO = 
        "ESTADO_DESCONECTADO";
  
    public static final String TITULO_CONFIRMACION_LENGUAJE = 
        "TITULO_CONFIRMACION_LENGUAJE";
    public static final String CONFIRMACION_LENGUAJE = 
        "CONFIRMACION_LENGUAJE";
   
    public static final String UNIDAD_MONETARIA = "UNIDAD_MONETARIA";
    
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
    public static final String ERROR_ANADIR_PLATO =
        "ERROR_ANADIR_PLATO";
    public static final String ERROR_ELIMINAR_PLATO = 
        "ERROR_ELIMINAR_PLATO";
    public static final String ERROR_OBTENER_COMANDA_MESA = 
            "ERROR_OBTENER_COMANDA_MESA";
    public static final String ERROR_GENERAR_FACTURA = 
            "ERROR_GENERAR_FACTURA";
     
    /* MensajesError */
    public static String FICHERO_CONFIG_WRONG = 
        "FICHERO_CONFIG_WRONG";
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
    * Construye localizacion.
    * 
    */
    private Localizacion(String lenguaje, String pais) { 
        try {   
            Locale locale = new Locale(lenguaje, pais);        
            recursos = ResourceBundle.getBundle(
                RestauranteVista.RUTA_RECURSOS.
                    substring(1).replace('/', '.') + 
                    FICHERO_LOCALIZACION, locale);       
            
            // localiza textos sistema si no son los de defecto
            if (! locale.equals(Locale.getDefault())) {   
                for (int i = 0; i < sistema.length; i++) { 
                    UIManager.put(sistema[i], 
                        recursos.getString(sistema[i]));    
                }
            }
        
        } catch (MissingResourceException e) {
            if (Restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar
                    (FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO, e);
            }     
        }
    }   

    /**
     * Devuelve la instancia de la localizacion.
     * 
     */        
    public static synchronized Localizacion 
        devolverInstancia(String lenguaje, String pais) { 
        if (instancia == null) {
            instancia = new Localizacion(lenguaje, pais);
        }
        return instancia;
    } 

    /**
     * Localiza recurso.
     * 
     */    
    public String devuelve(String texto) {
        return recursos.getString(texto);    
    }  
    
    /**
     * Devuelve un vector con los idiomas disponibles.
     * 
     */
    public String[] obtenerIdiomas() throws Exception {
        int max_idiomas = 0;
        
        URL rutaFichero = getClass().getResource(
            RestauranteVista.RUTA_RECURSOS + FICHERO_IDIOMAS);
        Scanner scanner = new Scanner(
            rutaFichero.openStream(), FORMATO_ENCODING);
        
        max_idiomas = devuelveNumIdiomas();
        String[] idiomas = new String[max_idiomas];
        
        for(int i = 0; i < idiomas.length; i++) {
            idiomas[i] = scanner.nextLine();
        }
        
        scanner.close();
        
        return idiomas;
    }
    
    /**
     * Devuelve el numero de idiomas disponibles.
     * 
     */ 
    private int devuelveNumIdiomas() throws Exception {
        int totalIdiomas = 0;
        
        URL rutaFichero = getClass().getResource(
            RestauranteVista.RUTA_RECURSOS + FICHERO_IDIOMAS);
        BufferedReader bf = new BufferedReader(
            new InputStreamReader(rutaFichero.openConnection().
                getInputStream()));
        
        while(bf.readLine() != null) {
            totalIdiomas++;
        }
        bf.close();
        
        return totalIdiomas;
    }
}
