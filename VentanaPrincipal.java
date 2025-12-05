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
        setTitle("Banco Azteca - Sistema Universitario");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        escritorio = new JDesktopPane();
        setContentPane(escritorio);

        JMenuBar barraMenu = new JMenuBar();

        // --- MENÚ CUENTAS ---
        JMenu menuCuentas = new JMenu("Gestión Cuentas");
        JMenuItem itemAltaCuenta = new JMenuItem("Alta Nueva Cuenta");
        JMenuItem itemModificarCuenta = new JMenuItem("Modificar Cuenta");
        JMenuItem itemBuscarCuenta = new JMenuItem("Buscar Cuenta (Binaria)");
        JMenuItem itemReporteGeneral = new JMenuItem("Reporte General (Todas)");

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

        menuCuentas.add(itemAltaCuenta);
        menuCuentas.add(itemModificarCuenta);
        menuCuentas.add(itemBuscarCuenta);
        menuCuentas.addSeparator();
        menuCuentas.add(itemReporteGeneral);
        menuCuentas.add(menuOrdenamientos);
        menuCuentas.add(menuCategorias);

        // --- MENÚ CAJERO ---
        JMenu menuMovimientos = new JMenu("Cajero");
        JMenuItem itemTransaccion = new JMenuItem("Depositar o Retirar");
        menuMovimientos.add(itemTransaccion);

        // --- MENÚ PRÉSTAMOS (AQUÍ ESTÁ EL CAMBIO SOLICITADO) ---
        JMenu menuPrestamos = new JMenu("Gestión Préstamos");
        JMenuItem itemAltaOrdinario = new JMenuItem("Alta Préstamo Ordinario");
        JMenuItem itemAltaMimoto = new JMenuItem("Alta Préstamo MiMoto");
        JMenuItem itemAltaAutomotriz = new JMenuItem("Alta Préstamo Automotriz");

        // BOTÓN AGREGADO SEGÚN INSTRUCCIONES:
        JMenuItem itemReporteCliente = new JMenuItem("Buscar Préstamos por Cliente");

        JMenuItem itemReportePrestamos = new JMenuItem("Reporte General Préstamos");

        menuPrestamos.add(itemAltaOrdinario);
        menuPrestamos.add(itemAltaMimoto);
        menuPrestamos.add(itemAltaAutomotriz);
        menuPrestamos.addSeparator();
        menuPrestamos.add(itemReporteCliente); // Agregado aquí
        menuPrestamos.add(itemReportePrestamos);

        // --- MENÚ CIERRE ---
        JMenu menuOperaciones = new JMenu("Administración");
        JMenuItem itemCorte = new JMenuItem("Cierre de Mes");
        menuOperaciones.add(itemCorte);

        barraMenu.add(menuCuentas);
        barraMenu.add(menuMovimientos);
        barraMenu.add(menuPrestamos);
        barraMenu.add(menuOperaciones);
        setJMenuBar(barraMenu);

        // --- EVENTOS ---
        itemAltaCuenta.addActionListener(e -> ventanaAltaCuenta());
        itemModificarCuenta.addActionListener(e -> funcionModificarCuenta());
        itemBuscarCuenta.addActionListener(e -> buscarCuenta());
        itemReporteGeneral.addActionListener(e -> ventanaReporteGeneral(false, true));

        itemRepNomAsc.addActionListener(e -> ventanaReporteGeneral(true, true));
        itemRepNomDesc.addActionListener(e -> ventanaReporteGeneral(true, false));
        itemRepNumAsc.addActionListener(e -> ventanaReporteGeneral(false, true));
        itemRepAhorro.addActionListener(e -> ventanaReporteCategoria("AHORRO"));
        itemRepCorriente.addActionListener(e -> ventanaReporteCategoria("CORRIENTE"));

        itemTransaccion.addActionListener(e -> ventanaMovimientos());

        itemAltaOrdinario.addActionListener(e -> ventanaAltaPrestamo("ORDINARIO"));
        itemAltaMimoto.addActionListener(e -> ventanaAltaPrestamo("MIMOTO"));
        itemAltaAutomotriz.addActionListener(e -> ventanaAltaPrestamo("AUTOMOTRIZ"));

        // EVENTO DEL NUEVO BOTÓN
        itemReporteCliente.addActionListener(e -> funcionBuscarPrestamosCliente());

        itemReportePrestamos.addActionListener(e -> ventanaReportePrestamos());

        itemCorte.addActionListener(e -> {
            gestor.aplicarCorteMensual();
            JOptionPane.showMessageDialog(this, "Intereses aplicados.");
            ventanaReporteGeneral(false, true);
        });
    }

    // --------------------------------------------------------------------------
    // FUNCIONES Y VENTANAS
    // --------------------------------------------------------------------------

    // --- NUEVO: FUNCIÓN PARA BUSCAR PRÉSTAMOS DE UN CLIENTE ---
    private void funcionBuscarPrestamosCliente() {
        // 1. Pedimos el nombre
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre exacto del cliente a buscar:");

        if (nombre != null && !nombre.isEmpty()) {
            // 2. Obtenemos solo sus préstamos
            ArrayList<Prestamo> lista = gestor.obtenerPrestamosPorCliente(nombre);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Este cliente no tiene préstamos registrados.");
            } else {
                // 3. Mostramos la ventana con la tabla
                JInternalFrame frame = new JInternalFrame("Préstamos de: " + nombre, true, true, true, true);
                frame.setSize(600, 300);

                String[] cols = {"No.", "Tipo", "Deuda Total"};
                DefaultTableModel modelo = new DefaultTableModel(cols, 0);
                JTable tabla = new JTable(modelo);

                for (Prestamo p : lista) {
                    String tipo = "Desconocido";
                    if(p instanceof PrestamoOrdinario) tipo = "Ordinario";
                    if(p instanceof PrestamoMimoto) tipo = "MiMoto";
                    if(p instanceof PrestamoAutomotriz) tipo = "Automotriz";

                    modelo.addRow(new Object[]{p.getNumPrestamo(), tipo, String.format("%.2f", p.getSaldoPrestamo())});
                }

                frame.add(new JScrollPane(tabla));
                frame.setVisible(true);
                escritorio.add(frame);
            }
        }
    }

    private void funcionModificarCuenta() {
        String inputNum = JOptionPane.showInputDialog("Ingrese el Número de Cuenta a Modificar:");
        if (inputNum != null && !inputNum.isEmpty()) {
            try {
                int num = Integer.parseInt(inputNum);
                Cuenta c = gestor.buscarCuentaPorNumero(num);
                if (c != null) {
                    String nuevo = JOptionPane.showInputDialog("Cuenta de: " + c.getNombreCliente() + "\nIngrese NUEVO nombre:", c.getNombreCliente());
                    if (nuevo != null && !nuevo.isEmpty()) {
                        gestor.modificarNombreCliente(num, nuevo);
                        JOptionPane.showMessageDialog(this, "Nombre actualizado.");
                    }
                } else JOptionPane.showMessageDialog(this, "Cuenta no existe.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Número inválido."); }
        }
    }

    private void ventanaMovimientos() {
        JInternalFrame frame = new JInternalFrame("Cajero", true, true, true, true);
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField txtCuenta = new JTextField();
        JTextField txtMonto = new JTextField();
        JButton btnDep = new JButton("DEPOSITAR");
        JButton btnRet = new JButton("RETIRAR");
        btnDep.setBackground(Color.GREEN);
        btnRet.setBackground(Color.ORANGE);

        frame.add(new JLabel(" No. Cuenta:")); frame.add(txtCuenta);
        frame.add(new JLabel(" Monto:")); frame.add(txtMonto);
        frame.add(btnDep); frame.add(btnRet);

        btnDep.addActionListener(e -> {
            try { JOptionPane.showMessageDialog(frame, gestor.realizarMovimiento(Integer.parseInt(txtCuenta.getText()), Double.parseDouble(txtMonto.getText()), "DEPOSITAR")); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error datos."); }
        });
        btnRet.addActionListener(e -> {
            try { JOptionPane.showMessageDialog(frame, gestor.realizarMovimiento(Integer.parseInt(txtCuenta.getText()), Double.parseDouble(txtMonto.getText()), "RETIRAR")); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error datos."); }
        });

        frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaAltaCuenta() {
        JInternalFrame frame = new JInternalFrame("Alta Cuenta", true, true, true, true);
        frame.setSize(350, 300);
        frame.setLayout(new GridLayout(6, 2, 10, 10));
        JTextField tNum=new JTextField(), tNom=new JTextField(), tSal=new JTextField(), tExt=new JTextField();
        JComboBox<String> cmb=new JComboBox<>(new String[]{"Ahorro", "Corriente"});

        frame.add(new JLabel(" No.:")); frame.add(tNum);
        frame.add(new JLabel(" Cliente:")); frame.add(tNom);
        frame.add(new JLabel(" Saldo:")); frame.add(tSal);
        frame.add(new JLabel(" Tipo:")); frame.add(cmb);
        frame.add(new JLabel(" Cuota/Costo:")); frame.add(tExt);
        JButton btn=new JButton("Guardar"); frame.add(new JLabel("")); frame.add(btn);

        btn.addActionListener(e -> {
            try {
                int n=Integer.parseInt(tNum.getText());
                double s=Double.parseDouble(tSal.getText()), x=Double.parseDouble(tExt.getText());
                if (cmb.getSelectedItem().equals("Ahorro")) gestor.agregarCuenta(new CuentaAhorro(n, tNom.getText(), s, x));
                else gestor.agregarCuenta(new CuentaCorriente(n, tNom.getText(), s, x));
                JOptionPane.showMessageDialog(frame, "Guardado."); frame.dispose();
            } catch(Exception ex){ JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
        });
        frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaAltaPrestamo(String tipo) {
        JInternalFrame frame = new JInternalFrame("Nuevo Préstamo", true, true, true, true);
        frame.setSize(400, 350);
        frame.setLayout(new GridLayout(7, 2, 10, 10));
        JTextField tNum=new JTextField(), tCli=new JTextField(), tMon=new JTextField(), tPla=new JTextField(), tVar=new JTextField(), tIva=new JTextField();

        frame.add(new JLabel(" No.:")); frame.add(tNum);
        frame.add(new JLabel(" Cliente:")); frame.add(tCli);
        frame.add(new JLabel(" Monto/Valor:")); frame.add(tMon);
        frame.add(new JLabel(" Plazo:")); frame.add(tPla);
        frame.add(new JLabel(" Var (ISR/Eng/Com):")); frame.add(tVar);
        if(tipo.equals("AUTOMOTRIZ")) { frame.add(new JLabel(" IVA:")); frame.add(tIva); }
        else { frame.add(new JLabel("")); frame.add(new JLabel("")); }
        JButton btn=new JButton("Guardar"); frame.add(new JLabel("")); frame.add(btn);

        btn.addActionListener(e -> {
            try {
                if(!gestor.existeCliente(tCli.getText())) { JOptionPane.showMessageDialog(frame, "Cliente no existe."); return; }
                int n=Integer.parseInt(tNum.getText()), p=Integer.parseInt(tPla.getText());
                double m=Double.parseDouble(tMon.getText()), v=Double.parseDouble(tVar.getText());
                Prestamo pr=null;
                if(tipo.equals("ORDINARIO")) pr=new PrestamoOrdinario(n, tCli.getText(), m, p, v);
                else if(tipo.equals("MIMOTO")) pr=new PrestamoMimoto(n, tCli.getText(), m, p, v);
                else pr=new PrestamoAutomotriz(n, tCli.getText(), m, p, v, Double.parseDouble(tIva.getText()));
                gestor.agregarPrestamo(pr); JOptionPane.showMessageDialog(frame, "Total deuda: "+pr.getSaldoPrestamo()); frame.dispose();
            } catch(Exception ex){ JOptionPane.showMessageDialog(frame, "Error datos."); }
        });
        frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaReporteGeneral(boolean nom, boolean asc) {
        JInternalFrame frame = new JInternalFrame("Reporte Cuentas", true, true, true, true);
        frame.setSize(600, 400);
        DefaultTableModel mod = new DefaultTableModel(new String[]{"No.", "Cliente", "Tipo", "Saldo"}, 0);
        gestor.ejecutarQuickSort(nom, asc);
        for(Cuenta c : gestor.getCuentas()) mod.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(), (c instanceof CuentaAhorro?"Ahorro":"Corriente"), c.getSaldo()});
        frame.add(new JScrollPane(new JTable(mod))); frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaReporteCategoria(String cat) {
        JInternalFrame frame = new JInternalFrame("Reporte "+cat, true, true, true, true);
        frame.setSize(600, 400);
        DefaultTableModel mod = new DefaultTableModel(new String[]{"No.", "Cliente", "Tipo", "Saldo"}, 0);
        for(Cuenta c : gestor.obtenerReportePorCategoria(cat, true)) mod.addRow(new Object[]{c.getNumCuenta(), c.getNombreCliente(), (c instanceof CuentaAhorro?"Ahorro":"Corriente"), c.getSaldo()});
        frame.add(new JScrollPane(new JTable(mod))); frame.setVisible(true); escritorio.add(frame);
    }

    private void ventanaReportePrestamos() {
        JInternalFrame frame = new JInternalFrame("Todos los Préstamos", true, true, true, true);
        frame.setSize(600, 400);
        DefaultTableModel mod = new DefaultTableModel(new String[]{"No.", "Cliente", "Tipo", "Deuda"}, 0);
        for(Prestamo p : gestor.getPrestamos()) {
            String t = (p instanceof PrestamoOrdinario)?"Ordinario":(p instanceof PrestamoMimoto)?"MiMoto":"Automotriz";
            mod.addRow(new Object[]{p.getNumPrestamo(), p.getCliente(), t, String.format("%.2f", p.getSaldoPrestamo())});
        }
        frame.add(new JScrollPane(new JTable(mod))); frame.setVisible(true); escritorio.add(frame);
    }

    private void buscarCuenta() {
        String[] op = {"Por Número", "Por Nombre"};
        int el = JOptionPane.showOptionDialog(this, "Buscar:", "Buscar", 0, 3, null, op, op[0]);
        if (el==0) {
            String i=JOptionPane.showInputDialog("No. Cuenta:");
            if(i!=null) try{ mostrar(gestor.buscarCuentaPorNumero(Integer.parseInt(i))); }catch(Exception e){}
        } else if (el==1) {
            String i=JOptionPane.showInputDialog("Nombre:");
            if(i!=null) mostrar(gestor.buscarCuentaPorNombre(i));
        }
    }

    private void mostrar(Cuenta c) {
        JOptionPane.showMessageDialog(this, c!=null ? "Cliente: "+c.getNombreCliente()+"\nSaldo: "+c.getSaldo() : "No encontrada.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}