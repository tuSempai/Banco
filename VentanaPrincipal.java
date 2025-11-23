package TrabajoCuentas;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JDesktopPane escritorio; // Contenedor para ventanas internas
    private GestorBanco gestor;      // Instancia de nuestra lógica de negocio

    public VentanaPrincipal() {
        // Inicializar el gestor (carga datos del archivo automáticamente)
        gestor = new GestorBanco();

        // Configuración de la Ventana Principal
        setTitle("Banco Azteca - Sistema de Gestión");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Crear el JDesktopPane (Área de trabajo)
        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        // 2. Crear la Barra de Menú
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

        // Agregar menús a la barra
        barraMenu.add(menuCuentas);
        barraMenu.add(menuPrestamos);
        barraMenu.add(menuOperaciones);
        setJMenuBar(barraMenu);

        // --- ASIGNACIÓN DE EVENTOS (Listeners) ---
        
        // Eventos Cuentas
        itemAltaCuenta.addActionListener(e -> ventanaAltaCuenta());
        itemReporteCuentas.addActionListener(e -> ventanaReporteCuentas());
        itemBuscarCuenta.addActionListener(e -> buscarCuenta());

        // Eventos Préstamos
        itemAltaOrdinario.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemAltaMimoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAltaAutomotriz.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));
        itemReportePrestamos.addActionListener(e -> ventanaReportePrestamos());

        // Evento Operaciones
        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual(); // Asegúrate de crear este método en GestorBanco que recorra las cuentas
            JOptionPane.showMessageDialog(this, "Intereses y comisiones aplicados correctamente.");
            ventanaReporteCuentas(); // Refrescar vista
        });
    }

    // --------------------------------------------------------------------------
    // MÉTODOS PARA VENTANAS INTERNAS (JInternalFrame)
    // --------------------------------------------------------------------------

    // --- FORMULARIO ALTA CUENTA ---
    private void ventanaAltaCuenta() {
        JInternalFrame frame = new JInternalFrame("Nueva Cuenta", true, true, true, true);
        frame.setSize(350, 300);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        // Componentes
        JTextField txtNum = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtSal = new JTextField();
        String[] tipos = {"Ahorro", "Corriente"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        JTextField txtExtra = new JTextField(); // Sirve para Cuota o Costo Transacción
        
        frame.add(new JLabel(" No. Cuenta:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente:")); frame.add(txtNom);
        frame.add(new JLabel(" Saldo Inicial:")); frame.add(txtSal);
        frame.add(new JLabel(" Tipo Cuenta:")); frame.add(cmbTipo);
        frame.add(new JLabel(" Cuota/Costo Trans:")); frame.add(txtExtra);
        
        JButton btnGuardar = new JButton("Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        // Acción Guardar
        btnGuardar.addActionListener(e -> {
            try { // Manejo de errores
                int num = Integer.parseInt(txtNum.getText());
                String nom = txtNom.getText();
                double saldo = Double.parseDouble(txtSal.getText());
                double extra = Double.parseDouble(txtExtra.getText());
                
                Cuenta nuevaCuenta;
                if (cmbTipo.getSelectedItem().equals("Ahorro")) {
                    // num, nombre, saldo, cuotaMantenimiento
                    nuevaCuenta = new CuentaAhorro(num, nom, saldo, extra);
                } else {
                    // num, nombre, saldo, transacciones(0), importeTransaccion
                    nuevaCuenta = new CuentaCorriente(num, nom, saldo, 0, extra);
                }
                
                gestor.agregarCuenta(nuevaCuenta);
                JOptionPane.showMessageDialog(frame, "Cuenta creada exitosamente.");
                frame.dispose(); // Cerrar ventana interna
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: Verifique que los campos numéricos sean correctos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al guardar: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
        escritorio.add(frame);
    }

    // --- FORMULARIO ALTA PRÉSTAMO (Genérico para los 3 tipos) ---
    private void ventanaAltaPrestamo(String tipo) {
        JInternalFrame frame = new JInternalFrame("Nuevo Préstamo: " + tipo, true, true, true, true);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField txtNum = new JTextField();
        JTextField txtCliente = new JTextField();
        JTextField txtMonto = new JTextField();
        JTextField txtPlazo = new JTextField();
        JTextField txtVariable = new JTextField(); // ISR, Enganche o Comision
        JTextField txtIVA = new JTextField(); // Solo para Automotriz
        
        frame.add(new JLabel(" No. Préstamo:")); frame.add(txtNum);
        frame.add(new JLabel(" Cliente:")); frame.add(txtCliente);
        
        // Etiquetas dinámicas según el tipo
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
            frame.add(new JLabel("")); frame.add(new JLabel("")); // Espacio vacío
        }

        JButton btnGuardar = new JButton("Calcular y Guardar");
        frame.add(new JLabel("")); frame.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try { // Manejo de errores
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
                
                // IMPORTANTE: GestorBanco debe tener metodo agregarPrestamo
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

    // --- REPORTE DE CUENTAS (TABLA) ---
    private void ventanaReporteCuentas() {
        JInternalFrame frame = new JInternalFrame("Reporte General de Cuentas", true, true, true, true);
        frame.setSize(600, 400);

        // Definición de tabla
        String[] columnas = {"No. Cuenta", "Cliente", "Tipo", "Saldo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        
        gestor.ordenarPorNombreQuickSort(); // Ordenamiento requerido

        Cuenta[] lista = gestor.getCuentas();
        for (Cuenta c : lista) {
            String tipo = (c instanceof CuentaAhorro) ? "Ahorro" : "Corriente";
            Object[] fila = {c.getNumCuenta(), c.getNombreCliente(), tipo, c.getSaldo()};
            modelo.addRow(fila);
        }

        frame.add(new JScrollPane(tabla)); // Scroll pane es vital para tablas
        frame.setVisible(true);
        escritorio.add(frame);
    }
    
    // --- REPORTE DE PRÉSTAMOS (TABLA) ---
    private void ventanaReportePrestamos() {
        JInternalFrame frame = new JInternalFrame("Reporte de Préstamos", true, true, true, true);
        frame.setSize(600, 400);

        String[] columnas = {"No.", "Cliente", "Tipo", "Deuda Total"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        
        ArrayList<Prestamo> lista = gestor.getPrestamos(); // Asumiendo que GestorBanco devuelve ArrayList
        
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

    // --- BÚ
