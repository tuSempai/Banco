package TrabajoCuentas;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GestorBanco {
    // Usamos ArrayList para manejo dinámico interno, pero convertiremos a Arrays para los algoritmos estáticos si es necesario
    private ArrayList<Cuenta> cuentas;
    private ArrayList<Prestamo> prestamos;
    
    private final String ARCHIVO_CUENTAS = "cuentas.dat";
    private final String ARCHIVO_PRESTAMOS = "prestamos.dat";

    public GestorBanco() {
        cuentas = new ArrayList<>();
        prestamos = new ArrayList<>();
        cargarDatos(); // Carga automática al iniciar 
    }

    // --- GESTIÓN DE DATOS ---
    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
        guardarDatos(); // Guardado automático
    }

    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo(); // Calculamos los intereses al registrarlo
        prestamos.add(p);
        guardarDatos();
    }

    public Cuenta[] getCuentas() {
        // Convertimos a arreglo estático para trabajar los algoritmos
        return cuentas.toArray(new Cuenta[0]);
    }
    
    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // --- PUNTO 4: ORDENAMIENTOS (QUICKSORT E INSERCION) ---

    // Requisito: Ordenar por Nombre usando QuickSort 
    public void ordenarPorNombreQuickSort() {
        if (cuentas.isEmpty()) return;
        Cuenta[] arr = getCuentas();
        quickSort(arr, 0, arr.length - 1);
        
        // Actualizamos la lista principal con el arreglo ordenado
        cuentas = new ArrayList<>(Arrays.asList(arr));
    }

    private void quickSort(Cuenta[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(Cuenta[] arr, int low, int high) {
        String pivot = arr[high].getNombreCliente();
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            // Comparamos cadenas (Nombres)
            if (arr[j].getNombreCliente().compareToIgnoreCase(pivot) < 0) {
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

    // Requisito: Ordenar por Inserción (por ejemplo, para categorías) [cite: 42]
    // Este método lo puedes usar antes de imprimir reportes por categoría
    public void ordenarPorNumeroInsercion() {
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

    // --- PUNTO 4: BÚSQUEDAS BINARIAS ---

    // Requisito: Búsqueda Binaria por NumCuenta [cite: 36]
    // NOTA: Para búsqueda binaria, la lista DEBE estar ordenada por ese criterio primero.
    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ordenarPorNumeroInsercion(); // Aseguramos orden
        Cuenta[] arr = getCuentas();
        
        int izquierda = 0, derecha = arr.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;

            if (arr[medio].getNumCuenta() == numCuenta)
                return arr[medio];

            if (arr[medio].getNumCuenta() < numCuenta)
                izquierda = medio + 1;
            else
                derecha = medio - 1;
        }
        return null; // No encontrado
    }
    
    // Requisito: Búsqueda Binaria por Nombre [cite: 37]
    public Cuenta buscarCuentaPorNombre(String nombre) {
        ordenarPorNombreQuickSort(); // Aseguramos orden
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

    // --- PUNTO A y B: CORTE MENSUAL (COMISIONES E INTERESES) ---
    public void aplicarCorteMensual() {
        // Recorremos todas las cuentas polimórficamente
        for (Cuenta c : cuentas) {
            // [cite: 11, 13, 20, 22] Se aplican comisiones e intereses
            c.comisiones(); 
            c.intereses();
        }
        guardarDatos(); // Guardamos los nuevos saldos
    }

    // --- PUNTO 4 (Numeral 4): PERSISTENCIA DE ARCHIVOS ---
    // Usamos Serialización para guardar los objetos completos en un archivo físico 
    private void guardarDatos() {
        try {
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS));
            oosC.writeObject(cuentas);
            oosC.close();
            
            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream(ARCHIVO_PRESTAMOS));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) {
            System.out.println("Error al guardar archivos: " + e.getMessage());
        }
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
        } catch (Exception e) {
            System.out.println("Error al cargar archivos o archivos vacíos.");
        }
    }
}