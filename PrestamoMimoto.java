package TrabajoCuentas;

public class PrestamoMimoto extends Prestamo {
    // Según PDF: Tasa 15%, Enganche se descuenta 
    private double enganche; 

    public PrestamoMimoto(int numPrestamo, String cliente, double valorVehiculo, int plazoMeses, double enganche) {
        super(numPrestamo, cliente, valorVehiculo, 0.15, plazoMeses); // 15% fijo
        this.enganche = enganche;
    }

    @Override
    public void calcula_prestamo() {
        // Lógica corregida: 
        // 1. Restamos enganche al valor original
        double montoA_Financiar = this.saldoPrestamo - enganche;
        
        // 2. Calculamos intereses sobre ese monto reducido
        double intereses = montoA_Financiar * this.tasaInteres;
        
        // 3. El saldo final es lo que quedó por financiar + intereses
        this.saldoPrestamo = montoA_Financiar + intereses;
    }
}