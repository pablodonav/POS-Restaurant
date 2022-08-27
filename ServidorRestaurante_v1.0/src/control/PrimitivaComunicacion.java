/**
 * PrimitivaComunicacion.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */

package control;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 *  Primitiva de comunicación cliente-servidor.
 * 
 */
public enum PrimitivaComunicacion {  
    CONECTAR_PUSH("connect"), 
    DESCONECTAR_PUSH("disconnect"), 
    OBTENER_CARTA("get_menu"),
    NUEVO_ID_CONEXION("new_id_conection"),
    TEST("test"),
    OCUPAR_MESA("new_ocuppied_table"),
    ANADIR_PLATO("new_plate"),
    ELIMINAR_PLATO("remove_plate"),
    OBTENER_COMANDA_MESA("get_tables_command"), 
    OBTENER_PLATO("get_plate"),
    OBTENER_NUM_PLATOS("get_number_of_plates"),
    OBTENER_DISPONIBILIDAD_MESA("get_table_status"),
    FACTURAR_MESA("get_tables_bill"),
    OBTENER_ID_MESA("get_table_id"),
    FIN("fin"),
    OK("ok"),
    NOK("nok");
    
    private String simbolo;

    /**
     *  Construye una primitiva.
     * 
     */   
    PrimitivaComunicacion(String simbolo) {
        this.simbolo = simbolo;   
    }  

    /**
     *  Obtiene sintaxis primitiva comunicación.
     *
     */  
    private static Pattern obtenerSintaxisPrimitiva() {
        String expresionRegular = "";
    
        for (PrimitivaComunicacion primitiva : 
                PrimitivaComunicacion.values()) {    
            expresionRegular = expresionRegular + 
                primitiva.toString() + "|";     
        }
    
        return Pattern.compile(expresionRegular);     
    } 
 
    /**
     *  Devuelve nueva primitiva de comunicación.
     *
     */  
    public static PrimitivaComunicacion nueva(Scanner scanner)
            throws InputMismatchException {
     
        String token = scanner.next(obtenerSintaxisPrimitiva());
   
        for (PrimitivaComunicacion primitiva : 
                PrimitivaComunicacion.values()) {
            if (token.equals(primitiva.toString())) {
                return primitiva;
            }
        }
        return NOK;
    }
  
    /**
     *  toString.
     *
     */  
    @Override
    public String toString() {
        return simbolo;    
    }
}
