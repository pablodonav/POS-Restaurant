/**
 * OyenteVista.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package control;

import vista.Localizacion;

/**
 *  Interfaz de oyente para recibir eventos de la interfaz de usuario.
 * 
 */
public interface OyenteVista {
    public enum Evento { OCUPAR_MESA, ANADIR_PLATO, 
                         ELIMINAR_PLATO, GENERAR_FACTURA, 
                         CAMBIAR_LENGUAJE, SALIR }
  
    /**
     *  Llamado para notificar un evento de la interfaz de usuario.
     * 
     */ 
    public void eventoProducido(Evento evento, Object obj, 
        Localizacion local);
}
