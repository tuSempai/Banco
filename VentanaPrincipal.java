package TrabajoCuentas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JDesktopPane escritorio; 
    private GestorBanco gestor;      

    public VentanaPrincipal() {
        // Inicializar el gestor
        gestor = new GestorBanco();

        // Configuración de la ventana
        setTitle("Banco Azteca - Sistema de Gestión");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Crear el JDesktopPane
        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        // 2. Barra de Menú
        JMenuBar barraMenu = new JMenuBar();

        // --- MENÚ CUENTAS ---
        JMenu menuCuentas = new JMenu("Gestión Cuentas");
        JMenuItem itemAltaCuenta = new JMenuItem("Alta Nueva Cuenta");
        JMenuItem itemBuscarCuenta = new JMenuItem("Buscar (Binaria)");
        JMenuItem itemReporteCuentas = new JMenuItem("Reporte General");
        
        menuCuentas.add(itemAltaCuenta);
        menuCuentas.add(itemBuscarCuenta);
        menuCuentas.add(itemReporteCuentas);

        // --- MENÚ PRÉSTAMOS ---
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

        // --- MENÚ OPERACIONES ---
        JMenu menuOperaciones = new JMenu("Cierre de Mes");
        JMenuItem itemCorte = new JMenuItem("Aplicar Intereses y Comisiones");
        menuOperaciones.add(itemCorte);

        barraMenu.add(menuCuentas);
        barraMenu.add(menuPrestamos);
        barraMenu.add(menuOperaciones);
        setJMenuBar(barraMenu);

        // --- EVENTOS ---
        itemAltaCuenta.addActionListener(e -> ventanaAltaCuenta());
        itemReporteCuentas.addActionListener(e -> ventanaReporteCuentas());
        itemBuscarCuenta.addActionListener(e -> buscarCuenta());

        itemAltaOrdinario.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemAltaMimoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAltaAutomotriz.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));
        itemReportePrestamos.addActionListener(e -> ventanaReportePrestamos());

        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual(); 
            JOptionPane.showMessageDialog(this, "Intereses y comisiones aplicados correctamente.");
            ventanaReporteCuentas(); 
        });
    }

    // --------------------------------------------------------------------------
    // MÉTODOS PARA VENTANAS INTERNAS
    // --------------------------------------------------------------------------

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
                
                Cuenta nuevaCuenta;
                if (cmbTipo.getSelectedItem().equals("Ahorro")) {
                    nuevaCuenta = new CuentaAhorro(num, nom, saldo, extra);
                } else {
                    // CORRECCIÓN AQUÍ: Eliminamos el '0' que sobraba
                    nuevaCuenta = new CuentaCorriente(num, nom, saldo, extra);
                }
                
                gestor.agregarCuenta(nuevaCuenta);
                JOptionPane.showMessageDialog(frame, "Cuenta creada exitosamente.");
                frame.dispose(); 
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: Verifique números.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al guardar: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

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
        frame.add(new JLabel(" Cliente:")); frame.add(txtCliente);
        
        String labelMonto = " Monto:";
        String labelVar = "";
        
        if(tipo.equals("ORDINARIO")) { labelMonto = " Monto:"; labelVar = " % ISR:"; }
        if(tipo.equals("MIMOTO")) { labelMonto = " Valor Moto:"; labelVar = " Enganche:"; }
        if(tipo.equals("AUTOMOTRIZ")) { labelMonto = " Monto:"; labelVar = " % Comisión:"; }
        
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
                
                Prestamo nuevoP = null;
                
                if (tipo.equals("ORDINARIO")) {
                    nuevoP = new PrestamoOrdinario(num, cli, mon, plazo, var);
                } else if (tipo.equals("MIMOTO")) {
                    nuevoP = new PrestamoMimoto(num, cli, mon, plazo, var);
                } else if (tipo.equals("AUTOMOTRIZ")) {
                    double iva = Double.parseDouble(txtIVA.getText());
                    nuevoP = new PrestamoAutomotriz(num, cli, mon, plazo, var, iva);
                }
                
                gestor.agregarPrestamo(nuevoP); 
                JOptionPane.showMessageDialog(frame, "Préstamo registrado.\nTotal a pagar: $" + nuevoP.getSaldoPrestamo());
                frame.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error en datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            Object[] fila = {c.getNumCuenta(), c.getNombreCliente(), tipo, c.getSaldo()};
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
            
            Object[] fila = {p.getNumPrestamo(), p.getCliente(), tipo, String.format("%.2f", p.getSaldoPrestamo())};
            modelo.addRow(fila);
        }

        frame.add(new JScrollPane(tabla));
        frame.setVisible(true);
        escritorio.add(frame);
    }
    
    // --- MÉTODOS DE BÚSQUEDA ---
    private void buscarCuenta() {
        String[] opciones = {"Por Número de Cuenta", "Por Nombre del Cliente"};
        int eleccion = JOptionPane.showOptionDialog(this, 
                "Seleccione el método de búsqueda (Binaria):", 
                "Buscar Cuenta", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, opciones, opciones[0]);

        if (eleccion == 0) {
            String input = JOptionPane.showInputDialog(this, "Ingrese el Número de Cuenta:");
            if (input != null && !input.isEmpty()) {
                try {
                    int num = Integer.parseInt(input);
                    Cuenta c = gestor.buscarCuentaPorNumero(num);
                    mostrarResultadoBusqueda(c);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un número válido.");
                }
            }
        } else if (eleccion == 1) {
            String input = JOptionPane.showInputDialog(this, "Ingrese el Nombre del Cliente:");
            if (input != null && !input.isEmpty()) {
                Cuenta c = gestor.buscarCuentaPorNombre(input);
                mostrarResultadoBusqueda(c);
            }
        }
    }

    private void mostrarResultadoBusqueda(Cuenta c) {
        if (c != null) {
            String info = "Cuenta Encontrada:\n\n" +
                          "Número: " + c.getNumCuenta() + "\n" +
                          "Cliente: " + c.getNombreCliente() + "\n" +
                          "Saldo Actual: $" + c.getSaldo() + "\n" +
                          "Tipo: " + (c instanceof CuentaAhorro ? "Ahorro" : "Corriente");
            JOptionPane.showMessageDialog(this, info, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna cuenta.", "Sin resultados", JOptionPane.WARNING_MESSAGE);
        }
    }
}
