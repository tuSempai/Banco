package Banco;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JDesktopPane escritorio;
    private GestorBanco gestor;

    public VentanaPrincipal() {
        gestor = new GestorBanco();

        // Configuración de la ventana principal
        setTitle("Banco Azteca - Sistema Universitario");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        // --- BARRA DE MENÚ ---
        JMenuBar barraMenu = new JMenuBar();

        // 1. MENÚ CUENTAS
        JMenu menuCuentas = new JMenu("Gestión Cuentas");
        JMenuItem itemAltaCuenta = new JMenuItem("Alta Nueva Cuenta");

        // --- NUEVO BOTÓN: MODIFICAR CUENTA ---
        JMenuItem itemModificarCuenta = new JMenuItem("Modificar Cuenta (Cambiar Nombre)");

        JMenuItem itemBuscarCuenta = new JMenuItem("Buscar Cuenta (Binaria)");
        JMenuItem itemReporteGeneral = new JMenuItem("Reporte General (Todas)");

        // Submenús de reportes
        JMenu menuOrdenamientos = new JMenu("Reportes Ordenados (QuickSort)");
        JMenuItem itemRepNomAsc = new JMenuItem("Por Nombre (A-Z)");
        JMenuItem itemRepNomDesc = new JMenuItem("Por Nombre (Z-A)");
        JMenuItem itemRepNumAsc = new JMenuItem("Por Num Cuenta (Menor a Mayor)");
        menuOrdenamientos.add(itemRepNomAsc); menuOrdenamientos.add(itemRepNomDesc);
        menuOrdenamientos.add(itemRepNumAsc);

        JMenu menuCategorias = new JMenu("Reportes por Categoría (Inserción)");
        JMenuItem itemRepAhorro = new JMenuItem("Solo Ahorro");
        JMenuItem itemRepCorriente = new JMenuItem("Solo Corriente");
        menuCategorias.add(itemRepAhorro); menuCategorias.add(itemRepCorriente);

        // Agregamos los items al menú
        menuCuentas.add(itemAltaCuenta);
        menuCuentas.add(itemModificarCuenta); // Agregamos el nuevo botón aquí
        menuCuentas.add(itemBuscarCuenta);
        menuCuentas.addSeparator();
        menuCuentas.add(itemReporteGeneral);
        menuCuentas.add(menuOrdenamientos);
        menuCuentas.add(menuCategorias);

        // 2. MENÚ CAJERO
        JMenu menuMovimientos = new JMenu("Cajero / Movimientos");
        JMenuItem itemTransaccion = new JMenuItem("Depositar o Retirar");
        menuMovimientos.add(itemTransaccion);

        // 3. MENÚ PRÉSTAMOS
        JMenu menuPrestamos = new JMenu("Gestión Préstamos");
        JMenuItem itemAltaOrdinario = new JMenuItem("Alta Préstamo Ordinario");
        JMenuItem itemAltaMimoto = new JMenuItem("Alta Préstamo MiMoto");
        JMenuItem itemAltaAutomotriz = new JMenuItem("Alta Préstamo Automotriz");
        JMenuItem itemReportePrestamos = new JMenuItem("Reporte de Préstamos");
        menuPrestamos.add(itemAltaOrdinario);
        menuPrestamos.add(itemAltaMimoto);
        menuPrestamos.add(itemAltaAutomotriz);
        menuPrestamos.addSeparator();
        menuPrestamos.add(itemReportePrestamos);

        // 4. MENÚ CIERRE
        JMenu menuOperaciones = new JMenu("Administración");
        JMenuItem itemCorte = new JMenuItem("Cierre de Mes");
        menuOperaciones.add(itemCorte);

        barraMenu.add(menuCuentas);
        barraMenu.add(menuMovimientos);
        barraMenu.add(menuPrestamos);
        barraMenu.add(menuOperaciones);
        setJMenuBar(barraMenu);

        // --- EVENTOS (Lo que hacen los botones) ---
        itemAltaCuenta.addActionListener(e -> ventanaAltaCuenta());

        // --- ACCIÓN DEL NUEVO BOTÓN MODIFICAR ---
        itemModificarCuenta.addActionListener(e -> funcionModificarCuenta());

        itemBuscarCuenta.addActionListener(e -> buscarCuenta());
        itemReporteGeneral.addActionListener(e -> ventanaReporteGeneral(false, true));

        // Eventos Reportes
        itemRepNomAsc.addActionListener(e -> ventanaReporteGeneral(true, true));
        itemRepNomDesc.addActionListener(e -> ventanaReporteGeneral(true, false));
        itemRepNumAsc.addActionListener(e -> ventanaReporteGeneral(false, true));
        itemRepAhorro.addActionListener(e -> ventanaReporteCategoria("AHORRO"));
        itemRepCorriente.addActionListener(e -> ventanaReporteCategoria("CORRIENTE"));

        itemTransaccion.addActionListener(e -> ventanaMovimientos());

        itemAltaOrdinario.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemAltaMimoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAltaAutomotriz.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));
        itemReportePrestamos.addActionListener(e -> ventanaReportePrestamos());

        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual();
            JOptionPane.showMessageDialog(this, "Intereses aplicados correctamente.");
            ventanaReporteGeneral(false, true);
        });
    }

    // --------------------------------------------------------------------------
    // MÉTODOS LÓGICOS Y VENTANAS
    // --------------------------------------------------------------------------

    // --- NUEVO: FUNCIÓN PARA MODIFICAR CUENTA ---
    private void funcionModificarCuenta() {
        // 1. Pedimos el número de cuenta al usuario
        String inputNum = JOptionPane.showInputDialog(this, "Ingrese el Número de Cuenta a Modificar:");

        if (inputNum != null && !inputNum.isEmpty()) {
            try {
                int numCuenta = Integer.parseInt(inputNum);

                // Verificamos si existe buscando primero
                Cuenta c = gestor.buscarCuentaPorNumero(numCuenta);

                if (c != null) {
                    // 2. Si existe, pedimos el nuevo nombre
                    String nuevoNombre = JOptionPane.showInputDialog(this,
                            "Cuenta encontrada: " + c.getNombreCliente() + "\nIngrese el NUEVO nombre del cliente:",
                            c.getNombreCliente()); // Ponemos el nombre actual por defecto

                    if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                        // 3. Mandamos modificar al gestor
                        gestor.modificarNombreCliente(numCuenta, nuevoNombre);
                        JOptionPane.showMessageDialog(this, "¡Nombre actualizado con éxito!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error: La cuenta no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
            }
        }
    }

    // --- VENTANA CAJERO ---
    private void ventanaMovimientos() {
        JInternalFrame frame = new JInternalFrame("Cajero", true, true, true, true);
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField txtCuenta = new JTextField();
        JTextField txtMonto = new JTextField();
        JButton btnDepositar = new JButton("DEPOSITAR");
        btnDepositar.setBackground(Color.GREEN);
        JButton btnRetirar = new JButton("RETIRAR");
        btnRetirar.setBackground(Color.ORANGE);

        frame.add(new JLabel(" No. Cuenta:")); frame.add(txtCuenta);
        frame.add(new JLabel(" Monto ($):")); frame.add(txtMonto);
        frame.add(btnDepositar); frame.add(btnRetirar);

        btnDepositar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtCuenta.getText());
                double monto = Double.parseDouble(txtMonto.getText());
                String msj = gestor.realizarMovimiento(num, monto, "DEPOSITAR");
                JOptionPane.showMessageDialog(frame, msj);
                if(msj.startsWith("Éxito")) frame.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Datos incorrectos."); }
        });

        btnRetirar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtCuenta.getText());
                double monto = Double.parseDouble(txtMonto.getText());
                String msj = gestor.realizarMovimiento(num, monto, "RETIRAR");
                JOptionPane.showMessageDialog(frame, msj);
                if(msj.startsWith("Éxito")) frame.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Datos incorrectos."); }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

    // --- VENTANA ALTA CUENTA ---
    private void ventanaAltaCuenta() {
        JInternalFrame frame = new JInternalFrame("Alta Cuenta", true, true, true, true);
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
        frame.add(new JLabel(" Cuota / Costo:")); frame.add(txtExtra);

        JButton btnGuardar = new JButton("Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText());
                String nom = txtNom.getText();
                double saldo = Double.parseDouble(txtSal.getText());
                double extra = Double.parseDouble(txtExtra.getText());

                Cuenta nueva;
                if (cmbTipo.getSelectedItem().equals("Ahorro"))
                    nueva = new CuentaAhorro(num, nom, saldo, extra);
                else
                    nueva = new CuentaCorriente(num, nom, saldo, extra);

                gestor.agregarCuenta(nueva);
                JOptionPane.showMessageDialog(frame, "Cuenta Guardada.");
                frame.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

    // --- VENTANA ALTA PRÉSTAMO ---
    private void ventanaAltaPrestamo(String tipo) {
        JInternalFrame frame = new JInternalFrame("Préstamo: " + tipo, true, true, true, true);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField txtNum = new JTextField();
        JTextField txtCliente = new JTextField();
        JTextField txtMonto = new JTextField();
        JTextField txtPlazo = new JTextField();
        JTextField txtVariable = new JTextField();
        JTextField txtIVA = new JTextField();

        frame.add(new JLabel(" No. Préstamo:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente:")); frame.add(txtCliente);

        String lblMonto = " Monto:", lblVar = "";
        if(tipo.equals("ORDINARIO")) { lblMonto=" Monto:"; lblVar=" % ISR:"; }
        if(tipo.equals("MIMOTO")) { lblMonto=" Valor Moto:"; lblVar=" Enganche:"; }
        if(tipo.equals("AUTOMOTRIZ")) { lblMonto=" Monto:"; lblVar=" % Comisión:"; }

        frame.add(new JLabel(lblMonto)); frame.add(txtMonto);
        frame.add(new JLabel(" Plazo (meses):")); frame.add(txtPlazo);
        frame.add(new JLabel(lblVar)); frame.add(txtVariable);

        if (tipo.equals("AUTOMOTRIZ")) { frame.add(new JLabel(" % IVA:")); frame.add(txtIVA); }
        else { frame.add(new JLabel("")); frame.add(new JLabel("")); }

        JButton btnGuardar = new JButton("Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                String cli = txtCliente.getText();
                if (!gestor.existeCliente(cli)) {
                    JOptionPane.showMessageDialog(frame, "Cliente no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int num = Integer.parseInt(txtNum.getText());
                double mon = Double.parseDouble(txtMonto.getText());
                int plazo = Integer.parseInt(txtPlazo.getText());
                double var = Double.parseDouble(txtVariable.getText());

                Prestamo p = null;
                if (tipo.equals("ORDINARIO")) p = new PrestamoOrdinario(num, cli, mon, plazo, var);
                else if (tipo.equals("MIMOTO")) p = new PrestamoMimoto(num, cli, mon, plazo, var);
                else if (tipo.equals("AUTOMOTRIZ")) p = new PrestamoAutomotriz(num, cli, mon, plazo, var, Double.parseDouble(txtIVA.getText()));

                gestor.agregarPrestamo(p);
                JOptionPane.showMessageDialog(frame, "Préstamo registrado. Total: " + p.getSaldoPrestamo());
                frame.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error en datos."); }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

    // --- REPORTES ---
    private void ventanaReporteGeneral(boolean porNombre, boolean ascendente) {
        String titulo = "Reporte General (" + (porNombre ? "Nombre" : "Cuenta") + ")";
        JInternalFrame frame = new JInternalFrame(titulo, true, true, true, true);
        frame.setSize(600, 400);

        String[] cols = {"No. Cuenta", "Cliente", "Tipo", "Saldo"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        gestor.ejecutarQuickSort(porNombre, ascendente);

        for (Cuenta c : gestor.getCuentas()) {
            String tipo = (c instanceof CuentaAhorro) ? "Ahorro" : "Corriente";
            modelo.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(), tipo, c.getSaldo()});
        }
        frame.add(new JScrollPane(tabla)); frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaReporteCategoria(String categoria) {
        JInternalFrame frame = new JInternalFrame("Reporte: " + categoria, true, true, true, true);
        frame.setSize(600, 400);

        String[] cols = {"No. Cuenta", "Cliente", "Tipo", "Saldo"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        for (Cuenta c : gestor.obtenerReportePorCategoria(categoria, true)) {
            String tipo = (c instanceof CuentaAhorro) ? "Ahorro" : "Corriente";
            modelo.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(), tipo, c.getSaldo()});
        }
        frame.add(new JScrollPane(tabla)); frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaReportePrestamos() {
        JInternalFrame frame = new JInternalFrame("Reporte Préstamos", true, true, true, true);
        frame.setSize(600, 400);

        String[] cols = {"No.", "Cliente", "Tipo", "Deuda"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        for (Prestamo p : gestor.getPrestamos()) {
            String tipo = (p instanceof PrestamoOrdinario) ? "Ordinario" : (p instanceof PrestamoMimoto) ? "MiMoto" : "Automotriz";
            modelo.addRow(new Object[]{p.getNumPrestamo(), p.getCliente(), tipo, String.format("%.2f", p.getSaldoPrestamo())});
        }
        frame.add(new JScrollPane(tabla)); frame.setVisible(true); escritorio.add(frame);
    }

    // --- BÚSQUEDA ---
    private void buscarCuenta() {
        String[] op = {"Por Número", "Por Nombre"};
        int eleccion = JOptionPane.showOptionDialog(this, "Método de búsqueda:", "Buscar", 0, 3, null, op, op[0]);

        if (eleccion == 0) {
            String in = JOptionPane.showInputDialog("Número de Cuenta:");
            if (in != null) try { mostrarResultado(gestor.buscarCuentaPorNumero(Integer.parseInt(in))); } catch(Exception e){}
        } else if (eleccion == 1) {
            String in = JOptionPane.showInputDialog("Nombre Cliente:");
            if (in != null) mostrarResultado(gestor.buscarCuentaPorNombre(in));
        }
    }

    private void mostrarResultado(Cuenta c) {
        if (c != null)
            JOptionPane.showMessageDialog(this, "Encontrada:\nCuenta: " + c.getNumCuenta() + "\nCliente: " + c.getNombreCliente() + "\nSaldo: " + c.getSaldo());
        else
            JOptionPane.showMessageDialog(this, "No encontrada.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}