package Banco;

public class PrestamoAutomotriz extends Prestamo{
    // Tasa 12%, comision apertura + IVA
    private double porcentajeComision = 0.12;
    private double porcentajeIVA = 0.16;

    public PrestamoAutomotriz(int numPrestamo, String cliente, double monto, int plazoMeses, double porcentajeComision, double porcentajeIVA) {
        super(numPrestamo, cliente, monto, 0.12, plazoMeses); // 12% fijo
        this.porcentajeComision = porcentajeComision;
        this.porcentajeIVA = porcentajeIVA;

        if(plazoMeses > 6 || plazoMeses < 60) {
            System.out.println("El plazo debe de estar entre 6 a 60 meses");

        }
    }

    public void calcula_prestamo(){
        //Comision por apertura sobre monto credito
        double comision = this.saldoPrestamo * (porcentajeComision / 100);

        //IVA sobre el monto credito
        double IVA = comision * (porcentajeIVA / 100);

        //Se suman a la deuda
        this.saldoPrestamo += comision + IVA;

        //Intereses anuales
        double tasaMensual = this.tasaInteres/12;
        double intereses = this.saldoPrestamo * tasaMensual * this.plazoMeses;
        this.saldoPrestamo += intereses;
    }

}
