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
        setTitle("Banco Azteca - Sistema Integral");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        JMenuBar barraMenu = new JMenuBar();

        // --- MENÚ CUENTAS ---
        JMenu menuCuentas = new JMenu("Gestión Cuentas");
        JMenuItem itemAltaCuenta = new JMenuItem("Alta Nueva Cuenta");
        JMenuItem itemBajaCuenta = new JMenuItem("Eliminar Cuenta");
        JMenuItem itemCambioCuenta = new JMenuItem("Cambios (Editar)");
        JMenuItem itemBuscarCuenta = new JMenuItem("Buscar Cuenta");

        menuCuentas.add(itemAltaCuenta);
        menuCuentas.add(itemBajaCuenta);
        menuCuentas.add(itemCambioCuenta);
        menuCuentas.addSeparator();
        menuCuentas.add(itemBuscarCuenta);

        // --- MENÚ PRÉSTAMOS ---
        JMenu menuPrestamos = new JMenu("Gestión Préstamos");
        JMenuItem itemAltaOrdinario = new JMenuItem("Alta Préstamo Ordinario");
        JMenuItem itemAltaMimoto = new JMenuItem("Alta Préstamo MiMoto");
        JMenuItem itemAltaAutomotriz = new JMenuItem("Alta Préstamo Automotriz");

        menuPrestamos.add(itemAltaOrdinario);
        menuPrestamos.add(itemAltaMimoto);
        menuPrestamos.add(itemAltaAutomotriz);

        // --- MENÚ REPORTES ---
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemRepGeneral = new JMenuItem("General Cuentas (QuickSort)");
        JMenuItem itemRepCategoria = new JMenuItem("Por Categoría (Inserción)");
        JMenuItem itemRepClientePrestamos = new JMenuItem("Préstamos por Cliente");
        JMenuItem itemRepCuentasYPrestamos = new JMenuItem("Relación Cuentas-Préstamos");

        menuReportes.add(itemRepGeneral);
        menuReportes.add(itemRepCategoria);
        menuReportes.addSeparator();
        menuReportes.add(itemRepClientePrestamos);
        menuReportes.add(itemRepCuentasYPrestamos);

        // --- MENÚ OPERACIONES ---
        JMenu menuOperaciones = new JMenu("Cierre");
        JMenuItem itemCorte = new JMenuItem("Aplicar Cierre Mensual");
        menuOperaciones.add(itemCorte);

        barraMenu.add(menuCuentas);
        barraMenu.add(menuPrestamos);
        barraMenu.add(menuReportes);
        barraMenu.add(menuOperaciones);
        setJMenuBar(barraMenu);

        // --- EVENTOS ---
        itemAltaCuenta.addActionListener(e -> ventanaAltaCuenta());
        itemBajaCuenta.addActionListener(e -> eliminarCuenta());
        itemCambioCuenta.addActionListener(e -> modificarCuenta());
        itemBuscarCuenta.addActionListener(e -> buscarCuenta());

        itemAltaOrdinario.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemAltaMimoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAltaAutomotriz.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));

        itemRepGeneral.addActionListener(e -> ventanaReporteGeneralOpciones());
        itemRepCategoria.addActionListener(e -> ventanaReporteCategoria());
        itemRepClientePrestamos.addActionListener(e -> ventanaBuscarPrestamosCliente());
        itemRepCuentasYPrestamos.addActionListener(e -> ventanaReporteCuentasConPrestamos());

        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual();
            JOptionPane.showMessageDialog(this, "Cierre mensual aplicado.");
        });
    }

    // ---------------- METODOS GESTION CUENTAS ----------------

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

                Cuenta nueva = cmbTipo.getSelectedItem().equals("Ahorro")
                        ? new CuentaAhorro(num, nom, saldo, extra)
                        : new CuentaCorriente(num, nom, saldo, extra);

                gestor.agregarCuenta(nueva);
                JOptionPane.showMessageDialog(frame, "Guardado.");
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error en datos.");
            }
        });
        frame.setVisible(true);
        escritorio.add(frame);
    }

    private void eliminarCuenta() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta a Eliminar:");
        if (input != null) {
            try {
                int num = Integer.parseInt(input);
                if (gestor.eliminarCuenta(num)) JOptionPane.showMessageDialog(this, "Cuenta Eliminada.");
                else JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Número inválido."); }
        }
    }

    private void modificarCuenta() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta a Modificar:");
        if (input != null) {
            try {
                int num = Integer.parseInt(input);
                Cuenta c = gestor.buscarCuentaPorNumero(num);
                if (c != null) {
                    String nuevoNom = JOptionPane.showInputDialog("Nombre actual: " + c.getNombreCliente() + "\nIngrese nuevo nombre:");
                    if (nuevoNom != null && !nuevoNom.isEmpty()) {
                        gestor.modificarCuenta(num, nuevoNom);
                        JOptionPane.showMessageDialog(this, "Modificado correctamente.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
                }
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error."); }
        }
    }

    // ---------------- METODOS GESTION PRESTAMOS (CON VALIDACIONES) ----------------

    private void ventanaAltaPrestamo(String tipo) {
        JInternalFrame frame = new JInternalFrame("Nuevo Préstamo " + tipo, true, true, true, true);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField txtNum = new JTextField();
        JTextField txtCliente = new JTextField(); // Debería coincidir con un cliente existente
        JTextField txtMonto = new JTextField();
        JTextField txtPlazo = new JTextField();
        JTextField txtVariable = new JTextField();
        JTextField txtIVA = new JTextField();

        frame.add(new JLabel(" No. Préstamo:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente (Nombre):")); frame.add(txtCliente);

        String lblMonto = " Monto:";
        if(tipo.equals("MIMOTO")) lblMonto = " Valor Moto:";
        frame.add(new JLabel(lblMonto)); frame.add(txtMonto);
        frame.add(new JLabel(" Plazo (meses):")); frame.add(txtPlazo);

        String lblVar = " % ISR:";
        if(tipo.equals("MIMOTO")) lblVar = " Enganche ($):";
        if(tipo.equals("AUTOMOTRIZ")) lblVar = " % Comisión:";
        frame.add(new JLabel(lblVar)); frame.add(txtVariable);

        if (tipo.equals("AUTOMOTRIZ")) {
            frame.add(new JLabel(" % IVA:")); frame.add(txtIVA);
        } else {
            frame.add(new JLabel("")); frame.add(new JLabel(""));
        }

        JButton btn = new JButton("Validar y Guardar");
        frame.add(new JLabel("")); frame.add(btn);

        btn.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText());
                String cli = txtCliente.getText();
                double mon = Double.parseDouble(txtMonto.getText());
                int plazo = Integer.parseInt(txtPlazo.getText());
                double var = Double.parseDouble(txtVariable.getText());

                // --- VALIDACIONES DE RANGO ---
                if (tipo.equals("ORDINARIO")) {
                    if (mon < 1000 || mon > 2000000) throw new Exception("Monto Ordinario debe ser entre 1,000 y 2,000,000");
                    if (plazo < 6 || plazo > 60) throw new Exception("Plazo Ordinario debe ser entre 6 y 60 meses");
                    gestor.agregarPrestamo(new PrestamoOrdinario(num, cli, mon, plazo, var));
                }
                else if (tipo.equals("MIMOTO")) {
                    if (mon < 10000 || mon > 50000) throw new Exception("Valor Moto debe ser entre 10,000 y 50,000");
                    if (plazo < 6 || plazo > 24) throw new Exception("Plazo MiMoto debe ser entre 6 y 24 meses");
                    gestor.agregarPrestamo(new PrestamoMimoto(num, cli, mon, plazo, var));
                }
                else if (tipo.equals("AUTOMOTRIZ")) {
                    if (mon > 700000) throw new Exception("Monto Automotriz máximo es 700,000");
                    if (plazo > 60) throw new Exception("Plazo máximo es 60 meses");
                    double iva = Double.parseDouble(txtIVA.getText());
                    gestor.agregarPrestamo(new PrestamoAutomotriz(num, cli, mon, plazo, var, iva));
                }

                JOptionPane.showMessageDialog(frame, "Préstamo registrado correctamente.");
                frame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });
        frame.setVisible(true);
        escritorio.add(frame);
    }

    // ---------------- REPORTES ----------------

    // Reporte 1: General con opciones de ordenamiento
    private void ventanaReporteGeneralOpciones() {
        String[] criterios = {"Por Nombre", "Por Número de Cuenta"};
        String[] ordenes = {"Ascendente", "Descendente"};

        JComboBox<String> cmbCrit = new JComboBox<>(criterios);
        JComboBox<String> cmbOrd = new JComboBox<>(ordenes);

        int result = JOptionPane.showConfirmDialog(this, new Object[]{"Criterio:", cmbCrit, "Orden:", cmbOrd},
                "Opciones de Reporte", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int critIdx = cmbCrit.getSelectedIndex(); // 0 nombre, 1 numero
            boolean asc = cmbOrd.getSelectedIndex() == 0;

            gestor.ordenarQuickSort(critIdx, asc); // Aplica QuickSort
            mostrarTablaCuentas("Reporte General (" + cmbCrit.getSelectedItem() + " - " + cmbOrd.getSelectedItem() + ")");
        }
    }

    private void mostrarTablaCuentas(String titulo) {
        JInternalFrame frame = new JInternalFrame(titulo, true, true, true, true);
        frame.setSize(600, 400);
        String[] col = {"No.", "Cliente", "Tipo", "Saldo"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);

        for (Cuenta c : gestor.getCuentas()) {
            mod.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(),
                    (c instanceof CuentaAhorro ? "Ahorro" : "Corriente"), c.getSaldo()});
        }
        frame.add(new JScrollPane(new JTable(mod)));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    // Reporte 2: Por Categoría (Usa Inserción)
    private void ventanaReporteCategoria() {
        gestor.ordenarPorInsercionNumero(); // Ordena por número primero

        JInternalFrame frame = new JInternalFrame("Reporte por Categoría", true, true, true, true);
        frame.setSize(600, 400);
        String[] col = {"Categoría", "No.", "Cliente", "Saldo"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);

        // Primero Ahorros
        for (Cuenta c : gestor.getCuentas()) {
            if (c instanceof CuentaAhorro)
                mod.addRow(new Object[]{"AHORRO", c.getNumCuenta(), c.getNombreCliente(), c.getSaldo()});
        }
        // Luego Corrientes
        for (Cuenta c : gestor.getCuentas()) {
            if (c instanceof CuentaCorriente)
                mod.addRow(new Object[]{"CORRIENTE", c.getNumCuenta(), c.getNombreCliente(), c.getSaldo()});
        }

        frame.add(new JScrollPane(new JTable(mod)));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    // Reporte 3: Buscar Cliente y mostrar sus préstamos
    private void ventanaBuscarPrestamosCliente() {
        String nombre = JOptionPane.showInputDialog("Ingrese Nombre del Cliente:");
        if (nombre == null || nombre.isEmpty()) return;

        ArrayList<Prestamo> encontrados = new ArrayList<>();
        // Buscamos en la lista global de préstamos por coincidencia de nombre
        for (Prestamo p : gestor.getPrestamos()) {
            if (p.getCliente().equalsIgnoreCase(nombre)) {
                encontrados.add(p);
            }
        }

        if (encontrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron préstamos para: " + nombre);
            return;
        }

        JInternalFrame frame = new JInternalFrame("Préstamos de: " + nombre, true, true, true, true);
        frame.setSize(600, 300);
        String[] col = {"No. Préstamo", "Tipo", "Deuda Total"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);

        for (Prestamo p : encontrados) {
            String tipo = (p instanceof PrestamoOrdinario) ? "Ordinario" :
                    (p instanceof PrestamoMimoto) ? "MiMoto" : "Automotriz";
            mod.addRow(new Object[]{p.getNumPrestamo(), tipo, String.format("%.2f", p.getSaldoPrestamo())});
        }

        frame.add(new JScrollPane(new JTable(mod)));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    // Reporte 4: General (Cuenta y Nombre) mostrar préstamos
    private void ventanaReporteCuentasConPrestamos() {
        JInternalFrame frame = new JInternalFrame("Relación Cuentas - Préstamos", true, true, true, true);
        frame.setSize(700, 500);

        String[] col = {"No. Cuenta", "Cliente", "Préstamos Asignados (Detalle)"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);

        gestor.ordenarQuickSort(0, true); // Orden alfabético

        for (Cuenta c : gestor.getCuentas()) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Prestamo> listaP = c.getPrestamosLista(); // Usamos la lista interna de la cuenta

            if (listaP.isEmpty()) {
                sb.append("Sin préstamos");
            } else {
                for (Prestamo p : listaP) {
                    sb.append("[#").append(p.getNumPrestamo()).append(" $").append(String.format("%.0f", p.getSaldoPrestamo())).append("] ");
                }
            }
            mod.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(), sb.toString()});
        }

        frame.add(new JScrollPane(new JTable(mod)));
        frame.setVisible(true);
        escritorio.add(frame);
    }

    // Búsqueda simple
    private void buscarCuenta() {
        String input = JOptionPane.showInputDialog("Ingrese No. Cuenta:");
        if (input != null) {
            try {
                Cuenta c = gestor.buscarCuentaPorNumero(Integer.parseInt(input));
                if (c != null) JOptionPane.showMessageDialog(this, c.toString());
                else JOptionPane.showMessageDialog(this, "No encontrada.");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error número."); }
        }
    }

    // --- MAIN AGREGADO ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}