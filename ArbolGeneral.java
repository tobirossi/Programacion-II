import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class ArbolGeneral {
    private NodoGeneral raiz;
    private Map<String, NodoGeneral> nodosPorDni;
    private int tamaño;

    public ArbolGeneral() {
        this.raiz = null;
        this.nodosPorDni = new HashMap<>();
        this.tamaño = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public boolean agregarCliente(Cliente cliente, String dniPadre) {
        if (cliente == null) {
            return false;
        }

        // Si el árbol está vacío, crear raíz
        if (estaVacio()) {
            raiz = new NodoGeneral(cliente);
            nodosPorDni.put(cliente.getDni(), raiz);
            tamaño++;
            return true;
        }

        if (nodosPorDni.containsKey(cliente.getDni())) {
            return false;
        }

        NodoGeneral nodoPadre = nodosPorDni.get(dniPadre);
        if (nodoPadre == null) {
            return false;
        }

        NodoGeneral nuevoNodo = new NodoGeneral(cliente);
        nodoPadre.agregarHijo(nuevoNodo);
        nodosPorDni.put(cliente.getDni(), nuevoNodo);
        tamaño++;

        return true;
    }


    public NodoGeneral buscarNodo(String dni) {
        return nodosPorDni.get(dni);
    }

    public List<Cliente> obtenerClientesNivelN(int nivel) {
        List<Cliente> clientesEnNivel = new ArrayList<>();
        if (estaVacio() || nivel < 0) {
            return clientesEnNivel;
        }

        Queue<NodoGeneral> cola = new LinkedList<>();
        Queue<Integer> niveles = new LinkedList<>();

        cola.offer(raiz);
        niveles.offer(0);

        while (!cola.isEmpty()) {
            NodoGeneral nodoActual = cola.poll();
            int nivelActual = niveles.poll();

            if (nivelActual == nivel) {
                clientesEnNivel.add(nodoActual.getCliente());
            } else if (nivelActual < nivel) {
                for (NodoGeneral hijo : nodoActual.getHijos()) {
                    cola.offer(hijo);
                    niveles.offer(nivelActual + 1);
                }
            }
        }

        return clientesEnNivel;
    }

    public int obtenerProfundidadMaxima(String dni) {
        NodoGeneral nodo = buscarNodo(dni);
        if (nodo == null) {
            return -1;
        }

        return calcularProfundidadMaximaDesdeNodo(nodo);
    }

    private int calcularProfundidadMaximaDesdeNodo(NodoGeneral nodo) {
        if (nodo.esHoja()) {
            return 0;
        }

        int profundidadMaxima = 0;
        for (NodoGeneral hijo : nodo.getHijos()) {
            int profundidadHijo = 1 + calcularProfundidadMaximaDesdeNodo(hijo);
            if (profundidadHijo > profundidadMaxima) {
                profundidadMaxima = profundidadHijo;
            }
        }

        return profundidadMaxima;
    }

    private void recorridoPreOrdenRecursivo(NodoGeneral nodo, List<Cliente> resultado) {
        resultado.add(nodo.getCliente());
        for (NodoGeneral hijo : nodo.getHijos()) {
            recorridoPreOrdenRecursivo(hijo, resultado);
        }
    }

    public void construirJerarquiaPorScoring(List<Cliente> clientes) {
        if (clientes == null || clientes.isEmpty()) {
            return;
        }

        raiz = null;
        nodosPorDni.clear();
        tamaño = 0;

        clientes.sort((c1, c2) -> Integer.compare(c2.getScoring(), c1.getScoring()));

        Cliente clienteRaiz = clientes.get(0);
        raiz = new NodoGeneral(clienteRaiz);
        nodosPorDni.put(clienteRaiz.getDni(), raiz);
        tamaño++;

        for (int i = 1; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);

            String dniPadre = encontrarMejorPadre(cliente, clientes.subList(0, i));

            if (dniPadre != null) {
                agregarCliente(cliente, dniPadre);
            }
        }
    }

    private String encontrarMejorPadre(Cliente cliente, List<Cliente> clientesSuperiores) {
        Cliente mejorPadre = null;
        int menorDiferencia = Integer.MAX_VALUE;

        for (Cliente candidato : clientesSuperiores) {
            if (candidato.getScoring() > cliente.getScoring()) {
                int diferencia = candidato.getScoring() - cliente.getScoring();
                if (diferencia < menorDiferencia) {
                    menorDiferencia = diferencia;
                    mejorPadre = candidato;
                }
            }
        }

        return mejorPadre != null ? mejorPadre.getDni() : null;
    }


}
