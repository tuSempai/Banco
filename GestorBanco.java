package Banco;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

// CLASE PRINCIPAL: GESTOR BANCO

public class GestorBanco {
    // Usamos ArrayList porque son dinámicos (crecen según necesitemos)
    private ArrayList<Cuenta> cuentas;
    private ArrayList<Prestamo> prestamos;

    public GestorBanco() {
        cuentas = new ArrayList<>();
        prestamos = new ArrayList<>();
        cargarDatos(); // Importante: Al arrancar, recuperamos lo guardado anteriormente.
    }

    // --- ALTAS (REGISTRO) ---
    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
        guardarDatos();
    }

    public void agregarPrestamo(Prestamo p) {
        p.calcula_prestamo(); // Lógica de negocio: calculamos intereses antes de guardar.
        prestamos.add(p);
        guardarDatos();
    }

    // --- MODIFICAR ---
    // Hacemos un recorrido simple para encontrar la cuenta por su ID único.
    public boolean modificarNombreCliente(int numCuenta, String nuevoNombre) {
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                c.nombreCliente = nuevoNombre; // Actualizamos el atributo
                guardarDatos(); // Guardamos el cambio en el archivo físico
                return true;
            }
        }
        return false; // No se encontró
    }

    // --- CAJERO (Lógica de Negocio) ---
    public String realizarMovimiento(int numCuenta, double monto, String tipoMovimiento) {
        // 1. Primero buscamos la cuenta
        Cuenta cuentaEncontrada = null;
        for (Cuenta c : cuentas) {
            if (c.getNumCuenta() == numCuenta) {
                cuentaEncontrada = c;
                break;
            }
        }

        if (cuentaEncontrada == null) return "Error: La cuenta no existe.";

        // 2. Aplicamos la operación según el tipo
        if (tipoMovimiento.equals("DEPOSITAR")) {
            cuentaEncontrada.abonar(monto); // Método de la clase padre Cuenta
        } else if (tipoMovimiento.equals("RETIRAR")) {
            // Validación importante: No dejar saldo negativo
            if (cuentaEncontrada.getSaldo() >= monto) {
                cuentaEncontrada.cargar(monto);
            } else {
                return "Error: Fondos insuficientes.";
            }
        }
        guardarDatos(); // Actualizamos el archivo con el nuevo saldo
        return "Éxito: Movimiento realizado. Nuevo Saldo: $" + cuentaEncontrada.getSaldo();
    }

    // Metodo auxiliar para validar si podemos dar un préstamo a alguien
    public boolean existeCliente(String nombreCliente) {
        for (Cuenta c : cuentas) {
            if (c.getNombreCliente().equalsIgnoreCase(nombreCliente)) return true;
        }
        return false;
    }

    // --- REPORTE FILTRADO DE PRÉSTAMOS ---
    public ArrayList<Prestamo> obtenerPrestamosPorCliente(String nombreCliente) {
        ArrayList<Prestamo> listaFiltrada = new ArrayList<>();
        for (Prestamo p : prestamos) {
            // Comparamos ignorando mayúsculas/minúsculas para ser amigables con el usuario
            if (p.getCliente().equalsIgnoreCase(nombreCliente)) {
                listaFiltrada.add(p);
            }
        }
        return listaFiltrada;
    }

    // --- GETTERS ---
    // Convertimos la lista a Arreglo ([]) porque los algoritmos de ordenamiento
    // del temario suelen trabajarse mejor con arreglos estáticos.
    public Cuenta[] getCuentas() {
        return cuentas.toArray(new Cuenta[0]);
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    // ALGORITMOS DE ORDENAMIENTO

    // --- 1. QUICKSORT (Recursivo) ---
    // Soporta ordenar por Nombre o por Número, Ascendente o Descendente.
    public void ejecutarQuickSort(boolean porNombre, boolean ascendente) {
        if (cuentas.isEmpty()) return;
        Cuenta[] arr = getCuentas();
        // Llamada inicial al metodo recursivo
        quickSortRecursivo(arr, 0, arr.length - 1, porNombre, ascendente);
        // Regresamos los datos ordenados a la lista principal
        cuentas = new ArrayList<>(Arrays.asList(arr));
    }

    private void quickSortRecursivo(Cuenta[] arr, int low, int high, boolean porNombre, boolean ascendente) {
        if (low < high) {
            // Obtenemos el índice de partición (donde se divide el arreglo)
            int pi = partition(arr, low, high, porNombre, ascendente);

            // Ordenamos recursivamente las dos mitades (izquierda y derecha del pivote)
            quickSortRecursivo(arr, low, pi - 1, porNombre, ascendente);
            quickSortRecursivo(arr, pi + 1, high, porNombre, ascendente);
        }
    }

    // Metodo Partition: Coloca los elementos menores al pivote a la izquierda y mayores a la derecha
    private int partition(Cuenta[] arr, int low, int high, boolean porNombre, boolean ascendente) {
        Cuenta pivot = arr[high]; // Tomamos el último elemento como pivote
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            boolean condicion;

            // Lógica dinámica para saber qué comparar
            if (porNombre) {
                // Comparamos Strings (Nombres)
                int res = arr[j].getNombreCliente().compareToIgnoreCase(pivot.getNombreCliente());
                condicion = ascendente ? (res < 0) : (res > 0);
            } else {
                // Comparamos Enteros (Números de cuenta)
                int res = Integer.compare(arr[j].getNumCuenta(), pivot.getNumCuenta());
                condicion = ascendente ? (res < 0) : (res > 0);
            }

            // Si se cumple la condición, hacemos el intercambio (swap)
            if (condicion) {
                i++;
                Cuenta temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        // Intercambio final del pivote a su posición correcta
        Cuenta temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // --- 2. ORDENAMIENTO POR INSERCIÓN ---
    // Usamos Inserción para los reportes por categoría. Filtramos primero
    // (Ahorro o Corriente) y luego ordenamos esa sub-lista."
    public ArrayList<Cuenta> obtenerReportePorCategoria(String tipoClase, boolean ascendente) {
        ArrayList<Cuenta> filtradas = new ArrayList<>();

        // Paso 1: Filtrar (Separar Ahorro de Corriente) [cite: 40]
        for (Cuenta c : cuentas) {
            if (tipoClase.equals("AHORRO") && c instanceof CuentaAhorro) filtradas.add(c);
            else if (tipoClase.equals("CORRIENTE") && c instanceof CuentaCorriente) filtradas.add(c);
        }

        // Paso 2: Algoritmo de Inserción
        Cuenta[] arr = filtradas.toArray(new Cuenta[0]);
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            Cuenta key = arr[i];
            int j = i - 1;

            // Movemos los elementos que son mayores que la 'key' una posición adelante
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


    // BÚSQUEDAS

    // --- BÚSQUEDA BINARIA POR NÚMERO ---
    // LA LISTA DEBE ESTAR ORDENADA.
    // Por eso llamamos a QuickSort antes de buscar."
    public Cuenta buscarCuentaPorNumero(int numCuenta) {
        ejecutarQuickSort(false, true); // Ordenamos por número ascendente [cite: 36]
        Cuenta[] arr = getCuentas();

        // Algoritmo clásico de Búsqueda Binaria (Divide y vencerás)
        int izquierda = 0, derecha = arr.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;

            if (arr[medio].getNumCuenta() == numCuenta) return arr[medio]; // Encontrado

            if (arr[medio].getNumCuenta() < numCuenta) izquierda = medio + 1; // Buscar en mitad derecha
            else derecha = medio - 1; // Buscar en mitad izquierda
        }
        return null; // No encontrado
    }

    // --- BÚSQUEDA BINARIA POR NOMBRE ---
    public Cuenta buscarCuentaPorNombre(String nombre) {
        ejecutarQuickSort(true, true); // Ordenamos por nombre alfabéticamente [cite: 37]
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

    // --- CIERRE DE MES (POLIMORFISMO) ---
    // Explica: "Aquí usamos Polimorfismo. Llamamos a comisiones() e intereses()
    // y Java decide automáticamente qué versión ejecutar (Ahorro o Corriente)."
    public void aplicarCorteMensual() {
        for (Cuenta c : cuentas) {
            c.comisiones();
            c.intereses();
        }
        guardarDatos();
    }

    // PERSISTENCIA
    // Usamos Serialización de Java. Esto guarda el objeto completo en binario
    // en lugar de guardar texto plano, lo cual facilita recuperar la estructura de datos.
    private void guardarDatos() {
        try {
            // Guardamos Cuentas
            ObjectOutputStream oosC = new ObjectOutputStream(new FileOutputStream("cuentas.dat"));
            oosC.writeObject(cuentas);
            oosC.close();

            // Guardamos Préstamos
            ObjectOutputStream oosP = new ObjectOutputStream(new FileOutputStream("prestamos.dat"));
            oosP.writeObject(prestamos);
            oosP.close();
        } catch (IOException e) {
            System.out.println("Error guardar: " + e.getMessage());
        }
    }

    // Carga los datos al iniciar el programa
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
            // Si falla (ej. primera vez que corre), iniciamos con listas vacías.
        }
    }
}