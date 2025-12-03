package Banco;

public class CuentaCorriente extends Cuenta {
    private int transacciones=0;
    private double importeTransaccion;

    public CuentaCorriente(int numCuenta, String nombreCliente, double saldo, double importeTransaccion) {
        super(numCuenta, nombreCliente, saldo);
        this.importeTransaccion = importeTransaccion;
    }

    @Override
    public void comisiones() {
        // Se aplica si es dia 1 del mes (simulado)
        // Multiplicando transacciones por importe
        double totalComision = transacciones * importeTransaccion;
        cargar(totalComision);
        // Reiniciamos contador de transacciones tras el corte (opcional segun logica de negocio)
        this.transacciones = 0;
    }

    @Override
    public void intereses() {
        // Regla: Si saldo > 20,000 aplica 10%
        if (this.saldo > 20000) {
            abonar(this.saldo * 0.10);
        }
        // Regla: Si saldo entre 5,000 y 10,000 aplica 5%
        else if (this.saldo >= 5000 && this.saldo <= 10000) {
            abonar(this.saldo * 0.05);
        }
    }

    // Getter setter para transacciones si necesitas actualizar el contador manualmente
    public void nuevaTransaccion() {
        this.transacciones++;
    }
}