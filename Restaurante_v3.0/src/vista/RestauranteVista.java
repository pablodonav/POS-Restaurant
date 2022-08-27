/**
 * RestauranteVista.java
 * Pablo Doñate y Adnana Dragut (04/2021). 
 *   
 */
package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import modelo.Mesas;
import control.OyenteVista;
import control.Restaurante;
import javax.swing.JLabel;
import modelo.Carta;
import modelo.Tupla;

public class RestauranteVista implements ActionListener, 
        PropertyChangeListener {
    private static RestauranteVista instancia = null; // es singleton
    private OyenteVista oyenteVista;
    private ComandaVista comandaVista;
    private Mesas mesas;
    private MesaVista mesaVistaSeleccionada;
    private String lenguaje;
    private String pais;
    private String version;
    private JFrame ventana;
    private Carta carta;
    private SalonVista salonVista;
    private JButton botonOcuparMesa;
    private JButton botonGenerarFactura;
    private JButton botonAnadirPlato;
    private JButton botonEliminarPlato;
    private JButton botonIdioma;
    private JButton botonSalir;
    private JLabel etiquetaConectado;
    private boolean conectado;
    
    private Localizacion local;
    
    private static final int OPCION_SI = JOptionPane.YES_OPTION;
    public static final String RUTA_RECURSOS = "/vista/recursos/";
    
    public static final int EJE_X_SALON = 5;
    public static final int EJE_Y_SALON = 5;
    
    private static final int DIMENSION_VENTANA_IDIOMAS = 25;
    private static final int UN_SOLO_PLATO = 1;
    private static final int NO_HAY_PLATOS = 0;
    
    /**
     * Construye la vista del restaurante. 
     * 
     */
    private RestauranteVista(OyenteVista oyenteVista, 
            Mesas mesas, String lenguaje, 
            String pais, String version, Carta carta) {
        this.oyenteVista = oyenteVista;
        this.mesas = mesas;
        this.lenguaje = lenguaje;
        this.pais = pais;
        this.version = version;
        this.carta = carta;
        
        local = Localizacion.devolverInstancia(lenguaje, pais);
        
        crearVentana();
    }
    
    /**
     * Devuelve la instancia de la vista del restaurante.
     * 
     */        
    public static synchronized RestauranteVista 
        instancia(OyenteVista oyenteIU, Mesas mesas, 
                String lenguaje, String pais, 
                String version, Carta carta) {
        if (instancia == null) {
            instancia = new RestauranteVista(
                    oyenteIU, mesas, lenguaje, pais, version, carta);    
        }
        return instancia;
    }
    
    /**
     * Crea la ventana de la vista.
     * 
     */ 
    private void crearVentana() {
        ventana = new JFrame(local.devuelve(local.TITULO) + 
            Restaurante.VERSION);
        
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                oyenteVista.eventoProducido(
                    OyenteVista.Evento.SALIR, null, null);
            }
        });
        
        ventana.getContentPane().setLayout(new BorderLayout());
        
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new GridLayout(2, 1));
        
        crearBarraHerramientas(panelNorte);
        
        ventana.getContentPane().add(panelNorte, BorderLayout.NORTH);

        JPanel panelSalon = new JPanel();
        panelSalon.setLayout(new FlowLayout());   
        salonVista = new SalonVista(this, mesas, 
            SalonVista.RECIBE_EVENTOS_RATON);
        panelSalon.add(salonVista);
        ventana.getContentPane().add(panelSalon, BorderLayout.CENTER);

        JPanel panelComandas = new JPanel();
        panelComandas.setLayout(new BorderLayout());
        comandaVista = new ComandaVista(this, local, oyenteVista);
        panelComandas.add(comandaVista);
        ventana.getContentPane().add(comandaVista, BorderLayout.EAST);

        ventana.setResizable(false);    

        ventana.pack();  // ajusta ventana y sus componentes
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);  // centra en la pantalla
    }
        
    /**
     * Crea botón barra de herramientas.
     * 
     */ 
    private JButton crearBotonBarraHerramientas(String etiqueta) {
        JButton boton = new JButton(local.devuelve(etiqueta));
        boton.addActionListener(this);
        boton.setActionCommand(etiqueta);
    
        return boton;
    } 
    
    /**
     * Crea barra de herramientas.
     * 
     */ 
    private void crearBarraHerramientas(JPanel panelNorte) {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        botonOcuparMesa = crearBotonBarraHerramientas(
            local.OCUPAR_MESA);
        barra.add(botonOcuparMesa);
        botonOcuparMesa.setEnabled(false);

        botonGenerarFactura = crearBotonBarraHerramientas(
            local.GENERAR_FACTURA);
        barra.add(botonGenerarFactura);
        botonGenerarFactura.setEnabled(false);    

        barra.add(new JToolBar.Separator());

        botonAnadirPlato = crearBotonBarraHerramientas(
            local.COMANDA_ANADIR_PLATO);
        barra.add(botonAnadirPlato);
        botonAnadirPlato.setEnabled(false);

        botonEliminarPlato = crearBotonBarraHerramientas
            (local.COMANDA_ELIMINAR_PLATO);
        barra.add(botonEliminarPlato);
        botonEliminarPlato.setEnabled(false); 
        
        barra.add(new JToolBar.Separator());
        
        barra.add(new JToolBar.Separator());
        etiquetaConectado = new JLabel(
            local.devuelve(local.ESTADO_CONECTADO));
        etiquetaConectado.setEnabled(false);
        barra.add(etiquetaConectado);
        
        botonIdioma = crearBotonBarraHerramientas(
            local.MENU_ITEM_LENGUAJE);
        barra.add(botonIdioma);
        botonIdioma.setEnabled(true);
        
        barra.add(new JToolBar.Separator());
        
        botonSalir = crearBotonBarraHerramientas(
            local.SALIR);
        barra.add(botonSalir);
        botonSalir.setEnabled(true);
        
        if(Restaurante.esModoDebug()) {
            barra.add(new JToolBar.Separator());
            JButton botonDebug = crearBotonBarraHerramientas(
                local.DEBUG);
            barra.add(botonDebug);
        }
        
        panelNorte.add(barra);
    }
    
    
    /**
     * Muestra la lista de idiomas .
     * 
     */   
    private void listarIdiomas() throws Exception {
        String[] idiomas = local.obtenerIdiomas();
        String idiomaSelec = "";
        
        JList lista = new JList(idiomas) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension dim = 
                    super.getPreferredScrollableViewportSize();
                dim.width = DIMENSION_VENTANA_IDIOMAS;
                return dim;
            }
        };
        
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        switch (JOptionPane.showOptionDialog(
                null, new JScrollPane(lista), 
                local.devuelve(local.TITULO_VENTANA_IDIOMAS), 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, null, null, 0)) {
            case JOptionPane.OK_OPTION:
                idiomaSelec = (String) lista.getSelectedValue();
                if(idiomaSelec != null) {
                    cambiarLenguaje(idiomaSelec);
                };
        }
    }   
    
    /**
     * Cambia al lenguaje indicado.
     * 
     */   
    private void cambiarLenguaje(String idioma) {
        String[] s = idioma.split(" ");
        String nuevoLenguaje = s[1];
        String nuevoPais = s[2];
        
        // si opción es distinta de la actual preguntamos cambiar  
        if (! lenguaje.equals(nuevoLenguaje)) { 
            // si cambiamos modificamos configuración y salimos  
            if (mensajeConfirmacion(local.devuelve(
                    local.CONFIRMACION_LENGUAJE)) == OPCION_SI) {               
                oyenteVista.eventoProducido(
                    OyenteVista.Evento.CAMBIAR_LENGUAJE, 
                        new Tupla(nuevoLenguaje, nuevoPais), null); 
            } 
        } 
    }
    
    /**
     * Sobreescribe actionPerformed de ActionListener.
     * 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case Localizacion.OCUPAR_MESA:   
                oyenteVista.eventoProducido(
                    OyenteVista.Evento.OCUPAR_MESA, 
                    mesaVistaSeleccionada.obtenerCodigo(), null);
                break;

            case Localizacion.GENERAR_FACTURA:
                oyenteVista.eventoProducido(
                    OyenteVista.Evento.GENERAR_FACTURA, 
                    mesaVistaSeleccionada.obtenerCodigo(), local);
                comandaVista.ponerEtiquetaComanda();
                break;
                
            case Localizacion.COMANDA_ANADIR_PLATO:
            {
                try {
                    comandaVista.notificarCambiosComanda(
                        OyenteVista.Evento.ANADIR_PLATO,
                        carta, mesas);
                } catch (Exception ex) {
                    mensajeDialogo(local.devuelve(
                        local.ERROR_ANADIR_PLATO));
                }
            }
                break;           
           

            case Localizacion.COMANDA_ELIMINAR_PLATO:
            {
                try {
                    comandaVista.notificarCambiosComanda(
                        OyenteVista.Evento.ELIMINAR_PLATO,
                        carta, mesas);
                } catch (Exception ex) {
                    mensajeDialogo(local.devuelve(
                        local.ERROR_ELIMINAR_PLATO));
                }
            }
                break;           
           
            case Localizacion.MENU_ITEM_LENGUAJE:
                try {
                    listarIdiomas();
                } 
                catch (Exception ex) {
                    if (Restaurante.esModoDebug()) {
                        DebugVista.devolverInstancia().mostrar
                        (local.devuelve(
                            local.FICHERO_IDIOMAS_WRONG), ex);
                    }
                    else{
                        mensajeDialogo(local.devuelve(
                            local.FICHERO_IDIOMAS_WRONG));
                    }
                }
                break;

            case Localizacion.SALIR:
                oyenteVista.eventoProducido(
                    OyenteVista.Evento.SALIR, null, null);
                break;
            
            case Localizacion.DEBUG:
                DebugVista.devolverInstancia().mostrar();
                break;
        } 
    }
    
    /**
     * Cuadro diálogo de confirmación acción.
     * 
     */    
    private int mensajeConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(ventana, mensaje, 
            local.devuelve(local.TITULO_CONFIRMACION_LENGUAJE),
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE); 
    }  
    
    /**
     * Escribe mensaje con diálogo modal.
     * 
     */    
    public void mensajeDialogo(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, 
            local.devuelve(local.TITULO) + " " + version, 
            JOptionPane.INFORMATION_MESSAGE,  null);    
    } 
    
    /**
     * Inicia salón vista.
     * 
     */  
    private void ponerSalonVista() throws Exception {    
        salonVista.ponerMesas(); 
    }
    
    /**
     * Activa botón añadir plato a la comanda.
     * 
     */   
    private void activarBotonAnadirPlato(boolean activar) {
        botonAnadirPlato.setEnabled(activar);
    }
    /**
     * Activa botón generar factura de una mesa.
     * 
     */   
    private void activarBotonGenerarFactura(boolean activar) {
        botonGenerarFactura.setEnabled(activar);
    }
    
    /**
     * Activa botón ocupar mesa.
     * 
     */   
    private void activarBotonOcuparMesa(boolean activar) {
        botonOcuparMesa.setEnabled(activar);
    }

    /**
     * Activa botón eliminar plato de la comanda.
     * 
     */     
    public void activarBotonEliminarPlato(boolean activar) {
        botonEliminarPlato.setEnabled(activar);
    }
    
    /**
     * Activa los correspondientes botones cuando 
     * una mesa esta ocupada.
     * 
     */
    private void activarBotonesMesaOcupada() {
        activarBotonOcuparMesa(false);
        activarBotonAnadirPlato(true);
    }
    
    /**
     * Activa los correspondientes botones cuando 
     * una mesa no esta ocupada.
     * 
     */
    private void activarBotonesMesaDesocupada() {
        activarBotonOcuparMesa(true);
        activarBotonAnadirPlato(false);
        activarBotonEliminarPlato(false);
        activarBotonGenerarFactura(false);
    }
    
    /**
     * Activa los correspondientes botones cuando 
     * una mesa no tiene comanda.
     * 
     */
    private void activarBotonesNoHayComanda() {
        activarBotonEliminarPlato(false); 
        activarBotonGenerarFactura(false);
    }
    
    /**
     * Activa los correspondientes botones cuando 
     * una mesa tiene comanda.
     * 
     */
    private void activarBotonesHayComanda() {
        activarBotonGenerarFactura(true);
    }
    
    /**
     * Selecciona mesa vista.
     * 
     */
    public void seleccionarMesaVista(MesaVista mesaVista) {
        boolean ocupada = false;
        
        // Quita selección anterior  
        if (mesaVistaSeleccionada != null) { 
            mesaVistaSeleccionada.deseleccionar();
            comandaVista.ponerTextoVacio();
        }     

        mesaVista.seleccionar();            
        this.mesaVistaSeleccionada = mesaVista;
        comandaVista.ponerEtiquetaComanda(
            mesaVistaSeleccionada.obtenerCodigo());
        
        try {
            ocupada = mesaVista.estaOcupada();
            if (ocupada) {
                activarBotonesMesaOcupada();
            } else {
                activarBotonesMesaDesocupada();
            }
            
            Object[] comanda = mesas.obtenerComandaMesa(
                mesaVista.obtenerCodigo());
            if(comanda != null) {
                comandaVista.ponerComanda(comanda);
                activarBotonesHayComanda();
            } else {
                activarBotonesNoHayComanda();
            }
            
        } catch (Exception ex) {
            if (Restaurante.esModoDebug()) {
                    DebugVista.devolverInstancia().mostrar
                        (local.devuelve(
                            local.ERROR_OBTENER_COMANDA_MESA), ex);
            }
            else{
                mensajeDialogo(local.devuelve(
                    local.ERROR_OBTENER_COMANDA_MESA));
            }
        }
    }
    
    /**
     * Devuelve la mesa vista seleccionada.
     * 
     */
    public MesaVista obtenerMesaVistaSeleccionada() {
        return mesaVistaSeleccionada;
    }
    
    /**
     * Recibe evento ocupar mesa.
     * 
     */
    private void propiedadOcuparMesa(PropertyChangeEvent evt){
        int codigoMesa = (int)evt.getNewValue();
        salonVista.ocuparMesa(codigoMesa);
        activarBotonOcuparMesa(false);
        activarBotonAnadirPlato(true);
    }
    
    /**
     * Recibe evento generar factura.
     * 
     */
    private void propiedadGenerarFactura(PropertyChangeEvent evt){
        int codigoMesa = (int)evt.getNewValue();
        salonVista.generarFacturaMesa(codigoMesa);
        comandaVista.ponerTextoVacio();
        activarBotonOcuparMesa(false);
        activarBotonGenerarFactura(false);
        activarBotonEliminarPlato(false);
        activarBotonAnadirPlato(false);
    }
    
    /**
     * Recibe evento añadir plato.
     * 
     */
    private void propiedadAnadirPlato(PropertyChangeEvent evt){
        try {
            Object tupla = evt.getNewValue();
            Tupla<Integer, Integer> tuplaAn = (Tupla<Integer,
                        Integer>)tupla;
            int codigoMesa = tuplaAn.a;
            int codigoPlato = tuplaAn.b;
            
            if(mesaVistaSeleccionada != null) {
                if(codigoMesa == mesaVistaSeleccionada.obtenerCodigo()) {
                    String plato = mesas.obtenerInfoPlato(codigoPlato);
                    comandaVista.insertarPlatoComanda(plato);
                }  
            }
            
            if(mesas.obtenerNumPlatos(codigoMesa) == UN_SOLO_PLATO) {
                salonVista.ponerHayPlatosEnComanda(codigoMesa); 
            }
            
            activarBotonEliminarPlato(false);
            activarBotonAnadirPlato(true);
            activarBotonGenerarFactura(true);
        } catch (Exception ex) {
            if (Restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar
                    (local.devuelve(
                        local.ERROR_CONEXION_SERVIDOR), ex);
            }
            else{
                mensajeDialogo(local.devuelve(
                    local.ERROR_CONEXION_SERVIDOR));
            }
        }
    }
        
    /**
     * Recibe evento eliminar plato.
     * 
     */
    private void propiedadEliminarPlato(PropertyChangeEvent evt){
        try {
            Object tupla = evt.getNewValue();
            Tupla<Integer, Integer> tuplaEl = (Tupla<Integer,
                        Integer>)tupla;
            int codigoMesa = tuplaEl.a;
            int codigoPlato = tuplaEl.b;
            
            activarBotonEliminarPlato(false);
            if(mesaVistaSeleccionada != null) {
                if(codigoMesa == mesaVistaSeleccionada.obtenerCodigo()){
                    String plato = mesas.obtenerInfoPlato(codigoPlato);
                    comandaVista.eliminarPlatoComanda(plato); 
                } 
            }
            
            if(mesas.obtenerNumPlatos(codigoMesa) == NO_HAY_PLATOS) {
                activarBotonGenerarFactura(false);
                salonVista.ponerNoHayPlatosEnComanda(codigoMesa);
            }
        } catch (Exception ex) {
            if (Restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar
                    (local.devuelve(
                        local.ERROR_CONEXION_SERVIDOR), ex);
            }
            else{
                mensajeDialogo(
                    local.devuelve(local.ERROR_CONEXION_SERVIDOR));
            }
        }
    }
    
    /**
     * Recibe evento conectar.
     * 
     */
    private void propiedadConectar(PropertyChangeEvent evt){
        conectado = (boolean)evt.getNewValue();
        etiquetaConectado.setEnabled(conectado);  
        try {
            ponerSalonVista();
        } catch (Exception ex) {
            if (Restaurante.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar
                    (local.devuelve(
                        local.ERROR_CONEXION_SERVIDOR), ex);
            }
            else{
                mensajeDialogo(local.devuelve(
                    local.ERROR_CONEXION_SERVIDOR));
            }
        }
    }
    
    /**
     * Sobreescribe propertyChange para recibir cambios en modelo.
     * 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equals(
                Mesas.PROPIEDAD_OCUPAR_MESA)) {
            propiedadOcuparMesa(evt);
        } 
        else if(evt.getPropertyName().equals(
                Mesas.PROPIEDAD_GENERAR_FACTURA)) {
            propiedadGenerarFactura(evt);
        } 
        else if(evt.getPropertyName().equals(
                Mesas.PROPIEDAD_ANADIR_PLATO)) {
            propiedadAnadirPlato(evt);
        } 
        else if (evt.getPropertyName().equals(
                Mesas.PROPIEDAD_ELIMINAR_PLATO)) {
            propiedadEliminarPlato(evt);
        }
        else if (evt.getPropertyName().equals(
                Mesas.PROPIEDAD_CONECTADO)) {
            propiedadConectar(evt);
        }
    }
}
