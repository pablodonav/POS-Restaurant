/**
 * Restaurante.java
 * 
 * Versión 0 Pablo Doñate y Adnana Dragut (03/2021)
 *  - Esqueleto de código
 *
 * Versión 1.0 Pablo Doñate y Adnana Dragut (03/2021)
 *  - Crea un restaurante con platos y mesas
 *
 * Versión 1.1 Pablo Doñate y Adnana Dragut (03/2021)
 *  - Permite tomar comandas(añadir/eliminar platos)
 *
 * Versión 1.2 Pablo Doñate y Adnana Dragut (03/2021)
 *  - Permite generar factura de una mesa en formato de fichero
 * 
 * Versión 2.0 Pablo Doñate y Adnana Dragut (04/2021)
 *  - Se han modificado todos los arrays por contenedores
 * 
 * Versión 2.1 Pablo Doñate y Adnana Dragut (04/2021)
 *  - Arquitectura MVC, paquetes de modelo, vista y control
 *  - Vista interfaz gráfica de ventanas con Java Swing
 * 
 *  Versión 2.2 Pablo Doñate y Adnana Dragut (04/2021)
 *  - La interfaz de usuario ha sido internacionalizada y localizada 
 *  - (Castellano - Inglés)
 *  
 *  Versión 2.2.1 Pablo Doñate y Adnana Dragut (04/2021)
 *  - Se ha sustituido el JMenu por un JList para el cambio de idioma
 *  - Además, se ha implementado la clase DebugVista
 *
 *  Versión 2.3 Pablo Doñate y Adnana Dragut (04/2021)
 *  - Se ha añadido la clase ComandaVista
 *  - Se ha llevado a cabo el tratamiento de excepciones
 *
 *  Versión 3.0 Pablo Doñate y Adnana Dragut (05/2021)
 *    - Guarda restaurante en servidor
 *    - Refactorización PrimitivaComunicacion para mejora 
 *          mantenibilidad
 *    - Refactorización Cliente para evitar posible pérdida
 *          datos con servidor.
 * 
 */ 
package control;

import java.io.FileInputStream;
import modelo.Carta;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import modelo.Mesas;
import modelo.Tupla;
import vista.DebugVista;
import vista.Localizacion;
import vista.RestauranteVista;

/**
 * Restaurante.
 * 
 */
public class Restaurante implements OyenteVista {
    private Carta carta;
    private Mesas mesas;
    private RestauranteVista vista;
    private Localizacion local;
    
    private static boolean modoDebug = false;
    
    public static String VERSION = "v3.0";
    
    private static String ARG_DEBUG = "-d";
    
    /** Configuración */  
    private static String COMENTARIO_CONFIG = "country = ES|US, "
        + "language = es|en";
  
    private Properties configuracion; 
    private static final String FICHERO_CONFIG = "config.properties";
  
    private static final String LENGUAJE = "language";
    private String lenguaje;  
    private static final String PAIS = "country";
    private String pais;

     
    /**
    * Construye un Restaurante.
    * 
    */            
    public Restaurante(){
        leerConfiguracion();
        
        mesas = new Mesas(this);
        carta = new Carta(this, mesas);
        local = Localizacion.devolverInstancia(lenguaje, pais);
        
        vista = RestauranteVista.instancia(this, mesas, lenguaje,
            pais, VERSION, carta);
        mesas.nuevoObservador(vista);
        
        mesas.conectar();
    }
    
    /**
     *  Lee configuración.
     * 
     */ 
    private void leerConfiguracion() {
        // valores por defecto de localización;  
        lenguaje = Locale.getDefault().getLanguage();
        pais = Locale.getDefault().getCountry();
    
        try {
            configuracion = new Properties();
            configuracion.load(new FileInputStream(FICHERO_CONFIG));

            lenguaje = configuracion.getProperty(LENGUAJE);
            pais = configuracion.getProperty(PAIS);
            
            // si falta lenguaje o país ponemos valores por defecto
            if ((lenguaje == null) || (pais == null)) {
                lenguaje = Locale.getDefault().getLanguage();
                configuracion.setProperty(LENGUAJE, lenguaje);              
                pais = Locale.getDefault().getCountry();
                configuracion.setProperty(PAIS, pais);
            }
        } catch (Exception e) {
            configuracion.setProperty(LENGUAJE, lenguaje);
            configuracion.setProperty(PAIS, pais);
            mensajeError(local.FICHERO_CONFIG_WRONG, e);

            if (esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                    local.devuelve(local.FICHERO_CONFIG_WRONG), e);
            }
        }
    }
    
    /**
     * Cambia lenguaje.
     * 
     */  
    private void cambiarLenguaje(Tupla tupla) {
        configuracion.setProperty(LENGUAJE, (String)tupla.a);
        configuracion.setProperty(PAIS, (String)tupla.b);
        
        salir();
    }
    
    /**
     * Guarda la nueva configuración y sale.
     * 
     */
    private void salir() {
        try {
            FileOutputStream fichero = new 
                FileOutputStream(FICHERO_CONFIG);
            configuracion.store(fichero, COMENTARIO_CONFIG);
            mesas.desconectar();
            fichero.close();
        } catch(Exception e) {
            mensajeError(local.CONFIGURACION_NO_GUARDADA, e);
        }    
        
        System.exit(0);
    }
    
    /**
     * Recibe eventos de vista.
     * 
     */
    public void eventoProducido(Evento evento, Object obj,
        Localizacion local) {
        switch(evento) {
            case OCUPAR_MESA: 
                 try {
                    mesas.ocuparMesa((int) obj);
                }catch(Exception ex) {
                    mensajeError(local.ERROR_OCUPAR_MESA, ex);
                } 
                break;

            case ANADIR_PLATO:             
                try {
                    Tupla<Integer, Integer> tuplaAn = (Tupla<Integer,
                        Integer>)obj;
                    mesas.anadirPlatoAComanda(tuplaAn.a, tuplaAn.b);
                } catch (Exception ex) {
                    mensajeError(local.ERROR_ANADIR_PLATO, ex);
                }
            
                break;

            case ELIMINAR_PLATO: 
                try {
                    Tupla<Integer, Integer> tuplaEl = (Tupla<Integer,
                        Integer>)obj;
                    mesas.eliminarPlatoDeComanda(tuplaEl.a, tuplaEl.b);
                } catch (Exception ex) {
                    mensajeError(local.ERROR_ELIMINAR_PLATO, ex);
                }
                break; 
            
            case GENERAR_FACTURA:
                try {
                    mesas.generarFactura((int) obj);
                }catch(Exception ex) {
                    mensajeError(local.ERROR_GENERAR_FACTURA, ex);
                } 
                break;
                
            case CAMBIAR_LENGUAJE:
                cambiarLenguaje((Tupla) obj);
                break;
                
            case SALIR:
                salir();
                break;

        }
    } 
    
    /**
     * Devuelve la carta del restaurante.
     * 
     */
    public Carta devolverCarta() {
        return carta;
    }
    
    /**
     *  Escribe mensaje error.
     * 
     */
    private void mensajeError(String mensaje, Exception e) {
        if (esModoDebug()) {
            DebugVista.devolverInstancia().mostrar(mensaje, e);
        } else {
            vista.mensajeDialogo(local.devuelve(mensaje));
        }
    }
  
    /**
     *  Indica si aplicación está en modo debug.
     * 
     */  
    public static boolean esModoDebug() {
        return modoDebug;
    }
    
    /**
     * Devuelve la vista del restaurante.
     * 
     */
    public RestauranteVista obtenerVista() {
        return vista;
    }
    
    /**
     * Devuelve la localización del Restaurante.
     * 
     */
    public Localizacion devolverLocal(){
        return local;
    }
    
    /**
     * Devuelve el lenguaje y el país leídos de fichero.
     * 
     */
    public String devolverLenguajeYPais(){
        return lenguaje + "\n" + pais;
    }
    
    /**
     *  Procesa argumentos de main.
     * 
     */  
    private static void procesarArgsMain(String[] args) {
        List<String> argumentos = new 
            ArrayList<String>(Arrays.asList(args));  
    
        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;    
        }
  } 
    
    /**
     *  Método main.
     * 
     */   
    public static void main(String[] args) {
        procesarArgsMain(args);
        new Restaurante(); 
    }
}
