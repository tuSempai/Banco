package Banco;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GestorBanco {
    // Listas para guardar la información en memoria mientras el programa corre
    private ArrayList<Cuenta> cuentas;
    private ArrayList<Prestamo> prestamos;

    // Constructor: Carga los datos guardados al iniciar
    public GestorBanco() {
        cuentas = new ArrayList<>();
        prestamos = new ArrayList<>();
        cargarDatos();
    }

    // --- MÉTODOS DE REGISTRO (ALTAS) ---
    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
        guardarDatos(); // Guardar siempre después de un cambio
    }

    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo();
        prestamos.add(p);
        guardarDatos();
    }

    // --- NUEVO: MÉTODO PARA MODIFICAR CUENTA ---
    // Explicación: Este método busca la cuenta por su número y le cambia el nombre al cliente.
    public boolean modificarNombreCliente(int numCuenta, String nuevoNombre) {
        // 1. Buscamos la cuenta en la lista
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                // 2. Si la encontramos, actualizamos el dato
                c.nombreCliente = nuevoNombre; // Actualizamos el atributo
                guardarDatos(); // 3. Guardamos los cambios en el archivo
                return true; // Indicamos que SÍ se pudo modificar
            }
        }
        return false; // Indicamos que NO se encontró la cuenta
    }

    // --- MÉTODOS DE CAJERO (MOVIMIENTOS) ---
    public String realizarMovimiento(int numCuenta, double monto, String tipoMovimiento) {
        Cuenta cuentaEncontrada = null;
        // Búsqueda lineal simple
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                cuentaEncontrada = c;
                break;
            }
        }

        if (cuentaEncontrada == null) {
            return "Error: La cuenta no existe.";
        }

        // Lógica de Depósito o Retiro
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

    // Validación simple para saber si un cliente existe antes de prestarle dinero
    public boolean existeCliente(String nombreCliente) {
        for (Cuenta c : cuentas) {
            if (c.getNombreCliente().equalsIgnoreCase(nombreCliente)) {
                return true;
            }
        }
        return false;
    }

    // --- MÉTODOS AUXILIARES (GETTERS) ---
    public Cuenta[] getCuentas() {
        // Convertimos la lista a un arreglo normal para facilitar los ordenamientos
        return cuentas.toArray(new Cuenta[0]);
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // --- ORDENAMIENTO QUICKSORT (Recursivo) ---
    // Usado para los reportes generales
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
    // Usado para los reportes por categoría
    public ArrayList<Cuenta> obtenerReportePorCategoria(String tipoClase, boolean ascendente) {
        ArrayList<Cuenta> filtradas = new ArrayList<>();

        // 1. Filtramos
        for (Cuenta c : cuentas) {
            if (tipoClase.equals("AHORRO") && c instanceof CuentaAhorro) {
                filtradas.add(c);
            } else if (tipoClase.equals("CORRIENTE") && c instanceof CuentaCorriente) {
                filtradas.add(c);
            }
        }

        // 2. Ordenamos (Inserción)
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
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
        }

        return new ArrayList<>(Arrays.asList(arr));
    }

    // --- BÚSQUEDAS (BINARIA) ---
    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ejecutarQuickSort(false, true); // Requisito: Ordenar antes de buscar
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

    // --- CIERRE MENSUAL ---
    public void aplicarCorteMensual() {
        for (Cuenta c : cuentas) {
            c.comisiones();
            c.intereses();
        }
        guardarDatos();
    }

    // --- GUARDADO EN ARCHIVOS (PERSISTENCIA) ---
    private void guardarDatos() {
        try {
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream("cuentas.dat"));
            oosC.writeObject(cuentas);
            oosC.close();

            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream("prestamos.dat"));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
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
        } catch (Exception e) {
            // Si es la primera vez que corre, no pasa nada
        }
    }
}