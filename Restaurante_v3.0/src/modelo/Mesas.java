/**
 * Mesas.java
 * Pablo Doñate y Adnana Dragut (05/2021).
 *   
 */
package modelo;

import control.Restaurante;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import modelo.camarerosEnlinea.Cliente;
import modelo.camarerosEnlinea.OyenteServidor;
import modelo.camarerosEnlinea.PrimitivaComunicacion;
import vista.DebugVista;
import vista.Localizacion;

public class Mesas implements OyenteServidor{
    private OyenteServidor oyenteServidor;
    private PropertyChangeSupport observadores;
    private Restaurante restaurante;
    private Localizacion local;
    
    public static String PROPIEDAD_ANADIR_PLATO = "Nuevo plato"; 
    public static String PROPIEDAD_ELIMINAR_PLATO = "Eliminar plato";
    public static String PROPIEDAD_OCUPAR_MESA = "Ocupar mesa";
    public static String PROPIEDAD_GENERAR_FACTURA = 
        "Generar factura de mesa";
    public static String PROPIEDAD_CONECTADO = "Conectado";

    private Cliente cliente; 
    private boolean conectado;  
    private String idConexion;
    
    private static String FICHERO_CONFIG_ERRONEO = 
          "Fichero configuración erróneo. "
            + "Usando parámetros por defecto";
    private Properties configuracion;
    private static final String FICHERO_CONFIG = "config.properties";
    private static String COMENTARIO_CONFIG = 
          Restaurante.VERSION + " configuración conexión Servidor";  
  
    public static final String URL_SERVIDOR = "URLServidor";
    private String URLServidor = "<ponAquiURLServidor>";
    public static final String PUERTO_SERVIDOR = "puertoServidor";
    private int puertoServidor = 15000; 
    
    private static final int CODIGO_INCORRECTO = -1;
    
    /**
     * Construye mesas.
     * 
     */
    public Mesas (Restaurante rest){
        oyenteServidor = this;
        restaurante = rest;
        local = restaurante.devolverLocal();
        conectado = false;
        observadores = new PropertyChangeSupport(this);    
    
        leerConfiguracion();    
      
        cliente = new Cliente(URLServidor, puertoServidor);  
    }
    
    /**
     *  Lee configuración del fichero de mesas.
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
            configuracion.setProperty(
                PUERTO_SERVIDOR, Integer.toString(puertoServidor));
            guardarConfiguracion();

            if (restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                    FICHERO_CONFIG_ERRONEO, e);
            }
        }
    }
  
    /**
     *  Guarda configuración de mesas.
     * 
     */
    private void guardarConfiguracion() {
        try {
            FileOutputStream fichero = 
                new FileOutputStream(FICHERO_CONFIG);
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
     *  Añade nuevo observador de las mesas.
     * 
     */     
    public void nuevoObservador(PropertyChangeListener observador) {
        this.observadores.addPropertyChangeListener(observador);
    } 
    
    /**
     *  Conecta con servidor mediante long polling.
     * 
     */        
    public void conectar() {
        new Thread() {
            @Override
            public void run() { 
                Cliente cliente = 
                    new Cliente(URLServidor, puertoServidor);

                while(true) { 
                    try { 
                        String parametros = restaurante.devolverLenguajeYPais();
                        cliente.enviarSolicitudLongPolling(
                            PrimitivaComunicacion.CONECTAR_PUSH, 
                            Cliente.TIEMPO_ESPERA_LARGA_ENCUESTA,
                            parametros, 
                            oyenteServidor);    
                    } catch (Exception e) {
                        conectado = false;  
                        observadores.firePropertyChange(
                            PROPIEDAD_CONECTADO, null, conectado);

                        if (restaurante.esModoDebug()) {
                            DebugVista.devolverInstancia().mostrar(
                                local.ERROR_CONEXION_SERVIDOR, e);
                        }             

                        // Volvemos a intentar conexión 
                        // pasado un tiempo
                        try { 
                            sleep(Cliente.
                                TIEMPO_REINTENTO_CONEXION_SERVIDOR);
                        } catch (InterruptedException e2) {
                            // Propagamos a la máquina virtual
                            new RuntimeException();     
                        }            
                    }
                }
            }
        }.start(); 
    }  
    
    /**
     *  Desconecta del servidor.
     * 
     */     
    public void desconectar() throws Exception {  
        if ( ! conectado) {
            return;
        }

        cliente.enviarSolicitud(PrimitivaComunicacion.DESCONECTAR_PUSH, 
            Cliente.TIEMPO_ESPERA_SERVIDOR, 
            idConexion);
    }
    
    /**
     * Envia solicitud de ocupar mesa.
     * 
     */
    public void ocuparMesa(int codigoMesa) throws Exception {
         if ( ! conectado) {
            return;
        }
        String parametros = codigoMesa + "";
        cliente.enviarSolicitud(PrimitivaComunicacion.OCUPAR_MESA, 
            Cliente.TIEMPO_ESPERA_SERVIDOR, 
            parametros);
    }
    
    /**
     * Solicita el codigo de una mesa.
     * 
     */
    public int buscarMesa(int posX, int posY) throws Exception {
         if ( ! conectado) {
            return CODIGO_INCORRECTO;
        }
         
        String parametros = posX + " " + posY;
        List<String> resultados =  new ArrayList<>();
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_ID_MESA, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros, resultados);
        
        return resultados.isEmpty() || 
            respuesta.equals(PrimitivaComunicacion.NOK.toString()) ? 
            CODIGO_INCORRECTO : Integer.parseInt(resultados.get(0));
    }
    
    /**
     * Solicita la comanda de una mesa.
     * 
     */ 
    public Object[] obtenerComandaMesa(int codigoMesa) 
            throws Exception {
        if ( ! conectado) {
            return null;
        }
        
        String parametros = String.valueOf(codigoMesa);
        
        List<String> resultados =  new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_COMANDA_MESA , 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros,
                resultados);
        if( resultados.isEmpty() || 
            respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
            Object[] comanda = parsearComanda(resultados);
            return comanda;
        }
    }
    
    /**
     * Solicita la información completa de un plato.
     * 
     */ 
    public String obtenerInfoPlato(int codigoPlato) throws Exception {
        if ( ! conectado) {
            return null;
        }
        
        String parametros = String.valueOf(codigoPlato);
        
        List<String> resultados =  new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_PLATO, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros,
                resultados);
        
        if( resultados.isEmpty() || 
            respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
            return resultados.get(0);
        }
    }
    
    /**
     * Solicita el número de platos de la comanda de una mesa.
     * 
     */ 
    public Integer obtenerNumPlatos(int codigoMesa) throws Exception {
        if ( ! conectado) {
            return null;
        }
        
        String parametros = String.valueOf(codigoMesa);
        
        List<String> resultados =  new ArrayList<>();

        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_NUM_PLATOS, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros,
                resultados);
        
        if( resultados.isEmpty() || 
            respuesta.equals(PrimitivaComunicacion.NOK.toString())) {
            return null;
        } else {
            return Integer.parseInt(resultados.get(0));
        }
    }
    
    /**
     * Se obtiene la comanda en un vector de objetos.
     * 
     */ 
    private Object[] parsearComanda(List<String> resultados) {
        Object[] platos = new Object[resultados.size()];
        for(int i = 0; i < platos.length; i++) {
            String plato = resultados.get(i);
            if (! plato.isEmpty()) {
                platos[i] = plato;
            }
        }
        return platos;
    }   
    
    /**
     * Solicita el estado de una mesa.
     * 
     */ 
    public boolean mesaEstaOcupada(int codigoMesa) throws Exception {
        if ( ! conectado) {
            return false;
        }
        
        String parametros = String.valueOf(codigoMesa);
        List<String> resultados =  new ArrayList<>();
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.OBTENER_DISPONIBILIDAD_MESA, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros,
                resultados);
        
        if( resultados.isEmpty() || respuesta.equals(
                PrimitivaComunicacion.NOK.toString())) {
            return false;
        } else {
            boolean estadoMesa = 
                Boolean.parseBoolean(resultados.get(0));
            return estadoMesa;
        }
    }
    
    /**
     * Notifica al servidor que tiene que añadir un plato
     * a una comanda.
     * 
     */ 
    public void anadirPlatoAComanda(int codigoMesa, int codigoPlato) 
            throws Exception {
        if ( ! conectado) {
            return;
        }
        String parametros = String.valueOf(codigoMesa) + "\n" + 
                String.valueOf(codigoPlato);
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.ANADIR_PLATO, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros);
    }
    
    /**
     * Notifica al servidor que tiene que eliminar un 
     * plato de una comanda.
     * 
     */ 
    public void eliminarPlatoDeComanda(int codigoMesa, int codigoPlato) 
            throws Exception {
        if ( ! conectado) {
            return;
        }
        String parametros = String.valueOf(codigoMesa) + "\n" + 
                String.valueOf(codigoPlato);
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.ELIMINAR_PLATO, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros);
    }
      
    /**
     * Notifica al servidor que genere una factura.
     * 
     */ 
    public void generarFactura(int codigoMesa) 
            throws Exception {
        if ( ! conectado) {
            return;
        }
        String parametros = String.valueOf(codigoMesa);
        
        PrimitivaComunicacion respuesta = cliente.enviarSolicitud(
                PrimitivaComunicacion.FACTURAR_MESA, 
                Cliente.TIEMPO_ESPERA_SERVIDOR, 
                parametros);
    }
    
    /**
     * Devuelve cierto si camarero esta conectado con servidor.
     * 
     */ 
    public boolean camareroEstaConectado(){
        return conectado;
    }
    
    /**
     *  Recibe solicitud del servidor de nuevo idConexion.
     * 
     */
    private boolean solicitudServidorNuevoIdConexion(
            List<String>resultados) throws IOException {
        idConexion = resultados.get(0);
        
        if (idConexion == null) {
            return false;
        }
    
        conectado = true; 
    
        observadores.firePropertyChange(PROPIEDAD_CONECTADO,
            null, conectado);      
    
        return true;
    }
    
    /**
     *  Recibe del servidor el estado de la mesa.
     * 
     */
    private boolean solicitudServidorOcuparMesa(
            String propiedad, List<String> resultados) 
            throws IOException {
        String codigoMesa = resultados.get(0);            
        if (codigoMesa == null) {
            return false;
        }
        observadores.firePropertyChange(propiedad, null, 
                Integer.parseInt(codigoMesa));  
        return true;
    }
  
    /**
     *  Recibe del servidor el resultado de añadir o eliminar un plato a una mesa.
     * 
     */
    private boolean solicitudServidorAnadirEliminarPlato(
            String propiedad, List<String> resultados)
            throws IOException {
        int codigoMesa = Integer.parseInt(resultados.get(0));
        int codigoPlato = Integer.parseInt(resultados.get(1));
        
        if (codigoMesa < 0 || codigoPlato < 0) {
            return false;
        }
        observadores.firePropertyChange(propiedad, null, 
                new Tupla <Integer, Integer>(codigoMesa, codigoPlato));  
        return true;
    }
    
    /**
     *  Recibe del servidor el resultado de generar una factura.
     * 
     */
    private boolean solicitudServidorGenerarFactura(
            String propiedad, List<String> resultados)
            throws IOException {
        int codigoMesa = Integer.parseInt(resultados.get(0));
        
        if (codigoMesa < 0) {
            return false;
        }
        observadores.firePropertyChange(propiedad, null, codigoMesa);  
        return true;
    }

    /**
     *  Recibe solicitudes servidor restaurante en línea.
     *
     */  
    @Override
    public boolean solicitudServidorProducida(
            PrimitivaComunicacion solicitud, 
            List<String> resultados) throws IOException {
        if (resultados.isEmpty()) {
            return false;
        } 
      
        switch(solicitud) {
            case NUEVO_ID_CONEXION:
                return solicitudServidorNuevoIdConexion(resultados);
            
            case OCUPAR_MESA:
                return solicitudServidorOcuparMesa(
                    PROPIEDAD_OCUPAR_MESA, resultados);
                
            case ANADIR_PLATO:
                return solicitudServidorAnadirEliminarPlato(
                    PROPIEDAD_ANADIR_PLATO, resultados);
                
            case ELIMINAR_PLATO:
                return solicitudServidorAnadirEliminarPlato(
                    PROPIEDAD_ELIMINAR_PLATO, resultados);
                
            case FACTURAR_MESA:
                return solicitudServidorGenerarFactura(
                    PROPIEDAD_GENERAR_FACTURA, resultados);
            default:
                return false;
        }     
    }
}
