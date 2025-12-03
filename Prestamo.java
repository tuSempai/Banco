package Banco;
import java.io.Serializable;

public abstract class Prestamo implements Serializable {
    protected int numPrestamo;
    protected String cliente;
    protected double saldoPrestamo; // Lo que debe el cliente
    protected double tasaInteres;
    protected int plazoMeses;

    public Prestamo(int numPrestamo, String cliente, double montoOriginal, double tasaInteres, int plazoMeses) {
        this.numPrestamo = numPrestamo;
        this.cliente = cliente;
        this.saldoPrestamo = montoOriginal;
        this.tasaInteres = tasaInteres;
        this.plazoMeses = plazoMeses;
    }

    // Metodo abstracto: Cada tipo de préstamo calcula su deuda total de forma diferente.
    public abstract void calcula_prestamo();

    public int getNumPrestamo() { return numPrestamo; }
    public String getCliente() { return cliente; }
    public double getSaldoPrestamo() { return saldoPrestamo; }

    @Override
    public String toString() {
        return "Préstamo #" + numPrestamo + " | Cliente: " + cliente + " | Deuda: $" + String.format("%.2f", saldoPrestamo);
    }
}