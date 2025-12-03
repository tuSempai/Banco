package Banco;

public class PrestamoMimoto extends Prestamo {
    private double enganche;

    public PrestamoMimoto(int numPrestamo, String cliente, double valorVehiculo, int plazoMeses, double enganche) {
        super(numPrestamo, cliente, valorVehiculo, 0.15, plazoMeses); // Tasa fija del 15%
        this.enganche = enganche;
    }

    @Override
    public void calcula_prestamo() {
        // Lógica: Primero pagas el enganche, y solo te prestan el resto.
        double montoA_Financiar = this.saldoPrestamo - enganche;

        // Los intereses se calculan sobre lo que pediste prestado (no sobre el valor total de la moto)
        double intereses = montoA_Financiar * this.tasaInteres;

        // La deuda final es el financiamiento + intereses
        this.saldoPrestamo = montoA_Financiar + intereses;
    }
}