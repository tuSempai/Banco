package Banco;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// Clase que hereda de JFrame para crear la ventana visual
public class VentanaPrincipal extends JFrame {

    private JDesktopPane escritorio; // Contenedor para las ventanitas internas
    private GestorBanco gestor;      // Referencia a nuestro sistema lógico (el cerebro)

    public VentanaPrincipal() {
        gestor = new GestorBanco(); // Inicia el gestor (y carga datos si existen en los archivos)

        // Configuración básica de la ventana principal
        setTitle("Banco Azteca - Sistema Universitario");
        setSize(1200, 800);
        setLocationRelativeTo(null); // Centrar en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cerrar programa al cerrar ventana

        // JDesktopPane permite tener ventanas internas flotantes (JInternalFrame)
        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        // --- CREACIÓN DEL MENÚ SUPERIOR ---
        JMenuBar barraMenu = new JMenuBar();

        // 1. Menú Cuentas
        JMenu menuCuentas = new JMenu("Gestión Cuentas");
        JMenuItem itemAlta = new JMenuItem("Alta Nueva Cuenta");
        JMenuItem itemBaja = new JMenuItem("Eliminar Cuenta");
        JMenuItem itemCambio = new JMenuItem("Modificar Cuenta");
        JMenuItem itemBuscar = new JMenuItem("Buscar (Binaria)");
        JMenuItem itemReporte = new JMenuItem("Reporte General");

        menuCuentas.add(itemAlta);
        menuCuentas.add(itemBaja);
        menuCuentas.add(itemCambio);
        menuCuentas.addSeparator(); // Línea separadora visual
        menuCuentas.add(itemBuscar);
        menuCuentas.add(itemReporte);

        // 2. Menú Operaciones (Caja)
        JMenu menuOps = new JMenu("Operaciones / Caja");
        JMenuItem itemMovimiento = new JMenuItem("Realizar Depósito/Retiro");
        JMenuItem itemCorte = new JMenuItem("Cierre Mensual (Intereses)");

        menuOps.add(itemMovimiento);
        menuOps.addSeparator();
        menuOps.add(itemCorte);

        // 3. Menú Préstamos
        JMenu menuPrestamos = new JMenu("Gestión Préstamos");
        JMenuItem itemOrd = new JMenuItem("Préstamo Ordinario");
        JMenuItem itemMoto = new JMenuItem("Préstamo MiMoto");
        JMenuItem itemAuto = new JMenuItem("Préstamo Automotriz");
        JMenuItem itemRepPrest = new JMenuItem("Reporte de Préstamos");
        JMenuItem itemRepCliente = new JMenuItem("Préstamos por Cliente");

        menuPrestamos.add(itemOrd);
        menuPrestamos.add(itemMoto);
        menuPrestamos.add(itemAuto);
        menuPrestamos.addSeparator();
        menuPrestamos.add(itemRepPrest);
        menuPrestamos.add(itemRepCliente);

        // Agregamos los menús a la barra
        barraMenu.add(menuCuentas);
        barraMenu.add(menuOps);
        barraMenu.add(menuPrestamos);
        setJMenuBar(barraMenu);

        // --- ASIGNACIÓN DE EVENTOS (LISTENERS) ---
        // Conectamos los clics del menú con los métodos correspondientes
        itemAlta.addActionListener(e -> ventanaAltaCuenta());
        itemBaja.addActionListener(e -> procesoEliminar());
        itemCambio.addActionListener(e -> procesoModificar());
        itemReporte.addActionListener(e -> ventanaReporteCuentas());
        itemBuscar.addActionListener(e -> buscarCuenta());

        itemMovimiento.addActionListener(e -> ventanaMovimientos());
        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual();
            JOptionPane.showMessageDialog(this, "Cierre mensual aplicado correctamente.");
            ventanaReporteCuentas(); // Actualizamos la vista si está abierta
        });

        itemOrd.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemMoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAuto.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));
        itemRepPrest.addActionListener(e -> ventanaReportePrestamos());
        itemRepCliente.addActionListener(e -> buscarPrestamosDeCliente());
    }

    // --- MÉTODOS PARA VENTANAS INTERNAS (GUI) ---

    private void ventanaAltaCuenta() {
        JInternalFrame frame = new JInternalFrame("Nueva Cuenta", true, true, true, true);
        frame.setSize(350, 300);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField txtNum = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtSal = new JTextField();
        String[] tipos = {"Ahorro", "Corriente"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        JTextField txtExtra = new JTextField();

        frame.add(new JLabel(" No. Cuenta:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente:")); frame.add(txtNom);
        frame.add(new JLabel(" Saldo Inicial:")); frame.add(txtSal);
        frame.add(new JLabel(" Tipo Cuenta:")); frame.add(cmbTipo);
        frame.add(new JLabel(" Cuota/Costo Trans:")); frame.add(txtExtra);

        JButton btnGuardar = new JButton("Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText());
                String nom = txtNom.getText();
                double saldo = Double.parseDouble(txtSal.getText());
                double extra = Double.parseDouble(txtExtra.getText());

                Cuenta nueva;
                if (cmbTipo.getSelectedItem().equals("Ahorro")) {
                    nueva = new CuentaAhorro(num, nom, saldo, extra);
                } else {
                    nueva = new CuentaCorriente(num, nom, saldo, extra);
                }

                gestor.agregarCuenta(nueva);
                JOptionPane.showMessageDialog(frame, "Cuenta creada exitosamente.");
                frame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error en datos: " + ex.getMessage());
            }
        });
        frame.setVisible(true);
        escritorio.add(frame);
    }

    private void procesoEliminar() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta a Eliminar:");
        if (input != null) {
            try {
                int num = Integer.parseInt(input);
                if (gestor.eliminarCuenta(num)) {
                    JOptionPane.showMessageDialog(this, "Cuenta eliminada.");
                } else {
                    JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Número inválido."); }
        }
    }

    private void procesoModificar() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta a Modificar:");
        if (input != null) {
            try {
                int num = Integer.parseInt(input);
                Cuenta c = gestor.buscarCuentaPorNumero(num);
                if (c != null) {
                    String nuevoNom = JOptionPane.showInputDialog("Nombre actual: " + c.getNombreCliente() + "\nNuevo nombre:");
                    if (nuevoNom != null && !nuevoNom.isEmpty()) {
                        gestor.modificarCuenta(num, nuevoNom);
                        JOptionPane.showMessageDialog(this, "Modificación exitosa.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error en datos."); }
        }
    }

    // --- MÉTODOS DE CAJA (MOVIMIENTOS) ---
    private void ventanaMovimientos() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta para Operación:");
        if (input == null || input.isEmpty()) return;

        try {
            int num = Integer.parseInt(input);
            Cuenta c = gestor.buscarCuentaPorNumero(num);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
                return;
            }

            String[] ops = {"Depositar", "Retirar"};
            int op = JOptionPane.showOptionDialog(this, "Seleccione Operación", "Caja",
                    0, JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);

            String montoStr = JOptionPane.showInputDialog("Ingrese Cantidad:");
            double monto = Double.parseDouble(montoStr);

            if (op == 0) { // Deposito
                c.abonar(monto);
                JOptionPane.showMessageDialog(this, "Depósito correcto. Nuevo saldo: " + c.getSaldo());
            } else { // Retiro
                if (monto <= c.getSaldo()) {
                    c.cargar(monto);
                    JOptionPane.showMessageDialog(this, "Retiro correcto. Nuevo saldo: " + c.getSaldo());
                    if (c instanceof CuentaCorriente) ((CuentaCorriente)c).nuevaTransaccion();
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
                }
            }
            // Los cambios se guardarán automáticamente si el gestor maneja autoguardado o al cerrar.

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en operación: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE PRÉSTAMOS ---
    private void ventanaAltaPrestamo(String tipo) {
        JInternalFrame frame = new JInternalFrame("Nuevo Préstamo: " + tipo, true, true, true, true);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField txtNum = new JTextField();
        JTextField txtCliente = new JTextField();
        JTextField txtMonto = new JTextField();
        JTextField txtPlazo = new JTextField();
        JTextField txtVariable = new JTextField();
        JTextField txtIVA = new JTextField();

        frame.add(new JLabel(" No. Préstamo:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente (Nombre):")); frame.add(txtCliente);

        String labelMonto = " Monto:";
        String labelVar = "";

        if(tipo.equals("ORDINARIO")) { labelMonto = " Monto ($1k - $2M):"; labelVar = " % ISR:"; }
        if(tipo.equals("MIMOTO")) { labelMonto = " Valor Moto ($10k - $50k):"; labelVar = " Enganche:"; }
        if(tipo.equals("AUTOMOTRIZ")) { labelMonto = " Monto (Max $700k):"; labelVar = " % Comisión:"; }

        frame.add(new JLabel(labelMonto)); frame.add(txtMonto);
        frame.add(new JLabel(" Plazo (meses):")); frame.add(txtPlazo);
        frame.add(new JLabel(labelVar)); frame.add(txtVariable);

        if (tipo.equals("AUTOMOTRIZ")) {
            frame.add(new JLabel(" % IVA:")); frame.add(txtIVA);
        } else {
            frame.add(new JLabel("")); frame.add(new JLabel(""));
        }

        JButton btnGuardar = new JButton("Calcular y Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText());
                String cli = txtCliente.getText();
                double mon = Double.parseDouble(txtMonto.getText());
                int plazo = Integer.parseInt(txtPlazo.getText());
                double var = Double.parseDouble(txtVariable.getText());

                // Validaciones de rangos
                if (tipo.equals("ORDINARIO")) {
                    if (mon < 1000 || mon > 2000000) throw new Exception("Monto fuera de rango (1k-2M)");
                    if (plazo < 6 || plazo > 60) throw new Exception("Plazo fuera de rango (6-60 meses)");
                    gestor.agregarPrestamo(new PrestamoOrdinario(num, cli, mon, plazo, var));
                }
                else if (tipo.equals("MIMOTO")) {
                    if (mon < 10000 || mon > 50000) throw new Exception("Monto fuera de rango (10k-50k)");
                    if (plazo < 6 || plazo > 24) throw new Exception("Plazo fuera de rango (6-24 meses)");
                    gestor.agregarPrestamo(new PrestamoMimoto(num, cli, mon, plazo, var));
                }
                else if (tipo.equals("AUTOMOTRIZ")) {
                    if (mon > 700000) throw new Exception("Monto excede el máximo (700k)");
                    if (plazo > 60) throw new Exception("Plazo excede el máximo (60 meses)");
                    double iva = Double.parseDouble(txtIVA.getText());
                    gestor.agregarPrestamo(new PrestamoAutomotriz(num, cli, mon, plazo, var, iva));
                }

                JOptionPane.showMessageDialog(frame, "Préstamo registrado correctamente.");
                frame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

    private void ventanaReporteCuentas() {
        JInternalFrame frame = new JInternalFrame("Reporte General de Cuentas", true, true, true, true);
        frame.setSize(600, 400);

        String[] columnas = {"No. Cuenta", "Cliente", "Tipo", "Saldo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);

        gestor.ordenarPorNombreQuickSort();

        Cuenta[] lista = gestor.getCuentas();
        for (Cuenta c : lista) {
            String tipo = (c instanceof CuentaAhorro) ? "Ahorro" : "Corriente";
            Object[] fila = {c.getNumCuenta(), c.getNombreCliente(), tipo, String.format("$%.2f", c.getSaldo())};
            modelo.addRow(fila);
        }

        frame.add(new JScrollPane(tabla));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    private void ventanaReportePrestamos() {
        JInternalFrame frame = new JInternalFrame("Reporte de Préstamos", true, true, true, true);
        frame.setSize(600, 400);

        String[] columnas = {"No.", "Cliente", "Tipo", "Deuda Total"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);

        ArrayList<Prestamo> lista = gestor.getPrestamos();

        for (Prestamo p : lista) {
            String tipo = "Desconocido";
            if(p instanceof PrestamoOrdinario) tipo = "Ordinario";
            if(p instanceof PrestamoMimoto) tipo = "MiMoto";
            if(p instanceof PrestamoAutomotriz) tipo = "Automotriz";

            Object[] fila = {p.getNumPrestamo(), p.getCliente(), tipo, String.format("$%.2f", p.getSaldoPrestamo())};
            modelo.addRow(fila);
        }

        frame.add(new JScrollPane(tabla));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    private void buscarPrestamosDeCliente() {
        String nombre = JOptionPane.showInputDialog("Ingrese nombre del cliente a buscar:");
        if(nombre == null || nombre.isEmpty()) return;

        Cuenta c = gestor.buscarCuentaPorNombre(nombre);
        if(c != null) {
            ArrayList<Prestamo> susPrestamos = c.getPrestamos();
            if(susPrestamos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El cliente existe pero no tiene préstamos activos.");
            } else {
                String msj = "Préstamos de " + c.getNombreCliente() + ":\n\n";
                for(Prestamo p : susPrestamos) {
                    msj += p.toString() + "\n";
                }
                JOptionPane.showMessageDialog(this, msj);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    private void buscarCuenta() {
        String[] opciones = {"Por Número", "Por Nombre"};
        int eleccion = JOptionPane.showOptionDialog(this, "Método de búsqueda:", "Buscar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (eleccion == 0) {
            String input = JOptionPane.showInputDialog("Ingrese el Número de Cuenta:");
            if (input != null) {
                try {
                    Cuenta c = gestor.buscarCuentaPorNumero(Integer.parseInt(input));
                    mostrarResultado(c);
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Número inválido."); }
            }
        } else if (eleccion == 1) {
            String input = JOptionPane.showInputDialog("Ingrese el Nombre del Cliente:");
            if (input != null) {
                Cuenta c = gestor.buscarCuentaPorNombre(input);
                mostrarResultado(c);
            }
        }
    }

    private void mostrarResultado(Cuenta c) {
        if (c != null) {
            String info = "Cuenta Encontrada:\n" + c.toString() +
                    "\nTipo: " + (c instanceof CuentaAhorro ? "Ahorro" : "Corriente");
            JOptionPane.showMessageDialog(this, info);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna cuenta.");
        }
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater asegura que la interfaz gráfica
        // se maneje en el hilo correcto (Event Dispatch Thread).
        SwingUtilities.invokeLater(() -> {
            // Creamos una instancia de nuestra ventana y la hacemos visible
            new VentanaPrincipal().setVisible(true);
        });
    }
}