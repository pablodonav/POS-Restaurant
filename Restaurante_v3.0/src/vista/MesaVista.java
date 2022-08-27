/**
 * MesaVista.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class MesaVista extends JLabel {
    private RestauranteVista vista;

    private static final int INC_FUENTE_MESA_OCUPADA = 8;   
    private Color colorNoSeleccionado;
    private boolean seleccionado = false;
    private static final Color COLOR_COMANDA = Color.ORANGE;
    private boolean comanda = false;
    private boolean ocupada = false;
    private int codigoMesa;
    private Font fuente;
    private Map atributos;
    private int tamanoNormal;
    
    private static final int CODIGO_INCORRECTO = -1;
    
    /**
     * Construye la vista de la mesa.
     * 
     */
    MesaVista(RestauranteVista vista, boolean recibeEventosRaton) {
        this.vista = vista;   

        fuente = getFont();
        atributos = fuente.getAttributes(); 
        tamanoNormal = fuente.getSize();
        colorNoSeleccionado = this.getBackground();

        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(
            BevelBorder.LOWERED));

        if (recibeEventosRaton) {
            recibirEventosRaton();
        }    
    }
    
    /**
     * Recibe los eventos de ratón.
     * 
     */ 
    private void recibirEventosRaton() {
        addMouseListener(new MouseAdapter() { 
            
            @Override
            public void mousePressed(MouseEvent e) { 
                MesaVista mesaVista = (MesaVista)e.getSource();
        
                if (mesaVista.obtenerCodigo() >= 0) {                  
                    vista.seleccionarMesaVista(mesaVista);
                }
            }
        });
    }
    
    /**
     * Obtiene el codigo de la mesa.
     *
     */
    public int obtenerCodigo() {
        return codigoMesa;
    }
    
    /**
     * Pone el codigo de mesa vista.
     * 
     */
    public void ponerCodigo(int codigo) {
        this.codigoMesa = codigo;
        String texto = String.valueOf(codigo);
        ponerTextoNormal(texto); 
    }

    /**
     * Selecciona mesa vista .
     * 
     */
    public void seleccionar() {
        seleccionado = true;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }  
  
    /**
     * Quita selección mesa vista.
     * 
     */
    public void deseleccionar() {
        seleccionado = false;
        if (! comanda) {
            setBackground(colorNoSeleccionado);
        } else {
            setBackground(COLOR_COMANDA);
        }
        setBorder(BorderFactory.createBevelBorder(
            BevelBorder.LOWERED));
    }  
    
    /**
     * Devuelve verdad si y solo si dicha mesa esta seleccionada.
     * 
     */
    public boolean estaSeleccionado() {
        return seleccionado;    
    }
    
    /**
     * Cambia el formato a mesa con comanda.
     * 
     */
    public void ponerHayPlatosEnComanda() {
        comanda = true;
        setBackground(COLOR_COMANDA);
    }
    
    /**
     * Cambia el formato a mesa sin comanda.
     * 
     */
    public void ponerNoHayPlatosEnComanda() {
        comanda = false;
        setBackground(colorNoSeleccionado);
    }
        
    /**
     * Libera una mesa .
     * 
     */
    public void liberarMesa() {
        ocupada = false;
        comanda = false;
        deseleccionar();
        ponerTextoNormal(String.valueOf(codigoMesa));
    }
    
    /**
     * Ocupa una mesa.
     * 
     */
    public void ocuparMesa() {
        String codigo = String.valueOf(codigoMesa);
        ocupada = true;
        ponerTextoDestacado(codigo);
    }

    /**
     * Inicia mesa vista.
     * 
     */
    public void iniciar() {     
        deseleccionar();
        codigoMesa = CODIGO_INCORRECTO;
    }
    
    /**
     * Devuelve cierto si mesa vista tiene comanda.
     *  
     */
    public boolean hayComandaVista() {
        return comanda;
    }
    
    /**
     * Pone texto con formato normal.
     * 
     */
    private void ponerTextoNormal(String texto) {
        setText(texto);
        atributos.put(TextAttribute.SIZE, tamanoNormal);
        setFont(fuente.deriveFont(atributos));           
    }
    
    /**
     * Pone texto con formato destacado.
     * 
     */
    private void ponerTextoDestacado(String texto) {
        atributos.put(TextAttribute.SIZE, fuente.getSize() + 
                                        INC_FUENTE_MESA_OCUPADA);
        setFont(fuente.deriveFont(atributos));           
    }
    
    /**
     * Devuelve cierto si mesa vista esta ocupada.
     * 
     */
    public boolean estaOcupada() {
        return ocupada;
    }
    
    /**
     * toString.
     * 
     */  
    @Override
    public String toString() {
        return codigoMesa + "";
    }
}
