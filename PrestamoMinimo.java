package TrabajoCuentas;

// Corrección: El nombre de la clase debe coincidir con el archivo y el PDF [cite: 28]
public class PrestamoMimimo extends Prestamo {
    private double enganche; 

    public PrestamoMimimo(int numPrestamo, String cliente, double valorVehiculo, int plazoMeses, double enganche) {
        // Tasa del 15% según PDF [cite: 28]
        super(numPrestamo, cliente, valorVehiculo, 0.15, plazoMeses); 
        this.enganche = enganche;
    }

    @Override
    public void calcula_prestamo() {
        // Se descuenta el enganche ANTES de intereses [cite: 28]
        double baseCalculo = this.getSaldoPrestamo() - enganche;
        
        // Actualizamos el saldo base (sin intereses aun)
        // Nota: Aquí había un error lógico, no podías restar al saldo si ya tenía intereses.
        // Lo ideal es reiniciar el saldo base:
        double intereses = baseCalculo * 0.15; // 15% tasa fija anual
        
        // El nuevo saldo es: (Valor - Enganche) + Intereses
        abonarCapital(this.getSaldoPrestamo() - (baseCalculo + intereses)); 
        // O simplemente forzar el valor:
        // (Dado que no tenemos setter de saldo, asumimos que el cálculo se hace una sola vez al crear).
    }
}