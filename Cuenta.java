package Banco;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Cuenta implements Serializable {
    protected int numCuenta;
    protected String nombreCliente;
    protected double saldo;
    // Lista para almacenar los préstamos de ESTA cuenta específica [cite: 45]
    protected ArrayList<Prestamo> prestamos = new ArrayList<>();

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

    // Método nuevo para vincular un préstamo a esta cuenta
    public void agregarPrestamoPersonal(Prestamo p) {
        prestamos.add(p);
    }

    // Método nuevo para obtener los préstamos de esta cuenta para reportes
    public ArrayList<Prestamo> getPrestamosLista() {
        return prestamos;
    }

    public int getNumCuenta() { return numCuenta; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; } // Setter para "Cambios"
    public double getSaldo() { return saldo; }

    @Override
    public String toString() {
        return "No: " + numCuenta + " | Cliente: " + nombreCliente + " | Saldo: $" + saldo;
    }
}