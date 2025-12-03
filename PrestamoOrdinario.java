package Banco;

public class PrestamoOrdinario extends Prestamo {
    // Ordinario: Tasa 18%, ISR sobre el total
    private double porcentajeISR;

    public PrestamoOrdinario(int numPrestamo, String cliente, double monto, int plazoMeses, double porcentajeISR) {
        super(numPrestamo, cliente, monto, 0.18, plazoMeses); // 18% fijo
        this.porcentajeISR = porcentajeISR;
    }

    @Override
    public void calcula_prestamo() {
        // El ISR se acumula al prestamo
        double montoISR = this.saldoPrestamo * (porcentajeISR / 100);
        this.saldoPrestamo += montoISR;

        // Asumimos calculo simple inicial:
        double intereses = this.saldoPrestamo * (this.tasaInteres);
        this.saldoPrestamo += intereses;
    }
}
