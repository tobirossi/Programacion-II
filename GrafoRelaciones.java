import java.util.*;

public class GrafoRelaciones {
    private Map<String, Set<String>> relaciones;

    public GrafoRelaciones() {
        this.relaciones = new HashMap<>();
    }

    public void agregarRelacion(String dni1, String dni2) {
        if (dni1.equals(dni2)) {
            return; // No se puede ser amigo de uno mismo
        }

        if (!relaciones.containsKey(dni1)) {
            relaciones.put(dni1, new HashSet<>());
        }
        if (!relaciones.containsKey(dni2)) {
            relaciones.put(dni2, new HashSet<>());
        }

        // Agregar relación bidireccional
        relaciones.get(dni1).add(dni2);
        relaciones.get(dni2).add(dni1);
    }

    public void eliminarRelacion(String dni1, String dni2) {
        if (relaciones.containsKey(dni1)) {
            relaciones.get(dni1).remove(dni2);
        }
        if (relaciones.containsKey(dni2)) {
            relaciones.get(dni2).remove(dni1);
        }
    }

    // Verificar si existe una relación
    public boolean existeRelacion(String dni1, String dni2) {
        return relaciones.containsKey(dni1) && relaciones.get(dni1).contains(dni2);
    }

    // Obtener todas las relaciones de un cliente (O(1))
    public Set<String> obtenerRelaciones(String dni) {
        return relaciones.getOrDefault(dni, new HashSet<>());
    }

    // Calcular la distancia entre dos clientes (BFS)
    public int calcularDistancia(String dniOrigen, String dniDestino) {
        if (dniOrigen.equals(dniDestino)) {
            return 0;
        }

        if (!relaciones.containsKey(dniOrigen) || !relaciones.containsKey(dniDestino)) {
            return -1;
        }

        // BFS para encontrar el camino más corto
        Queue<String> cola = new LinkedList<>();
        Map<String, Integer> distancias = new HashMap<>();

        cola.offer(dniOrigen);
        distancias.put(dniOrigen, 0);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            int distanciaActual = distancias.get(actual);

            if (actual.equals(dniDestino)) {
                return distanciaActual;
            }

            for (String vecino : relaciones.getOrDefault(actual, new HashSet<>())) {
                if (!distancias.containsKey(vecino)) {
                    cola.offer(vecino);
                    distancias.put(vecino, distanciaActual + 1);
                }
            }
        }

        return -1; // No hay camino entre los clientes
    }

    public void agregarCliente(String dni) {
        if (!relaciones.containsKey(dni)) {
            relaciones.put(dni, new HashSet<>());
        }
    }

    public void eliminarCliente(String dni) {
        if (relaciones.containsKey(dni)) {
            Set<String> amigos = new HashSet<>(relaciones.get(dni));
            for (String amigo : amigos) {
                eliminarRelacion(dni, amigo);
            }
            relaciones.remove(dni);
        }
    }
}
