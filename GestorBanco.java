package Banco;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GestorBanco {
    // REQUISITOS: "Trabajar con estructuras estáticas" -> Usamos un arreglo fijo [].
    private Cuenta[] cuentas;
    // 'cantidadCuentas' sirve como un puntero para saber hasta dónde está lleno el arreglo.
    private int cantidadCuentas;
    private final int MAX_CUENTAS = 100; // Tope máximo del banco.

    // Para préstamos permite dinámicas, así que usamos ArrayList.
    private ArrayList<Prestamo> prestamos; // esto no deberia ir aqui por que ya esta en el banco

    // Nombres de los archivos físicos
    private final String ARCHIVO_CUENTAS = "cuentas.dat";
    private final String ARCHIVO_PRESTAMOS = "prestamos.dat";

    public GestorBanco() {
        // Inicializamos el arreglo vacío con 100 espacios
        cuentas = new Cuenta[MAX_CUENTAS];
        cantidadCuentas = 0;
        prestamos = new ArrayList<>();
        cargarDatos(); // Intentamos recuperar datos guardados anteriormente
    }

    // --- GESTIÓN DE CUENTAS (Altas, Bajas, Cambios) ---

    public void agregarCuenta(Cuenta c) {
        // Validación: Solo guardamos si hay espacio en el arreglo
        if (cantidadCuentas < MAX_CUENTAS) {
            cuentas[cantidadCuentas] = c; // Guardamos en la posición disponible
            cantidadCuentas++; // Aumentamos el contador
            guardarDatos(); // Guardamos en disco duro automáticamente
        } else {
            JOptionPane.showMessageDialog(null, "Error: El banco está lleno (Límite 100 cuentas).");
        }
    }

    // REQUISITOS: Opción Eliminar Cuenta
    public boolean eliminarCuenta(int numCuenta) {
        // 1. Buscar la cuenta en el arreglo
        for (int i = 0; i < cantidadCuentas; i++) {
            if (cuentas[i].getNumCuenta() == numCuenta) {
                // 2. Si la encontramos, hacemos el "recorrido a la izquierda"
                // Esto tapa el hueco moviendo todos los elementos siguientes una posición atrás.
                for (int j = i; j < cantidadCuentas - 1; j++) {
                    cuentas[j] = cuentas[j + 1];
                }
                cuentas[cantidadCuentas - 1] = null; // Limpiamos el último rastro
                cantidadCuentas--; // Ahora hay una cuenta menos
                guardarDatos();
                return true;
            }
        }
        return false; // No se encontró
    }

    // REQUISITOS: Opción Modificar Cuenta
    public boolean modificarCuenta(int numCuenta, String nuevoNombre) {
        Cuenta c = buscarCuentaPorNumero(numCuenta);
        if (c != null) {
            c.setNombreCliente(nuevoNombre); // Usamos el Setter
            guardarDatos();
            return true;
        }
        return false;
    }

    // --- GESTIÓN DE PRÉSTAMOS ---
    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo(); // Ejecutamos la matemática de intereses
        prestamos.add(p);     // Agregamos a la lista global del banco


        // Buscamos si el cliente tiene cuenta para agregarle el préstamo a su historial personal
        boolean vinculado = false;
        for(int i = 0; i < cantidadCuentas; i++) {
            if (cuentas[i].getNombreCliente().equalsIgnoreCase(p.getCliente())) {
                cuentas[i].getPrestamos().add(p); // Agregamos a SU lista personal
                vinculado = true;
                break;
            }
        }

        if(!vinculado) {
            JOptionPane.showMessageDialog(null, "Nota: El préstamo se creó, pero el cliente no tiene cuenta de ahorro asociada.");
        }
        guardarDatos();
    }

    // Metodo auxiliar para obtener solo las cuentas activas (sin los espacios nulos del arreglo)
    public Cuenta[] getCuentas() {
        Cuenta[] activas = new Cuenta[cantidadCuentas];
        for (int i = 0; i < cantidadCuentas; i++) {
            activas[i] = cuentas[i];
        }
        return activas;
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // --- ALGORITMOS DE ORDENAMIENTO ---

    // QuickSort: Algoritmo recursivo rápido para ordenar por nombre
    public void ordenarPorNombreQuickSort() {
        if (cantidadCuentas > 0) {
            quickSort(cuentas, 0, cantidadCuentas - 1);
        }
    }

    private void quickSort(Cuenta[] arr, int inicio, int fin) {
        if (inicio < fin) {
            int indicePivote = partition(arr, inicio, fin);
            quickSort(arr, inicio, indicePivote - 1); // Ordenar mitad izquierda
            quickSort(arr, indicePivote + 1, fin);    // Ordenar mitad derecha
        }
    }

    private int partition(Cuenta[] arr, int inicio, int fin) {
        String pivote = arr[fin].getNombreCliente();
        int i = (inicio - 1);
        for (int j = inicio; j < fin; j++) {
            // Compara alfabéticamente los nombres
            if (arr[j].getNombreCliente().compareToIgnoreCase(pivote) < 0) {
                i++;
                // Intercambio (Swap) clásico
                Cuenta temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Cuenta temp = arr[i + 1];
        arr[i + 1] = arr[fin];
        arr[fin] = temp;
        return i + 1;
    }

    // Inserción: Algoritmo simple para ordenar por número de cuenta
    public void ordenarPorNumeroInsercion() {
        for (int i = 1; i < cantidadCuentas; ++i) {
            Cuenta key = cuentas[i];
            int j = i - 1;
            // Mueve los elementos mayores a la derecha para hacer espacio
            while (j >= 0 && cuentas[j].getNumCuenta() > key.getNumCuenta()) {
                cuentas[j + 1] = cuentas[j];
                j = j - 1;
            }
            cuentas[j + 1] = key;
        }
    }

    // --- BÚSQUEDAS BINARIAS ---
    // La búsqueda binaria requiere que el arreglo esté ordenado primero.

    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ordenarPorNumeroInsercion(); // Paso 1: Ordenar

        int izquierda = 0, derecha = cantidadCuentas - 1;

        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;

            if (cuentas[medio].getNumCuenta() == numCuenta)
                return cuentas[medio]; // Encontrado

            if (cuentas[medio].getNumCuenta() < numCuenta)
                izquierda = medio + 1; // Buscar en la mitad derecha
            else
                derecha = medio - 1;   // Buscar en la mitad izquierda
        }
        return null; // No existe
    }

    public Cuenta buscarCuentaPorNombre(String nombre) {
        ordenarPorNombreQuickSort(); // Paso 1: Ordenar alfabéticamente

        int izquierda = 0, derecha = cantidadCuentas - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            // compareTo devuelve 0 si son iguales
            int res = nombre.compareToIgnoreCase(cuentas[medio].getNombreCliente());

            if (res == 0) return cuentas[medio];
            if (res > 0) izquierda = medio + 1;
            else derecha = medio - 1;
        }
        return null;
    }

    // --- CIERRE MENSUAL ---
    public void aplicarCorteMensual() {
        // Polimorfismo: No importa si es Ahorro o Corriente, ejecutamos sus métodos
        for (int i = 0; i < cantidadCuentas; i++) {
            cuentas[i].comisiones();
            cuentas[i].intereses();
        }
        guardarDatos();
    }

    // --- PERSISTENCIA (Archivos) ---
    // Serialización: Guarda el objeto completo en bytes.
    private void guardarDatos() {
        try {
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS));
            oosC.writeObject(cuentas);
            oosC.close();

            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream(ARCHIVO_PRESTAMOS));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) {
            // Ignoramos errores de escritura por simplicidad
        }
    }

    // Carga los datos al iniciar el programa
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try {
            File fC = new File(ARCHIVO_CUENTAS);
            if (fC.exists()) {
                ObjectInputStream oisC = new ObjectInputStream(new FileInputStream(fC));
                cuentas = (Cuenta[]) oisC.readObject();
                oisC.close();

                // IMPORTANTE: Al cargar el arreglo, debemos contar cuántos no son null
                // para restaurar la variable 'cantidadCuentas'.
                cantidadCuentas = 0;
                for (Cuenta c : cuentas) {
                    if (c != null) cantidadCuentas++;
                    else break;
                }
            }

            File fP = new File(ARCHIVO_PRESTAMOS);
            if (fP.exists()) {
                ObjectInputStream oisP = new ObjectInputStream(new FileInputStream(fP));
                prestamos = (ArrayList<Prestamo>) oisP.readObject();
                oisP.close();
            }
        } catch (Exception e) {
            // Si falla, iniciamos en blanco
        }
    }
}