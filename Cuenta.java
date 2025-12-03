package Banco;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Cuenta implements Serializable {
    //protected para que las hijas accedan
    protected int numCuenta;
    protected String nombreCliente;
    protected double saldo;
    protected ArrayList<Prestamo> prestamos = new ArrayList<Prestamo>();

    public Cuenta(int numCuenta, String nombreCliente, double saldo) {
        this.numCuenta = numCuenta;
        this.nombreCliente = nombreCliente;
        this.saldo = saldo;
    }

    public abstract void comisiones();
    public abstract void intereses();

    public void abonar(double cantidad) {
        this.saldo += cantidad;
    }

    public void cargar(double cantidad) {
        if (saldo >= cantidad) {
            this.saldo -= cantidad;
        }
    }

    public int getNumCuenta() { return numCuenta; }
    public String getNombreCliente() { return nombreCliente; }
    public double getSaldo() { return saldo; }

    @Override
    public String toString() {
        return "No: " + numCuenta + " | Cliente: " + nombreCliente + " | Saldo: $" + saldo;
    }
}