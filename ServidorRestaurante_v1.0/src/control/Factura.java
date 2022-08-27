/**
 * Factura.java
 * Pablo Doñate y Adnana Dragut (05/2021). 
 *   
 */
package control;

import modelo.Comanda;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import modelo.Localizacion;
import modelo.Mesa;
import modelo.Plato;

/**
 * Factura de una mesa.
 * 
 */
public class Factura {
    private int codigo;
    private Mesa mesa;
    private Localizacion local;
    
    private static final int IVA = 10;
    private static final String NOMBRE_FICHERO_CONTADOR_FACTURAS = 
            "contadorFacturas.txt";
    private static final String EXTENSION_DOCUMENTO_TEXTO = ".txt";
    
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm";
    private static final String FORMATO_IVA = "%.2f";
    
    /**
     * Construye una factura.
     * 
     */
    public Factura(Mesa mesa) throws Exception {
        this.codigo = devolverCodigoFactura(
            NOMBRE_FICHERO_CONTADOR_FACTURAS);
        this.mesa = mesa;
    }
    
    /**
     * Devuelve la fecha y hora actual.
     * 
     */
    private String obtenerFechaActual () {
        Date date = new Date();
        DateFormat hourdateFormat = 
            new SimpleDateFormat(FORMATO_FECHA);
        
        return hourdateFormat.format(date);
    }
    
    /**
     * Calcula el precio total de los platos de una comanda.
     * 
     */
    public double calcularPrecio(Comanda comanda) {
        double precio = 0;
        Plato[] platos = comanda.obtenerPlatos();
        
        for(Plato plato : platos) {
            precio = precio + plato.obtenerPrecio();
        }
        return precio;
    }
    
    /**
     * Genera la factura de una mesa.
     * 
     */
    public boolean generar(Localizacion local) 
        throws Exception {
        this.local = local;
        
        String nombreFichero = (local.devuelve(
            local.FICHERO_FACTURA) + codigo + 
            EXTENSION_DOCUMENTO_TEXTO);
        
        Comanda comanda = mesa.obtenerComanda();
        if(comanda != null) {
            escribirFactura(nombreFichero, comanda);
            return true;
        }
        return false;
    }
    
    /**
     * Si hay comanda escribe la factura en el fichero.
     * 
     */
    private void escribirFactura(String nombreFichero, 
            Comanda comanda) throws Exception{
        double precio = 0;
        double iva = 0;
        
        PrintWriter pw = new PrintWriter(new BufferedWriter
            (new FileWriter(nombreFichero)));

        pw.println(local.devuelve(local.TITULO));
        pw.println(local.devuelve(local.FACTURA) + codigo);
        pw.println(local.devuelve(local.MESA_FACTURA) +
            mesa.obtenerCodigo());
        pw.println(obtenerFechaActual());
        pw.print(comanda.toString());

        precio = calcularPrecio(comanda);
        iva = (precio * IVA) / 100.0;
        pw.println(local.devuelve(local.IVA_FACTURA) + 
            IVA + "% " + 
        String.format(FORMATO_IVA, iva) + " " + 
        local.devuelve(local.UNIDAD_MONETARIA));
        pw.println(local.devuelve(local.TOTAL_FACTURA) + 
            (precio + iva) + " " + 
        local.devuelve(local.UNIDAD_MONETARIA));
        pw.close();
    }
    
    /**
     * Devuelve el codigo único de la factura.
     * 
     */
    private int devolverCodigoFactura(String nombreFichero) 
            throws Exception {
        int codigoFactura = 0;
        Scanner scanner = new Scanner(
            new FileInputStream(nombreFichero));
        
        codigoFactura = Integer.parseInt(scanner.nextLine());
        scanner.close();
        
        guardarCodigoSiguiente(nombreFichero, codigoFactura);
        return codigoFactura;
    }
    
    /**
     * Guarda en fichero el código único de la proxima factura.
     * 
     */
    private void guardarCodigoSiguiente(String nombreFichero,
            int codigoFactura) throws Exception {
        PrintWriter pw = new PrintWriter(new BufferedWriter
            (new FileWriter(nombreFichero)));

        pw.println(codigoFactura+1);
        pw.close();
    }
    
    /**
     * Sobreescribe toString.
     *
     */
    @Override
    public String toString() {
        String s = "";
        double precio = 0;
        double iva = 0;
        Comanda comanda = mesa.obtenerComanda();
        
        if(comanda != null) {
            s = s + local.devuelve(local.FACTURA) + codigo + "\n";
            s = s + local.devuelve(local.MESA_FACTURA) + 
                mesa.obtenerCodigo() + "\n";
            s = s + obtenerFechaActual() + "\n";
            s = s + comanda.toString();

            precio = calcularPrecio(comanda);
            iva = (precio * IVA) / 100.0;
            s = s + local.devuelve(local.IVA_FACTURA) + IVA + "% " + 
                String.format(FORMATO_IVA, iva) + 
                local.devuelve(local.UNIDAD_MONETARIA) + "\n";
            s = s + local.devuelve(local.TOTAL_FACTURA) + 
                (precio + iva) + local.devuelve(
                    local.UNIDAD_MONETARIA) + "\n";
        }
        return s;
    }
}
