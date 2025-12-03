package Banco;

public class PrestamoMimoto extends Prestamo {
    //Tasa 15%, Enganche se descuenta
    private double enganche;

    public PrestamoMimoto(int numPrestamo, String cliente, double valorVehiculo, int plazoMeses, double enganche) {
        super(numPrestamo, cliente, valorVehiculo, 0.15, plazoMeses); // 15% fijo
        this.enganche = enganche;

        if (plazoMeses > 6 || plazoMeses < 24) {
            System.out.println("El plazo debe estar entre 6 y 24");
        }
    }

    @Override
    public void calcula_prestamo() {
        //Enganche como porcentaje del valor de la moto
        double montoEnganche = this.saldoPrestamo * enganche;

        //Restamos enganche al valor original
        double montoA_Financiar = this.saldoPrestamo - enganche;

        //Validamos rango del prestamo
        if(montoA_Financiar < 10000 || montoA_Financiar > 50000)
            System.out.println("El prestamo debe ser menor a 10000 y 50000");

        //Calculamos intereses sobre ese monto reducido
        double tasaMensual = this.tasaInteres/12;
        double intereses = montoA_Financiar * tasaMensual * this.plazoMeses;

        //El saldo final es lo que quedó por financiar + intereses
        this.saldoPrestamo = montoA_Financiar + intereses;
    }
}