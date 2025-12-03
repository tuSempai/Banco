package Banco;

public class PrestamoOrdinario extends Prestamo {
    private double porcentajeISR;

    public PrestamoOrdinario(int numPrestamo, String cliente, double monto, int plazoMeses, double porcentajeISR) {
        super(numPrestamo, cliente, monto, 0.18, plazoMeses); // Tasa fija del 18% (0.18)
        this.porcentajeISR = porcentajeISR;
    }

    @Override
    public void calcula_prestamo() {
        // 1. Calcular ISR sobre el total
        double montoISR = this.saldoPrestamo * (porcentajeISR / 100);
        this.saldoPrestamo += montoISR; // Se suma a la deuda

        // 2. Calcular Intereses anuales
        double intereses = this.saldoPrestamo * (this.tasaInteres);
        this.saldoPrestamo += intereses; // Se suma a la deuda final
    }
}