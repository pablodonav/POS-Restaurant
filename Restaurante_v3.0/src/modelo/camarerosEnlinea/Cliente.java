/**
 * Cliente.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package modelo.camarerosEnlinea;

import control.Restaurante;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import vista.DebugVista;

/**
 * Cliente comunicación con servidor de camareros en línea.
 * 
 */
public class Cliente {
    //Tiempos en ms
    public static int TIEMPO_ESPERA_LARGA_ENCUESTA = 0;       
    public static int TIEMPO_ESPERA_SERVIDOR = 1000;      
    public static int TIEMPO_REINTENTO_CONEXION_SERVIDOR = 
        10 * 1000; 
  
    private String URLServidor;
    private int puertoServidor;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private static String DEBUG_ENVIANDO_SOLICITUD = 
        "Enviando solicitud:";
    private static String DEBUG_DATOS_RECIBIDOS = 
        "Datos recibidos:";
  
  
    /**
     *  Construye cliente.
     *  
     */   
    public Cliente(String URLServidor, int puertoServidor) {
        this.URLServidor = URLServidor;
        this.puertoServidor = puertoServidor;
    }

    /**
     *  Envía solicitud al servidor.
     * 
     */ 
    private synchronized void enviar(
            PrimitivaComunicacion solicitud, 
            int tiempoEspera, 
            String parametros) throws IOException {
      
        socket = new Socket(URLServidor, puertoServidor);
        socket.setSoTimeout(tiempoEspera);

        entrada = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                    socket.getOutputStream())), true);  

        if (Restaurante.esModoDebug()) {
            DebugVista.devolverInstancia().mostrar(
                DEBUG_ENVIANDO_SOLICITUD + " " + 
                solicitud + " " + parametros);      
        }

        salida.println(solicitud.toString());

        if (parametros != null) {
            salida.println(parametros);
        }
    }
  
    /**
     *  Recibe respuesta servidor.
     * 
     */
    private void recibirRespuestaServidor(List<String> resultados) 
            throws Exception {          
        String linea = "";
    
        while ((linea = entrada.readLine()) != null) {
            resultados.add(linea);   
        }    
    }
  
    /**
     *  Recibe solicitud servidor.
     * 
     */  
    private void recibirSolicitudServidor(List<String> resultados) 
            throws Exception {      
    
        String resultado = entrada.readLine();
    
        while (! resultado.equals(
                PrimitivaComunicacion.FIN.toString())) {        
            resultados.add(resultado);        
            resultado = entrada.readLine();
        }        
    }  
  
    /**
     *  Recibe respuesta o solicitud del servidor.
     * 
     */  
    private synchronized PrimitivaComunicacion recibir(
            List<String> resultados, boolean solicitudServidor) 
            throws Exception {   
            
        // Esperamos solicitud servidor
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.nueva(
            new Scanner(new StringReader(entrada.readLine())));                

        if (Restaurante.esModoDebug()) {
            DebugVista.devolverInstancia().mostrar(
                DEBUG_DATOS_RECIBIDOS + " " + respuesta.toString());      
        }

        // Servidor envía test conexión
        if (respuesta == PrimitivaComunicacion.TEST) {
            entrada.readLine();                     // Salta FIN
            salida.println(PrimitivaComunicacion.OK);
            return respuesta;
        }

        // En el resto de casos recibimos, si los hay, 
        // resultados en líneas sig.    
        resultados.clear();

        if ( ! solicitudServidor) {
            recibirRespuestaServidor(resultados);
        }
        else {
            recibirSolicitudServidor(resultados);    
        }

        if (Restaurante.esModoDebug()) {
            DebugVista.devolverInstancia().mostrar(
                resultados.toString());      
        }     

        salida.println(PrimitivaComunicacion.OK);      

        return respuesta;    
    }
  
    /**
     *  Recibe respuesta del servidor.
     * 
     */   
    private synchronized PrimitivaComunicacion recibir(
            List<String> resultados) throws Exception {   
        return recibir(resultados, false);
    }  
  
    /**
     *  Envía una solicitud al servidor devolviendo con resultados.
     *     
     */ 
    public synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud, 
            int tiempoEspera, 
            String parametros, List<String> resultados) 
            throws Exception {
          
        enviar(solicitud, tiempoEspera, parametros);    
    
        PrimitivaComunicacion respuesta = recibir(resultados);

        entrada.close();
        salida.close();
        socket.close();
    
        return respuesta;
    }
 
    /**
     *  Envía una solicitud al servidor sin devolver resultados.
     * 
     */ 
    public synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud, 
            int tiempoEspera, 
            String parametros) throws Exception {  
      
        return enviarSolicitud(solicitud, tiempoEspera, 
                parametros,  new ArrayList<String>());
    }
  
    /**
     *  Envía una solicitud long polling al servidor.
     * 
     */ 
    public synchronized void enviarSolicitudLongPolling(
            PrimitivaComunicacion solicitud, 
            int tiempoEspera, 
            String parametros,
            OyenteServidor oyenteServidor) throws Exception {
                 
        enviar(solicitud, tiempoEspera, parametros);
   
        List<String> resultados = new ArrayList<>();
    
        do { 
            PrimitivaComunicacion respuesta = recibir(
                resultados, true);
      
            if (respuesta != PrimitivaComunicacion.TEST) { 
                if (oyenteServidor.solicitudServidorProducida(
                    respuesta, resultados)) {
                    salida.println(PrimitivaComunicacion.OK);
                } else {
                    salida.println(PrimitivaComunicacion.NOK);  
                }
            }        
        } while(true);
    }
}
