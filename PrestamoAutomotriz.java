package Banco;

public class PrestamoAutomotriz extends Prestamo {
    private double porcentajeComision;
    private double porcentajeIVA;

    public PrestamoAutomotriz(int numPrestamo, String cliente, double monto, int plazoMeses, double porcentajeComision, double porcentajeIVA) {
        super(numPrestamo, cliente, monto, 0.12, plazoMeses); // Tasa fija del 12%
        this.porcentajeComision = porcentajeComision;
        this.porcentajeIVA = porcentajeIVA;
    }

    @Override
    public void calcula_prestamo() {
        // Calcular comisiones e impuestos extras
        double comision = this.saldoPrestamo * (porcentajeComision / 100);
        double iva = this.saldoPrestamo * (porcentajeIVA / 100);

        // Sumar todo a la deuda base
        this.saldoPrestamo += comision + iva;

        // Finalmente aplicar interés anual
        double intereses = this.saldoPrestamo * this.tasaInteres;
        this.saldoPrestamo += intereses;
    }
}