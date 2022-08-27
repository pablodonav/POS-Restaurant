/**
 * ServidorCamareros.java
 *
 * Versión 1.0 Pablo Doñate y Adnana Dragut (05/2021)
 *    - Refactorización PrimitivaComunicacion para mejora 
 *      mantenibilidad.
 * 
 */
package control;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import modelo.Carta;
import modelo.Comanda;
import modelo.Localizacion;
import modelo.Mesas;


public class ServidorCamareros extends Thread {
    private static int TIEMPO_TEST_CONEXIONES = 10 * 1000;
    public static int TIEMPO_ESPERA_CLIENTE = 1000;   
  
    private static boolean modoDebug = false;

    private Map<String, ConexionPushRestaurante> 
        conexionesPushCamareros;
    private int numConexion = 0;
    private Mesas mesas;
    private Localizacion local;
    private Carta carta;

    public static String VERSION = "Restaurant Server 1.0";
    private static String ARG_DEBUG = "-d";
    
    private static String FICHERO_CONFIG_ERRONEO = 
            "Config file is wrong. Set default values";
    private static String ESPERANDO_SOLICITUD_CAMARERO = 
            "Waiting for waiter requests...";
    private static String ERROR_EJECUCION_SERVIDOR = 
        "Error: Server running in ";   
    private static String ERROR_CREANDO_CONEXION_CAMARERO = 
            "Failed to create waiter connection";    

    /** Configuración */  
    private Properties propiedades; 
    private static final String FICHERO_CONFIG = 
        "config.properties";

    private static final String NUM_THREADS = "threadsNumber";
    private int numThreads = 16;
    private static final String PUERTO_SERVIDOR = "serverPort";
    private int puertoServidor = 15000;
 
    private static String COMENTARIO_CONFIG = 
        "country = ES|US, language = es|en";
  
    private static final String LENGUAJE = "language";
    private String lenguaje;  
    private static final String PAIS = "country";
    private String pais;
    
    private static final String NOMBRE_FICHERO_MESAS = 
        "ficheroMesas.txt";
    
  
    /**
     *  Construye el servidor de camareros.
     * 
     */   
    public ServidorCamareros(String ficheroMesas) {
        leerConfiguracion();
        
        conexionesPushCamareros = new ConcurrentHashMap<>();
        inicializarMesas(ficheroMesas, carta);
        envioTestPeriodicosConexionesPushCamareros();
        start();
    }
    
    /**
     *  Lee configuración.
     * 
     */ 
    private void leerConfiguracion() {
      
        lenguaje = Locale.getDefault().getLanguage();
        pais = Locale.getDefault().getCountry();
        
        try {
            propiedades = new Properties();
            propiedades.load(new FileInputStream(FICHERO_CONFIG));

            lenguaje = propiedades.getProperty(LENGUAJE);
            pais = propiedades.getProperty(PAIS);
            numThreads = Integer.parseInt(
                    propiedades.getProperty(NUM_THREADS));
            puertoServidor = Integer.parseInt(
                    propiedades.getProperty(PUERTO_SERVIDOR));
          
            // si falta lenguaje o país ponemos valores por defecto
            if ((lenguaje == null) || (pais == null)) {
                lenguaje = Locale.getDefault().getLanguage();
                propiedades.setProperty(LENGUAJE, lenguaje);              
                pais = Locale.getDefault().getCountry();
                propiedades.setProperty(PAIS, pais);
            }
        } catch (Exception e) {
            System.out.println(FICHERO_CONFIG_ERRONEO);
            System.out.println(NUM_THREADS + " = " + numThreads);
            System.out.println(PUERTO_SERVIDOR + " = " + 
                puertoServidor);
            propiedades.setProperty(LENGUAJE, lenguaje);
            propiedades.setProperty(PAIS, pais);
            
            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * Envía tests periódicos para mantener lista conexiones
     * push con camareros.
     *  
     */
    private void envioTestPeriodicosConexionesPushCamareros() {  
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(ConexionPushRestaurante conexionPushRestaurante :
                conexionesPushCamareros.values()) {
                    try {
                        conexionPushRestaurante.enviarSolicitud(
                            PrimitivaComunicacion.TEST, 
                            TIEMPO_TEST_CONEXIONES);                       
                    } catch (IOException e1) {
                        System.out.println(
                            ServidorRestaurante.
                                ERROR_CONEXION_RESTAURANTE + " " + 
                                conexionPushRestaurante.toString());

                        conexionesPushCamareros.remove(
                            conexionPushRestaurante.
                                obtenerIdConexion());
                        try {
                            conexionPushRestaurante.cerrar();
                        } catch (IOException e2) {
                            // No hacemos nada, ya hemos 
                            // cerrado conexión 
                        } 
                        if (ServidorCamareros.esModoDebug()) {
                            e1.printStackTrace();
                        } 
                    }
                }
            }          
        }, TIEMPO_TEST_CONEXIONES, TIEMPO_TEST_CONEXIONES);              
    }  
  
    
    /**
     * Inicializa las mesas de Restaurante.
     * 
     */
    private void inicializarMesas(String ficheroMesas, Carta carta) {
         try {
            mesas = new Mesas(ficheroMesas, carta);
        } catch(Exception e) {
            System.out.println(local.devuelve(
                local.FICHERO_MESAS_WRONG));
        }    
    }
    
    /**
     * Inicializa la carta de Restaurante.
     * 
     */
    private void inicializarCarta(Localizacion local){
        try {
            carta = new Carta(local);
        } catch(Exception e) {
            System.out.println(local.devuelve(
                local.FICHERO_CARTA_WRONG));
        }    
    }
    
    /**
     * Obtener carta del restaurante.
     * 
     */
    synchronized String obtenerCarta() throws Exception {
        ConexionPushRestaurante conexionPushRestaurante = 
            obtenerConexionPushRestaurante(obtenerIdConexion());
        
        System.out.println(obtenerIdConexion());
        lenguaje = conexionPushRestaurante.obtenerLenguajeLocal();
        pais = conexionPushRestaurante.obtenerPaisLocal();
        local = new Localizacion(lenguaje, pais); 
        inicializarCarta(local);
        return carta.toString();
    } 
    
    /**
     * Obtener la comanda de una mesa.
     * 
     */
    synchronized String obtenerComandaMesa(int codigoMesa) {
        Comanda comanda = null;
        if((comanda = 
            mesas.obtenerComandaMesa(codigoMesa)) == null) {
            return null;
        } else {
            return comanda.toString();
        }
    } 
    
    /**
     * Obtener la información de un plato.
     * 
     */
    synchronized String obtenerInfoPlato(int codigoPlato) {
        String plato = null;
        if((plato = 
            carta.obtenerPlato(codigoPlato).toString()) == null) {
            return null;
        } else {
            return plato;
        }
    }
    
    /**
     * Obtener el numero de platos de una mesa.
     * 
     */
    synchronized Integer obtenerNumPlatos(int codigoMesa) {
        return mesas.obtenerNumPlatos(codigoMesa);
    }
    
    /**
     * Devuelve el estado de una mesa.
     * 
     */
    synchronized boolean mesaEstaDisponible(int codigoMesa) {
        return (mesas.estaOcupada(codigoMesa));
    } 
    
    /**
     * Devuelve la carta del restaurante.
     * 
     */
    public Carta devolverCarta() {
        return carta;
    }
    
    /**
     * Devuelve el codigo de una mesa.
     * 
     */
    synchronized int obtenerIDMesa(int posX, int posY) {
        return mesas.buscarMesa(posX, posY);
    }

    /**
     *  Indica si aplicación está en modo debug.
     * 
     */  
    public static boolean esModoDebug() {
        return modoDebug;
    }
    
    /**
     *  Ejecuta bucle espera conexiones.
     * 
     */   
    @Override
    public void run() { 
        System.out.println(VERSION);  
        try {
            ExecutorService poolThreads = 
                    Executors.newFixedThreadPool(numThreads);
      
            ServerSocket serverSocket = 
                new ServerSocket(puertoServidor);
      
            while(true) {
                System.out.println(ESPERANDO_SOLICITUD_CAMARERO);  

                Socket socket = serverSocket.accept();
                poolThreads.execute(
                    new ServidorRestaurante(this, socket));
            }      
      
        } catch (BindException e) {
            System.out.println(ERROR_EJECUCION_SERVIDOR + 
                puertoServidor);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(ERROR_CREANDO_CONEXION_CAMARERO);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }
  
    /**
     *  Genera nuevo identificador de conexión push restaurante.
     * 
     */
    synchronized String generarIdConexionPushRestaurante() { 
        return String.valueOf(++numConexion); 
    }
  
    /**
     * Devuelve el numero de conexión.
     * 
     */
    synchronized String obtenerIdConexion() { 
        return String.valueOf(numConexion);
    }
  
    /**
     *  Obtiene conexión push restaurante por id conexión.
     * 
     */
    ConexionPushRestaurante obtenerConexionPushRestaurante(
            String idConexion) {
        ConexionPushRestaurante conexionPushRestaurante = 
                conexionesPushCamareros.get(idConexion);
    
        if (conexionPushRestaurante != null) {
            return conexionPushRestaurante;
        }
    
        return null;
    }
    
    /**
     * Ocupa una mesa y notifica a todos los camareros.
     * 
     */
    synchronized boolean ocuparMesa(String codigoMesa) 
            throws IOException {
        if ( ! mesas.ocuparMesa(Integer.parseInt(codigoMesa))){
            return false;
        }
        notificarCamarerosPush(
            PrimitivaComunicacion.OCUPAR_MESA, codigoMesa);
        return true;
    }
    
    /**
     * Añade un plato a una mesa y notifica a los camareros.
     * 
     */
    synchronized boolean anadirPlato(int codigoMesa, int codigoPlato) 
            throws IOException {
        if( ! mesas.anadirPlatoAComanda(codigoMesa, codigoPlato)) {
            return false;
        }
        notificarCamarerosPush(PrimitivaComunicacion.ANADIR_PLATO, 
                String.valueOf(codigoMesa + "\n" + codigoPlato));
        return true;
    }
    
    /**
     * Eliminar un plato de una mesa y notifica a los camareros.
     * 
     */
    synchronized boolean eliminarPlato(int codigoMesa, int codigoPlato) 
            throws IOException {
        if( ! mesas.eliminarPlatoDeComanda(codigoMesa, codigoPlato)) {
            return false;
        }
        notificarCamarerosPush(PrimitivaComunicacion.ELIMINAR_PLATO, 
                String.valueOf(codigoMesa + "\n" + codigoPlato));
        return true;
    }
    
    /**
     * Genera factura de una mesa y notifica a los camareros.
     * 
     */
    synchronized boolean generarFactura(int codigoMesa) 
            throws Exception {
        if( ! mesas.generarFactura(codigoMesa, local)) {
            return false;
        }
        notificarCamarerosPush(PrimitivaComunicacion.FACTURAR_MESA, 
                String.valueOf(codigoMesa));
        return true;
    }
    
    /**
     *  Nueva conexión push restaurante.
     * 
     */   
    synchronized void nuevaConexionPushRestaurante(
            ConexionPushRestaurante _conexionPushRestaurante) {
        
        conexionesPushCamareros.put(
            _conexionPushRestaurante.obtenerIdConexion(), 
            _conexionPushRestaurante);  
    }
    
    /**
     *  Elimina conexión push restaurante.
     * 
     */   
    synchronized boolean eliminarConexionPushRestaurante(
            String idConexion) throws IOException {
        ConexionPushRestaurante conexionPushRestaurante = 
                conexionesPushCamareros.get(idConexion);

        if (conexionPushRestaurante == null) {
          return false;
        }            

        conexionPushRestaurante.cerrar();
        conexionesPushCamareros.remove(idConexion); 

        return true;
    }
    
    /**
     *  Notifica cambio restaurante al resto de camareros.
     * 
     */ 
    private void notificarCamarerosPush(
            PrimitivaComunicacion primitivaComunicacion, 
            String parametros)
            throws IOException {
      for(ConexionPushRestaurante conexionPushRestaurante : 
              conexionesPushCamareros.values()) {
            conexionPushRestaurante.enviarSolicitud(
                primitivaComunicacion, 
                TIEMPO_ESPERA_CLIENTE, 
                parametros);        
      }
  }
    
    /**
     *  Procesa argumentos de main.
     * 
     */  
    private static void procesarArgsMain(String[] args) {
        List<String> argumentos = 
            new ArrayList<String>(Arrays.asList(args));  
    
        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;    
        }
    } 
  
    /**
     *  Método main.
     * 
     */ 
    public static void main(String args[]) { 
        procesarArgsMain(args);  

        new ServidorCamareros(NOMBRE_FICHERO_MESAS);
    }
}
