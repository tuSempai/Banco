package Banco;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GestorBanco {
    private ArrayList<Cuenta> cuentas;
    private ArrayList<Prestamo> prestamos;

    private final String ARCHIVO_CUENTAS = "cuentas.dat";
    private final String ARCHIVO_PRESTAMOS = "prestamos.dat";

    public GestorBanco() {
        cuentas = new ArrayList<>();
        prestamos = new ArrayList<>();
        cargarDatos();
    }

    // --- GESTIÓN DE CUENTAS (Altas, Bajas, Cambios) ---
    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
        guardarDatos();
    }

    //  Eliminar cuenta
    public boolean eliminarCuenta(int numCuenta) {
        Cuenta c = buscarCuentaPorNumero(numCuenta);
        if (c != null) {
            cuentas.remove(c);
            guardarDatos();
            return true;
        }
        return false;
    }

    //  Cambios (Modificar nombre del cliente)
    public boolean modificarCuenta(int numCuenta, String nuevoNombre) {
        Cuenta c = buscarCuentaPorNumero(numCuenta);
        if (c != null) {
            c.setNombreCliente(nuevoNombre);
            guardarDatos();
            return true;
        }
        return false;
    }

    // --- GESTIÓN DE PRÉSTAMOS ---
    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo();
        prestamos.add(p);

        // [cite: 45] Vincular el préstamo a la cuenta del cliente si existe
        // Buscamos si hay una cuenta con ese nombre exacto
        Cuenta c = buscarCuentaPorNombre(p.getCliente());
        if (c != null) {
            c.agregarPrestamoPersonal(p);
        }

        guardarDatos();
    }

    public Cuenta[] getCuentas() {
        return cuentas.toArray(new Cuenta[0]);
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // --- ALGORITMOS DE ORDENAMIENTO  ---

    // QuickSort Genérico para elegir criterio y orden
    // criterio: 0 = Nombre, 1 = Numero
    // ascendente: true = A-Z/0-9, false = Z-A/9-0
    public void ordenarQuickSort(int criterio, boolean ascendente) {
        if (cuentas.isEmpty()) return;
        Cuenta[] arr = getCuentas();
        quickSort(arr, 0, arr.length - 1, criterio, ascendente);
        cuentas = new ArrayList<>(Arrays.asList(arr));
    }

    private void quickSort(Cuenta[] arr, int low, int high, int criterio, boolean ascendente) {
        if (low < high) {
            int pi = partition(arr, low, high, criterio, ascendente);
            quickSort(arr, low, pi - 1, criterio, ascendente);
            quickSort(arr, pi + 1, high, criterio, ascendente);
        }
    }

    private int partition(Cuenta[] arr, int low, int high, int criterio, boolean ascendente) {
        Cuenta pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            boolean condicion = false;

            if (criterio == 0) { // Por Nombre
                int res = arr[j].getNombreCliente().compareToIgnoreCase(pivot.getNombreCliente());
                condicion = ascendente ? (res < 0) : (res > 0);
            } else { // Por Numero
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

    // [cite: 42] Método Inserción para Categorías
    public void ordenarPorInsercionNumero() {
        Cuenta[] arr = getCuentas();
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            Cuenta key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j].getNumCuenta() > key.getNumCuenta()) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
        cuentas = new ArrayList<>(Arrays.asList(arr));
    }

    // --- BÚSQUEDAS ---
    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ordenarPorInsercionNumero(); // Requisito: Ordenar antes de busqueda binaria
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
        ordenarQuickSort(0, true); // Ordenamos por nombre ascendente para buscar
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

    public void aplicarCorteMensual() {
        for (Cuenta c : cuentas) {
            c.comisiones();
            c.intereses();
        }
        guardarDatos();
    }

    private void guardarDatos() {
        try {
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS));
            oosC.writeObject(cuentas);
            oosC.close();
            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream(ARCHIVO_PRESTAMOS));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) { /* Ignorar */ }
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try {
            File fC = new File(ARCHIVO_CUENTAS);
            if (fC.exists()) {
                ObjectInputStream oisC = new ObjectInputStream(new FileInputStream(fC));
                cuentas = (ArrayList<Cuenta>) oisC.readObject();
                oisC.close();
            }
            File fP = new File(ARCHIVO_PRESTAMOS);
            if (fP.exists()) {
                ObjectInputStream oisP = new ObjectInputStream(new FileInputStream(fP));
                prestamos = (ArrayList<Prestamo>) oisP.readObject();
                oisP.close();
            }
        } catch (Exception e) { /* Ignorar */ }
    }
}