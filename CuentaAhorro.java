package TrabajoCuentas;

public class CuentaAhorro extends Cuenta {
 private double cuotaMantenimiento;

 public CuentaAhorro(int numCuenta, String nombreCliente, double saldo, double cuotaMantenimiento) {
     super(numCuenta, nombreCliente, saldo);
     this.cuotaMantenimiento = cuotaMantenimiento;
 }

 @Override
 public void comisiones() {
     // Se descuenta la cuota de mantenimiento
     cargar(cuotaMantenimiento);
 }

 @Override
 public void intereses() {
     // Se aplica 15% al saldo [cite: 13]
     double interes = this.saldo * 0.15;
     abonar(interes);
 }
}