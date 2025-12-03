package Banco;

public class CuentaCorriente extends Cuenta {
    private int transacciones = 0; // Contador de movimientos
    private double importeTransaccion; // Costo por cada movimiento

    public CuentaCorriente(int numCuenta, String nombreCliente, double saldo, double importeTransaccion) {
        super(numCuenta, nombreCliente, saldo);
        this.importeTransaccion = importeTransaccion;
    }

    // REQUISITO PDF: Cobra (Transacciones * Costo).
    @Override
    public void comisiones() {
        double totalComision = transacciones * importeTransaccion;
        cargar(totalComision);
        this.transacciones = 0; // Al corte de mes, el contador vuelve a cero.
    }

    // REQUISITO PDF: Reglas de intereses según el saldo.
    @Override
    public void intereses() {
        if (this.saldo > 20000) {
            abonar(this.saldo * 0.10); // 10% si tienes mucho dinero
        } else if (this.saldo >= 5000 && this.saldo <= 10000) {
            abonar(this.saldo * 0.05); // 5% si tienes saldo medio
        }
        // Si tienes menos de 5000, no ganas intereses (regla implícita).
    }

    // Metodo extra para contar cada vez que el usuario hace un retiro.
    public void nuevaTransaccion() {
        this.transacciones++;
    }
}