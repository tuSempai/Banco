package TrabajoCuentas;


import java.io.Serializable;
import java.util.*;

// Clase abstracta como pide el punto B 
public abstract class Cuenta implements Serializable {
    private  int numCuenta;
    private String nombreCliente;
    private double saldo;
    private ArrayList<Prestamo> prestamos = new ArrayList<Prestamo>(); //Cuando el usuario pida un prestamo o lo solicite
    
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