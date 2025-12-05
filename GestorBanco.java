package Banco;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GestorBanco {
    private ArrayList<Cuenta> cuentas;
    private ArrayList<Prestamo> prestamos;

    public GestorBanco() {
        cuentas = new ArrayList<>();
        prestamos = new ArrayList<>();
        cargarDatos();
    }

    // --- ALTAS ---
    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
        guardarDatos();
    }

    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo();
        prestamos.add(p);
        guardarDatos();
    }

    // --- MODIFICAR ---
    public boolean modificarNombreCliente(int numCuenta, String nuevoNombre) {
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                c.nombreCliente = nuevoNombre;
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    // --- CAJERO ---
    public String realizarMovimiento(int numCuenta, double monto, String tipoMovimiento) {
        Cuenta cuentaEncontrada = null;
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                cuentaEncontrada = c;
                break;
            }
        }

        if (cuentaEncontrada == null) return "Error: La cuenta no existe.";

        if (tipoMovimiento.equals("DEPOSITAR")) {
            cuentaEncontrada.abonar(monto);
        } else if (tipoMovimiento.equals("RETIRAR")) {
            if (cuentaEncontrada.getSaldo() >= monto) {
                cuentaEncontrada.cargar(monto);
            } else {
                return "Error: Fondos insuficientes.";
            }
        }
        guardarDatos();
        return "Éxito: Movimiento realizado. Nuevo Saldo: $" + cuentaEncontrada.getSaldo();
    }

    public boolean existeCliente(String nombreCliente) {
        for (Cuenta c : cuentas) {
            if (c.getNombreCliente().equalsIgnoreCase(nombreCliente)) return true;
        }
        return false;
    }

    // --- NUEVO: OBTENER PRÉSTAMOS DE UN SOLO CLIENTE ---
    // Este metodo cumple con el requerimiento de buscar cliente y mostrar sus préstamos
    public ArrayList<Prestamo> obtenerPrestamosPorCliente(String nombreCliente) {
        ArrayList<Prestamo> listaFiltrada = new ArrayList<>();
        // Recorremos todos los préstamos
        for (Prestamo p : prestamos) {
            // Si el nombre coincide (ignorando mayúsculas), lo agregamos
            if (p.getCliente().equalsIgnoreCase(nombreCliente)) {
                listaFiltrada.add(p);
            }
        }
        return listaFiltrada;
    }

    // --- GETTERS ---
    public Cuenta[] getCuentas() {
        return cuentas.toArray(new Cuenta[0]);
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // --- ORDENAMIENTO QUICKSORT ---
    public void ejecutarQuickSort(boolean porNombre, boolean ascendente) {
        if (cuentas.isEmpty()) return;
        Cuenta[] arr = getCuentas();
        quickSortRecursivo(arr, 0, arr.length - 1, porNombre, ascendente);
        cuentas = new ArrayList<>(Arrays.asList(arr));
    }

    private void quickSortRecursivo(Cuenta[] arr, int low, int high, boolean porNombre, boolean ascendente) {
        if (low < high) {
            int pi = partition(arr, low, high, porNombre, ascendente);
            quickSortRecursivo(arr, low, pi - 1, porNombre, ascendente);
            quickSortRecursivo(arr, pi + 1, high, porNombre, ascendente);
        }
    }

    private int partition(Cuenta[] arr, int low, int high, boolean porNombre, boolean ascendente) {
        Cuenta pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            boolean condicion;
            if (porNombre) {
                int res = arr[j].getNombreCliente().compareToIgnoreCase(pivot.getNombreCliente());
                condicion = ascendente ? (res < 0) : (res > 0);
            } else {
                int res = Integer.compare(arr[j].getNumCuenta(), pivot.getNumCuenta());
                condicion = ascendente ? (res < 0) : (res > 0);
            }
            if (condicion) {
                i++;
                Cuenta temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Cuenta temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // --- ORDENAMIENTO INSERCIÓN ---
    public ArrayList<Cuenta> obtenerReportePorCategoria(String tipoClase, boolean ascendente) {
        ArrayList<Cuenta> filtradas = new ArrayList<>();
        for (Cuenta c : cuentas) {
            if (tipoClase.equals("AHORRO") && c instanceof CuentaAhorro) filtradas.add(c);
            else if (tipoClase.equals("CORRIENTE") && c instanceof CuentaCorriente) filtradas.add(c);
        }

        Cuenta[] arr = filtradas.toArray(new Cuenta[0]);
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            Cuenta key = arr[i];
            int j = i - 1;
            while (j >= 0) {
                boolean mover;
                if (ascendente) mover = arr[j].getNumCuenta() > key.getNumCuenta();
                else mover = arr[j].getNumCuenta() < key.getNumCuenta();

                if (mover) {
                    arr[j + 1] = arr[j];
                    j = j - 1;
                } else break;
            }
            arr[j + 1] = key;
        }
        return new ArrayList<>(Arrays.asList(arr));
    }

    // --- BÚSQUEDAS ---
    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ejecutarQuickSort(false, true);
        Cuenta[] arr = getCuentas();
        int izquierda = 0, derecha = arr.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (arr[medio].getNumCuenta() == numCuenta) return arr[medio];
            if (arr[medio].getNumCuenta() < numCuenta) izquierda = medio + 1;
            else derecha = medio - 1;
        }
        return null;
    }

    public Cuenta buscarCuentaPorNombre(String nombre) {
        ejecutarQuickSort(true, true);
        Cuenta[] arr = getCuentas();
        int izquierda = 0, derecha = arr.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            int res = nombre.compareToIgnoreCase(arr[medio].getNombreCliente());
            if (res == 0) return arr[medio];
            if (res > 0) izquierda = medio + 1;
            else derecha = medio - 1;
        }
        return null;
    }

    // --- CIERRE ---
    public void aplicarCorteMensual() {
        for (Cuenta c : cuentas) {
            c.comisiones();
            c.intereses();
        }
        guardarDatos();
    }

    // --- ARCHIVOS ---
    private void guardarDatos() {
        try {
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream("cuentas.dat"));
            oosC.writeObject(cuentas);
            oosC.close();
            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream("prestamos.dat"));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) { System.out.println("Error guardar: " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try {
            File fC = new File("cuentas.dat");
            if (fC.exists()) {
                ObjectInputStream oisC = new ObjectInputStream(new FileInputStream(fC));
                cuentas = (ArrayList<Cuenta>) oisC.readObject();
                oisC.close();
            }
            File fP = new File("prestamos.dat");
            if (fP.exists()) {
                ObjectInputStream oisP = new ObjectInputStream(new FileInputStream(fP));
                prestamos = (ArrayList<Prestamo>) oisP.readObject();
                oisP.close();
            }
        } catch (Exception e) {}
    }
}