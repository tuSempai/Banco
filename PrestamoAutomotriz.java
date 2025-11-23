package TrabajoCuentas;

public class PrestamoAutomotriz extends Prestamo {
    // Automotriz: Tasa 12%, Comision Apertura + IVA [cite: 29, 30]
    private double porcentajeComision;
    private double porcentajeIVA;

    public PrestamoAutomotriz(int numPrestamo, String cliente, double monto, int plazoMeses, double porcentajeComision, double porcentajeIVA) {
        super(numPrestamo, cliente, monto, 0.12, plazoMeses); // 12% fijo
        this.porcentajeComision = porcentajeComision;
        this.porcentajeIVA = porcentajeIVA;
    }

    @Override
    public void calcula_prestamo() {
        // Comision por apertura sobre monto credito [cite: 30]
        double comision = this.saldoPrestamo * (porcentajeComision / 100);
        
        // IVA sobre el monto de credito (segun redaccion del punto 30)
        double iva = this.saldoPrestamo * (porcentajeIVA / 100);
        
        // Se suman a la deuda
        this.saldoPrestamo += comision + iva;
        
        // Intereses anuales
        double intereses = this.saldoPrestamo * this.tasaInteres;
        this.saldoPrestamo += intereses;
    }
}