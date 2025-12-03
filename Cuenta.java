package Banco;
import java.io.Serializable;
import java.util.ArrayList;

// 'abstract': No se pueden crear objetos 'Cuenta' directamente, solo de sus hijas.
// 'Serializable': Permite guardar (guardar en disco) objetos de esta clase.
public abstract class Cuenta implements Serializable {

    // Atributos 'protected' para que las clases hijas (Ahorro/Corriente) puedan acceder a ellos.
    protected int numCuenta;
    protected String nombreCliente;
    protected double saldo;

    // REQUISITOS: Lista para guardar los préstamos que pertenecen a ESTE cliente.
    // Usamos ArrayList aquí porque el PDF permite dinámicos para préstamos.
    protected ArrayList<Prestamo> prestamos = new ArrayList<>();

    // Constructor: Inicializa los datos básicos al crear la cuenta.
    public Cuenta(int numCuenta, String nombreCliente, double saldo) {
        this.numCuenta = numCuenta;
        this.nombreCliente = nombreCliente;
        this.saldo = saldo;
    }

    // Métodos abstractos: Las hijas ESTÁN OBLIGADAS a programar estos métodos con su propia fórmula.
    public abstract void comisiones();
    public abstract void intereses();

    // Metodo común: Sumar dinero al saldo (sirve para todas las cuentas).
    public void abonar(double cantidad) {
        if (cantidad > 0) {
            this.saldo += cantidad;
        }
    }

    // Metodo común: Restar dinero. Verifica que haya saldo suficiente.
    public void cargar(double cantidad) {
        if (saldo >= cantidad) {
            this.saldo -= cantidad;
        }
    }

    // Getters y Setters (necesarios para ver o modificar datos privados desde otras clases)
    public int getNumCuenta() { return numCuenta; }
    public String getNombreCliente() { return nombreCliente; }

    // Setter necesario para la opción de "Modificar Cuenta" del menú.
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public double getSaldo() { return saldo; }
    public ArrayList<Prestamo> getPrestamos() { return prestamos; }

    // 'toString': Convierte el objeto a texto para mostrarlo bonito en los mensajes.
    @Override
    public String toString() {
        // String.format("%.2f") sirve para mostrar solo 2 decimales en el dinero.
        return "No: " + numCuenta + " | Cliente: " + nombreCliente + " | Saldo: $" + String.format("%.2f", saldo);
    }
}