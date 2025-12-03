package Banco;


import java.io.Serializable;

public abstract class Prestamo implements Serializable {
    protected int numPrestamo;
    protected String cliente;
    protected double saldoPrestamo;
    protected double tasaInteres;
    protected int plazoMeses;

    public Prestamo(int numPrestamo, String cliente, double montoOriginal, double tasaInteres, int plazoMeses) {
        this.numPrestamo = numPrestamo;
        this.cliente = cliente;
        this.saldoPrestamo = montoOriginal;
        this.tasaInteres = tasaInteres;
        this.plazoMeses = plazoMeses;
    }

    public abstract void calcula_prestamo();

    public void abonarCapital(double cantidad) {
        if (cantidad > 0) {
            this.saldoPrestamo -= cantidad;
        }
    }

    public int getNumPrestamo() { return numPrestamo; }
    public String getCliente() { return cliente; }
    public double getSaldoPrestamo() { return saldoPrestamo; }

    @Override
    public String toString() {
        return "Préstamo #" + numPrestamo + " | Cliente: " + cliente + " | Deuda: $" + saldoPrestamo;
    }
}