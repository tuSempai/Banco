package TrabajoCuentas;

import java.io.Serializable;
import java.util.*;

public abstract class Prestamo implements Serializable {
    private int numPrestamo;
    private String cliente; 
    private double saldoPrestamo;
    private double tasaInteres; // Anual
    private int plazoMeses;

    public Prestamo(int numPrestamo, String cliente, double montoOriginal, double tasaInteres, int plazoMeses) {
        this.numPrestamo = numPrestamo;
        this.cliente = cliente;
        this.saldoPrestamo = montoOriginal;
        this.tasaInteres = tasaInteres;
        this.plazoMeses = plazoMeses;
    }

    // Metodo abstracto que cada tipo de prestamo calculará diferente
    public abstract void calcula_prestamo();

    public void abonarCapital(double cantidad) {
        if (cantidad > 0) {
            this.saldoPrestamo -= cantidad; // Reduce la deuda
        }
    }

    // Getters básicos necesarios para reportes
    public int getNumPrestamo() { return numPrestamo; }
    public String getCliente() { return cliente; }
    public double getSaldoPrestamo() { return saldoPrestamo; }
    
    @Override
    public String toString() {
        return "Préstamo #" + numPrestamo + " | Cliente: " + cliente + " | Deuda: $" + saldoPrestamo;
    }
}