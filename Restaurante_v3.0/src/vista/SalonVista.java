/**
 * SalonVista.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package vista;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;

import modelo.Mesas;

public class SalonVista extends JPanel {
    private static final int ALTURA_FILA = 100;
    private static final int ANCHURA_COLUMNA = 60;
    
    private MesaVista[][] mesaVista;
    private RestauranteVista vista;
    private Mesas mesas;
    
    public static final boolean RECIBE_EVENTOS_RATON = true;
    public static final boolean NO_RECIBE_EVENTOS_RATON = false;
    
    private static final int CODIGO_INCORRECTO = -1;
    
    /**
     * Construye la vista del salon.
     * 
     */
    SalonVista(RestauranteVista vista, 
            Mesas mesas, boolean recibeEventosRaton) {
        this.vista = vista;
        this.mesas = mesas;
        
        crearCasillas(recibeEventosRaton);
        this.setPreferredSize(new Dimension(
            vista.EJE_Y_SALON * ALTURA_FILA, 
            vista.EJE_X_SALON * ANCHURA_COLUMNA));
    }
    
    /**
     * Crea casillas .
     * 
     */  
    private void crearCasillas(boolean recibeEventosRaton) {
        int eje_x = vista.EJE_X_SALON;
        int eje_y = vista.EJE_Y_SALON;

        setLayout(new GridLayout(eje_x, eje_y));
        mesaVista = new MesaVista[eje_x][eje_y];

        for(int x = 0; x < eje_y; x++) 
            for(int y = 0; y < eje_x; y++) {
                mesaVista[x][y] = new MesaVista(
                    vista, recibeEventosRaton);         
                add(mesaVista[x][y]);            
          }      
    }
    
    /**
     * Inicia vista del salon.
     * 
     */   
    private void iniciarSalonVista() {
        for (int y = 0; y < vista.EJE_Y_SALON; y++) {
            for (int x = 0; x < vista.EJE_X_SALON; x++) {  
                mesaVista[y][x].iniciar();
            }
        }  
    }
    
    /**
     * Dado el codigo de la mesa, devuelve la mesa vista.
     * 
     */
    private MesaVista buscarMesaVista(int codigoMesa) {
        for (int y = 0; y < vista.EJE_Y_SALON; y++) {
            for (int x = 0; x < vista.EJE_X_SALON; x++) { 
                int codigo = mesaVista[y][x].obtenerCodigo();
                if((codigo == codigoMesa) && (codigo >= 0)) {
                    return mesaVista[y][x];
                }
            }
        }
        return null;
    }
    
    /**
     * Ocupa mesa vista.
     * 
     */
    public void ocuparMesa(int codigoMesa) {
        MesaVista mesaVista = buscarMesaVista(codigoMesa);
        if((mesaVista != null) && ( ! mesaVista.estaOcupada())) {
            mesaVista.ocuparMesa();
        }
    }
    
    /**
     * Poner el formato de mesa con comanda.
     * 
     */
    public void ponerHayPlatosEnComanda(int codigoMesa){
        MesaVista mesaVista = buscarMesaVista(codigoMesa);
        if(mesaVista != null) {
            mesaVista.ponerHayPlatosEnComanda();
        }
    }
    
    /**
     * Poner el formato de mesa sin comanda.
     * 
     */
    public void ponerNoHayPlatosEnComanda(int codigoMesa){
        MesaVista mesaVista = buscarMesaVista(codigoMesa);
        if(mesaVista != null) {
            mesaVista.ponerNoHayPlatosEnComanda();
        }
    }
    
    /**
     * Generar factura de mesa vista.
     * 
     */
    public void generarFacturaMesa(int codigoMesa) {
        MesaVista mesaVista = buscarMesaVista(codigoMesa);
        if(mesaVista != null) {
            mesaVista.liberarMesa();
        }
    }
    
    /**
     * Pone mesas de salon vista.
     * 
     */       
    public void ponerMesas() throws Exception { 
        int codigoMesa = 0;
                
        iniciarSalonVista();
        
        for (int y = 0; y < vista.EJE_Y_SALON; y++) {
            for (int x = 0; x < vista.EJE_X_SALON; x++) { 
        
                //salta huecos donde no haya mesas
                codigoMesa = mesas.buscarMesa(x, y);
                if (codigoMesa == CODIGO_INCORRECTO) {
                    continue;
                }
                
                mesaVista[y][x].ponerCodigo(codigoMesa);
                if(mesas.mesaEstaOcupada(codigoMesa)){
                    mesaVista[y][x].ocuparMesa();
                }
                if(mesas.obtenerComandaMesa(codigoMesa) != null){
                    mesaVista[y][x].ponerHayPlatosEnComanda();
                }
            }
        }
    }
}
