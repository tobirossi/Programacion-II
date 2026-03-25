import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndicesClientes {
    private Map<String, List<String>> indicePorNombre;

    private TreeMap<Integer, List<String>> indicePorScoring;

    public IndicesClientes() {
        this.indicePorNombre = new HashMap<>();
        this.indicePorScoring = new TreeMap<>();
    }

    public void agregarCliente(Cliente cliente) {
        String dni = cliente.getDni();
        String nombre = cliente.getNombre();
        int scoring = cliente.getScoring();

        if (!indicePorNombre.containsKey(nombre)) {
            indicePorNombre.put(nombre, new ArrayList<>());
        }
        indicePorNombre.get(nombre).add(dni);

        if (!indicePorScoring.containsKey(scoring)) {
            indicePorScoring.put(scoring, new ArrayList<>());
        }
        indicePorScoring.get(scoring).add(dni);
    }

    public void removerCliente(Cliente cliente) {
        String dni = cliente.getDni();
        String nombre = cliente.getNombre();
        int scoring = cliente.getScoring();

        if (indicePorNombre.containsKey(nombre)) {
            indicePorNombre.get(nombre).remove(dni);
            if (indicePorNombre.get(nombre).isEmpty()) {
                indicePorNombre.remove(nombre);
            }
        }

        if (indicePorScoring.containsKey(scoring)) {
            indicePorScoring.get(scoring).remove(dni);
            if (indicePorScoring.get(scoring).isEmpty()) {
                indicePorScoring.remove(scoring);
            }
        }
    }

    public List<String> buscarPorNombre(String nombre) {
        return indicePorNombre.getOrDefault(nombre, new ArrayList<>());
    }

    public List<String> buscarPorScoring(int scoring) {
        return indicePorScoring.getOrDefault(scoring, new ArrayList<>());
    }
}
