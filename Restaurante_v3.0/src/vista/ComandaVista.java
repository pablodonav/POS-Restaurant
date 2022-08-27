/**
 * ComandaVista.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package vista;

import control.OyenteVista;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelo.Carta;
import modelo.Mesas;
import modelo.Tupla;

public class ComandaVista extends JPanel 
        implements ListSelectionListener {
    private static ComandaVista instancia = null; // es singleton
    private RestauranteVista vista;
    private Localizacion local;
    private JLabel labelComandas;
    private JList platosComanda;
    private OyenteVista oyenteVista;
    private DefaultListModel modelo;
    
    private static final int EJE_X_COMANDAS = 300;
    private static final int EJE_Y_COMANDAS = 300;
    
    private static final int DIMENSION_VENTANA_PLATOS = 25;
    
    /**
     * Crea la vista de la Comanda.
     * 
     */
    ComandaVista(RestauranteVista vista, Localizacion local, 
            OyenteVista oyenteVista) {
        modelo = new DefaultListModel();
        this.vista = vista;   
        this.local = local;  
        this.oyenteVista = oyenteVista;
        this.setLayout(new BorderLayout());
        iniciarComandaVista();
        platosComanda.setModel(modelo);
    }
     
    /**
     * Devuelve la instancia de ComandaVista.
     * 
     */        
    public static synchronized ComandaVista devolverInstancia(
            RestauranteVista vista, Localizacion local, 
            OyenteVista oyenteVista) { 
        if (instancia == null) {
            instancia = new ComandaVista(vista, local, 
                oyenteVista);
        }
        return instancia;
    } 
    
    /**
     * Crea vista del contenido de la comanda.
     * 
     */   
    public void iniciarComandaVista() {
        labelComandas = new JLabel(local.devuelve(
            local.ETIQUETA_COMANDA));
        this.platosComanda = new JList();
        platosComanda.setPreferredSize(new Dimension(
                EJE_X_COMANDAS, EJE_Y_COMANDAS));
        platosComanda.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        
        platosComanda.addListSelectionListener(this);
        
        add(labelComandas, BorderLayout.NORTH);
        add(platosComanda, BorderLayout.CENTER);
    }
    
    /**
     * Crea ventana de platos y devuelve la lista generada.
     * 
     */
    private JList listarPlatos(Carta carta) throws Exception {
        Object[] listaPlatos = carta.obtenerCarta();
        
        JList lista = new JList(listaPlatos) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension dim = 
                    super.getPreferredScrollableViewportSize();
                dim.width = DIMENSION_VENTANA_PLATOS;
                return dim;
            }
        };
        
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        switch (JOptionPane.showOptionDialog(
                null, new JScrollPane(lista), 
                local.devuelve(local.TITULO_VENTANA_PLATOS), 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, null, null, 0)) {
            case JOptionPane.OK_OPTION:
                return lista;
        }
        return null;
    }
    
    /**
     * Notifica a control los cambios en la comanda.
     *  
     */
    public void notificarCambiosComanda(
            OyenteVista.Evento evento, Carta carta, 
            Mesas mesas) throws Exception {
        String[] codigos = null;
        int codigoPlato = 0;
        JList lista = null;
        Object plato = null;
        
        if(evento.name().equals(
            OyenteVista.Evento.ANADIR_PLATO.name())) {
            lista = listarPlatos(carta);
        } else {
            lista = platosComanda;
        }
        
        if(lista != null) {
            plato =  lista.getSelectedValue();
            if(plato != null) {
                codigos = plato.toString().split(" ");
                codigoPlato = Integer.parseInt(codigos[0]);
                oyenteVista.eventoProducido(
                    evento, new Tupla <Integer, Integer>
                        (vista.obtenerMesaVistaSeleccionada().
                            obtenerCodigo(), 
                            codigoPlato), null);
            }
        }
    }
    
    /**
     * Pone plato en una comanda.
     * 
     */
    public void insertarPlatoComanda(String plato) {
        if(plato != null) {
            modelo.addElement(plato);
        }
    }
    
    /**
     * Elimina plato de una comanda.
     * 
     */
    public void eliminarPlatoComanda(String plato) {
        if(plato != null) {
            modelo.removeElement(plato);
        }
    }
    
    /**
     * Pone platos de una comanda.
     * 
     */
    public void ponerComanda(Object[] comanda) {
        modelo.clear();
        if(comanda != null) {
            for(int i = 0; i < comanda.length; i++) {
                modelo.addElement(comanda[i]);
            }
        }
    }
   
    /**
     * Pone comanda en blanco.
     *
     */
    public void ponerTextoVacio() {
        modelo.clear();
    }
    
    /**
     * Pone en comanda el identificador de la mesa seleccionada.
     * 
     */
    public void ponerEtiquetaComanda(int codigoMesaVista) {
        labelComandas.setText(local.devuelve(
                local.ETIQUETA_COMANDA) + 
                codigoMesaVista + ":");
    }
    
    /**
     * Borra en comanda el identificador de la mesa 
     * que estaba seleccionada.
     * 
     */
    public void ponerEtiquetaComanda() {
        labelComandas.setText(local.devuelve(
            local.ETIQUETA_COMANDA));
    }

    /**
     * Sobreescribe valueChanged de ListSelectionListener.
     * 
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList)e.getSource();
        if( ! list.isSelectionEmpty()) {
            vista.activarBotonEliminarPlato(true);
        }
    }
    
}
