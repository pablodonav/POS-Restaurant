/**
 * ServidorRestaurante.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Servidor de Restaurante.
 *   
 */
public class ServidorRestaurante implements Runnable{
    public static String ERROR_CONEXION_RESTAURANTE = 
        "Closed restaurant connection"; 
  private static String FORMATO_FECHA_CONEXION = 
        "kk:mm:ss EEE d MMM yy";
    
  private ServidorCamareros servidorCamareros;
  private Socket socket;
  private BufferedReader entrada; 
  private PrintWriter salida;
  
  private static String DEBUG_SOLICITUD = "Request:";
  private static String DEBUG_ERROR_SOLICITUD = 
          DEBUG_SOLICITUD + " wrong from ";
  
    /**
     *  Construye el servidor de resturante.
     * 
     */
    ServidorRestaurante(ServidorCamareros servidorCamareros,
            Socket socket) throws IOException {
        this.servidorCamareros = servidorCamareros;
        this.socket = socket;  

        entrada = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(
                socket.getOutputStream())), true);  
    } 
    
    /**
     *  Cierra conexión.
     * 
     */
    private void cerrarConexion() throws IOException {
        entrada.close();
        salida.close();
        socket.close();      
    }
  
  
    /**
     *  Atiende solicitudes de restaurante.
     * 
     */  
    @Override
    public void run() {
        try {        
            PrimitivaComunicacion solicitud = 
                PrimitivaComunicacion.nueva(
                    new Scanner(new StringReader(
                        entrada.readLine())));

            switch(solicitud) {
                case CONECTAR_PUSH:
                    conectarPushRestaurante();
                    break;

                case DESCONECTAR_PUSH:
                    desconectarPushRestaurante();
                    break;  
                    
                case OBTENER_CARTA:
                    obtenerCarta();
                    break;
                    
                case OBTENER_COMANDA_MESA:
                    obtenerComandaMesa();
                    break;
                 
                case OBTENER_PLATO:
                    obtenerInfoPlato();
                    break;
                    
                case OBTENER_NUM_PLATOS:
                    obtenerNumPlatos();
                    break;
                    
                case OBTENER_DISPONIBILIDAD_MESA:
                    mesaEstaDisponible();
                    break;
                
                case OCUPAR_MESA: 
                    ocuparMesa();
                    break;
                    
                case OBTENER_ID_MESA:
                    obtenerIDMesa();
                    break;
                    
                case ANADIR_PLATO:
                    anadirPlato();
                    break;
                    
                case ELIMINAR_PLATO:
                    eliminarPlato();
                    break;
                    
                case FACTURAR_MESA:
                    generarFactura();
                break;
            }  
        } catch (IOException e) {
            System.out.println(ERROR_CONEXION_RESTAURANTE +
                ": " + e.toString());

            if (ServidorCamareros.esModoDebug()) {
                e.printStackTrace();
            }         
        } catch (InterruptedException e) {
            if (ServidorCamareros.esModoDebug()) {
                e.printStackTrace();
            }
        } catch (Exception e){
            if (ServidorCamareros.esModoDebug()) {
                e.printStackTrace();
            }   
        }
    }
    
    /**
     * Muestra el identificador de conexión de cada camarero conectado.
     * 
     */
    private void mostrarIdConexion(
            PrimitivaComunicacion primitivaComunicacion, 
            String identificador)throws IOException{        
        if(ServidorCamareros.esModoDebug()){
            System.out.println(DEBUG_SOLICITUD + " " + 
                primitivaComunicacion.toString() + " " + 
                identificador + " " + obtenerFechaHoy());
        } 
    }
    
    /**
     *  Conecta restaurante push.
     * 
     */    
    private void conectarPushRestaurante() throws IOException,
        InterruptedException { 
        
        CountDownLatch cierreConexion = new CountDownLatch(1);
        String lenguaje = entrada.readLine();
        String pais = entrada.readLine();
        String idConexion = 
            servidorCamareros.generarIdConexionPushRestaurante();
        
        mostrarIdConexion(PrimitivaComunicacion.CONECTAR_PUSH, 
            idConexion);
        ConexionPushRestaurante conexionPushRestaurante = 
           new ConexionPushRestaurante(idConexion, socket, 
                cierreConexion); 
        
        conexionPushRestaurante.aniadirLocalizacionCamarero(lenguaje, pais);

        PrimitivaComunicacion respuesta = 
            conexionPushRestaurante.enviarSolicitud(
                PrimitivaComunicacion.NUEVO_ID_CONEXION,
                servidorCamareros.TIEMPO_ESPERA_CLIENTE,
                idConexion);

        if (respuesta.equals(PrimitivaComunicacion.OK)) {    
            servidorCamareros.nuevaConexionPushRestaurante(
                conexionPushRestaurante);

            // Esperamos hasta cierre conexión push agenda
            cierreConexion.await();
        } else {
            conexionPushRestaurante.cerrar();
        }
    }  
  
    /**
     *  Obtiene fecha de hoy como string.
     * 
     */      
    private String obtenerFechaHoy() {
        return new SimpleDateFormat(FORMATO_FECHA_CONEXION, 
                    Locale.getDefault()).format(new Date());
    }
    
    /**
     *  Desconecta restaurante push.
     * 
     */    
    private void desconectarPushRestaurante() throws IOException {
        String idConexion = entrada.readLine();                     

        if (ServidorCamareros.esModoDebug()) {    
            ConexionPushRestaurante conexionPushRestaurante = 
               servidorCamareros.obtenerConexionPushRestaurante(
                    idConexion);

            if (conexionPushRestaurante != null) {
                System.out.println(DEBUG_SOLICITUD + " " +
                PrimitivaComunicacion.DESCONECTAR_PUSH + " " +
                conexionPushRestaurante.toString() + " " + 
                obtenerFechaHoy());
          }         
        }

        if (servidorCamareros.eliminarConexionPushRestaurante(
                idConexion)) {
            salida.println(PrimitivaComunicacion.OK);
        } else {
            salida.println(PrimitivaComunicacion.NOK);
        }
        cerrarConexion();
    }  
    
    /**
     *  Recibe el código de mesa de cliente.
     * 
     */
    private String leerCodigoMesa() throws IOException {
        String codigoMesa = entrada.readLine();
    
        if (codigoMesa == null) {
            if (ServidorCamareros.esModoDebug()) {
                System.out.println(DEBUG_ERROR_SOLICITUD + " " + 
                            socket.getInetAddress() + " " + 
                            obtenerFechaHoy());
            }
        }
        
        return codigoMesa;
    }
  
    /**
     *  Obtiene la carta del restaurante.
     * 
     */
    private void obtenerCarta() throws IOException, Exception {
        salida.println(PrimitivaComunicacion.OBTENER_CARTA);  
        
        String carta = servidorCamareros.obtenerCarta();
      
        if (carta != null) {
            salida.println(carta); 
        } 
        else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }
        
        cerrarConexion();    
    }
    
    /**
     *  Obtiene la comanda de una mesa.
     * 
     */
    private void obtenerComandaMesa() throws IOException {
        String codigoMesa = entrada.readLine();
        
        if(codigoMesa != null){
            salida.println(
                PrimitivaComunicacion.OBTENER_COMANDA_MESA);  
            String comanda = servidorCamareros.obtenerComandaMesa(
                Integer.parseInt(codigoMesa));
            if(comanda != null){
                if(comanda.length() > 0){
                     comanda = comanda.substring(
                        0, comanda.length() - 1);
                }
                salida.println(comanda); 
                }
        }
        else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }
        
        cerrarConexion();  
    }
    
    /**
     *  Obtiene la información completa de un plato.
     * 
     */
    private void obtenerInfoPlato() throws IOException {
        String codigoPlato = entrada.readLine();
        
        if(codigoPlato != null){
            salida.println(
                PrimitivaComunicacion.OBTENER_PLATO);  
            
            String plato = servidorCamareros.obtenerInfoPlato(
                Integer.parseInt(codigoPlato));
            
            if(plato != null){
                salida.println(plato);
            }
        }
        else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }
        
        cerrarConexion();
    } 
    
    /**
     *  Obtiene el número de platos de la comanda de una mesa.
     * 
     */
    private void obtenerNumPlatos() throws IOException {
        String codigoMesa = entrada.readLine();
        
        if(codigoMesa != null){
            salida.println(
                PrimitivaComunicacion.OBTENER_NUM_PLATOS);  
            
            Integer numPlatos = servidorCamareros.
                    obtenerNumPlatos(Integer.parseInt(codigoMesa));
            
            if(numPlatos != null){
                salida.println(String.valueOf(numPlatos));
            }
        }
        else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }
        
        cerrarConexion();
    } 
    
    /**
     *  Devuelve si una mesa está disponible.
     * 
     */
    private void mesaEstaDisponible()throws IOException{
        String codigoMesa = entrada.readLine();
        
        if(codigoMesa != null){
            salida.println(
                PrimitivaComunicacion.OBTENER_DISPONIBILIDAD_MESA);  
            
            boolean mesaDisponible = 
                servidorCamareros.mesaEstaDisponible(
                    Integer.valueOf(codigoMesa));
            
            salida.println(String.valueOf(mesaDisponible)); 
        }
        else {
            salida.println(PrimitivaComunicacion.NOK.toString());
        }
        
        cerrarConexion();  
    }
    
    /**
     *  Devuelve el identificador de una mesa.
     * 
     */
    private void obtenerIDMesa() throws IOException {
        Scanner scanner = new Scanner(entrada.readLine());
        int posX = scanner.nextInt();
        int posY = scanner.nextInt();
        
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK; 
        
        if (posX < 0 || posY < 0) {
            if (ServidorCamareros.esModoDebug()) {
                System.out.println(DEBUG_ERROR_SOLICITUD + 
                    " " + socket.getInetAddress() + " " + 
                    obtenerFechaHoy());
            }
            salida.println(PrimitivaComunicacion.NOK.toString());
        } else {
            salida.println(PrimitivaComunicacion.OBTENER_ID_MESA);
            
            int codigoMesa = servidorCamareros.obtenerIDMesa(
                posX, posY);
            
            if (codigoMesa  >= 0) {
                salida.println(String.valueOf(codigoMesa));
            } 
        }
        cerrarConexion();   
    }
    
    /**
     *  Ocupa una mesa.
     * 
     */  
    private void ocuparMesa() throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK; 

        String codigoMesa = leerCodigoMesa();
        if (codigoMesa != null) {
            if (servidorCamareros.ocuparMesa(codigoMesa)) { 
                respuesta = PrimitivaComunicacion.OK;       
            }
        } 
        salida.println(respuesta);

        cerrarConexion();     
    }   
    
    /**
     *  Añade un plato a la comanda de una mesa.
     * 
     */
    private void anadirPlato() throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;
        Integer codigoMesa = Integer.parseInt(entrada.readLine());
        Integer codigoPlato = Integer.parseInt(entrada.readLine());
        
        if (codigoMesa < 0 || codigoPlato < 0) {
            if (ServidorCamareros.esModoDebug()) {
                System.out.println(DEBUG_ERROR_SOLICITUD + " " + 
                           socket.getInetAddress() + " " + 
                           obtenerFechaHoy());
            }
        } else {
            if (servidorCamareros.anadirPlato(codigoMesa, 
                    codigoPlato)) {
                respuesta = PrimitivaComunicacion.OK;
            }
        }
        
        salida.println(respuesta);
        
        cerrarConexion(); 
    }
    
    /**
     *  Eliminar un plato a la comanda de una mesa.
     * 
     */
    private void eliminarPlato() throws IOException {
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;
        Integer codigoMesa = Integer.parseInt(entrada.readLine());
        Integer codigoPlato = Integer.parseInt(entrada.readLine());
        
        if (codigoMesa < 0 || codigoPlato < 0) {
            if (ServidorCamareros.esModoDebug()) {
                System.out.println(DEBUG_ERROR_SOLICITUD + " " + 
                           socket.getInetAddress() + " " + 
                           obtenerFechaHoy());
            }
        } else {
            if (servidorCamareros.eliminarPlato(codigoMesa, 
                    codigoPlato)) {
                respuesta = PrimitivaComunicacion.OK;
            }
        }
        
        salida.println(respuesta);
        
        cerrarConexion(); 
    }
    
    /**
     *  Genera una factura.
     * 
     */  
    private void generarFactura() throws Exception {
        String idConexion = servidorCamareros.obtenerIdConexion();
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK; 

        String codigoMesa = leerCodigoMesa();
        if (codigoMesa != null) {
            if (servidorCamareros.generarFactura(
                    Integer.parseInt(codigoMesa))) { 
                respuesta = PrimitivaComunicacion.OK;       
            }
        } 
        salida.println(respuesta);

        cerrarConexion();     
    }   
   
}
