package TrabajoCuentas;

public class PrestamoMinimo extends Prestamo {
    // Mimoto: Tasa 15%, Enganche se descuenta [cite: 28]
    private double enganche; // Cantidad o porcentaje segun se interprete. Asumimos cantidad directa.

    public PrestamoMimoto(int numPrestamo, String cliente, double valorVehiculo, int plazoMeses, double enganche) {
        super(numPrestamo, cliente, valorVehiculo, 0.15, plazoMeses); // 15% fijo
        this.enganche = enganche;
    }

    @Override
    public void calcula_prestamo() {
        // Se descuenta el enganche ANTES de intereses [cite: 28]
        this.saldoPrestamo -= enganche;
        
        // Aplicar intereses al restante
        double intereses = this.saldoPrestamo * this.tasaInteres;
        this.saldoPrestamo += intereses;
    }
}