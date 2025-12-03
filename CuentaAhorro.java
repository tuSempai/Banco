package Banco;



public class CuentaAhorro extends Cuenta {
    // Atributo único de esta clase
    private double cuotaMantenimiento;

    public CuentaAhorro(int numCuenta, String nombreCliente, double saldo, double cuotaMantenimiento) {
        super(numCuenta, nombreCliente, saldo); // Llama al constructor del Padre (Cuenta)
        this.cuotaMantenimiento = cuotaMantenimiento;
    }

    // REQUISITO PDF: Comisiones descuenta la cuota de mantenimiento.
    @Override
    public void comisiones() {
        cargar(cuotaMantenimiento); // Reutilizamos el metodo 'cargar' del padre
    }

    // REQUISITO PDF: Intereses suma el 15% mensual.
    @Override
    public void intereses() {
        double interes = this.saldo * 0.15; // 15%
        abonar(interes); // Reutilizamos 'abonar'
    }
}