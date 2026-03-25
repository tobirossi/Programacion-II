import java.util.ArrayList;
import java.util.List;


public class ArbolBinarioBusqueda {
    private NodoABB raiz;
    private int tamaño;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
        this.tamaño = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }


    public boolean insertar(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        if (estaVacio()) {
            raiz = new NodoABB(cliente);
            tamaño++;
            return true;
        }

        return insertarRecursivo(raiz, cliente);
    }

    private boolean insertarRecursivo(NodoABB nodo, Cliente cliente) {
        int scoringCliente = cliente.getScoring();
        int scoringNodo = nodo.getScoring();

        if (scoringCliente < scoringNodo) {
            if (nodo.getHijoIzquierdo() == null) {
                nodo.setHijoIzquierdo(new NodoABB(cliente));
                tamaño++;
                return true;
            } else {
                return insertarRecursivo(nodo.getHijoIzquierdo(), cliente);
            }
        } else if (scoringCliente > scoringNodo) {
            if (nodo.getHijoDerecho() == null) {
                nodo.setHijoDerecho(new NodoABB(cliente));
                tamaño++;
                return true;
            } else {
                return insertarRecursivo(nodo.getHijoDerecho(), cliente);
            }
        } else {
            // Scoring igual - agregar al subárbol derecho (permitir duplicados)
            if (nodo.getHijoDerecho() == null) {
                nodo.setHijoDerecho(new NodoABB(cliente));
                tamaño++;
                return true;
            } else {
                return insertarRecursivo(nodo.getHijoDerecho(), cliente);
            }
        }
    }


    public List<Cliente> buscarPorScoring(int scoring) {
        List<Cliente> resultado = new ArrayList<>();
        buscarPorScoringRecursivo(raiz, scoring, resultado);
        return resultado;
    }

    private void buscarPorScoringRecursivo(NodoABB nodo, int scoring, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }

        int scoringNodo = nodo.getScoring();

        if (scoring == scoringNodo) {
            resultado.add(nodo.getCliente());
            // Buscar en ambos subárboles por si hay duplicados
            buscarPorScoringRecursivo(nodo.getHijoIzquierdo(), scoring, resultado);
            buscarPorScoringRecursivo(nodo.getHijoDerecho(), scoring, resultado);
        } else if (scoring < scoringNodo) {
            buscarPorScoringRecursivo(nodo.getHijoIzquierdo(), scoring, resultado);
        } else {
            buscarPorScoringRecursivo(nodo.getHijoDerecho(), scoring, resultado);
        }
    }

    public Cliente obtenerClienteMayorScoring() {
        if (estaVacio()) {
            return null;
        }

        NodoABB nodoMayor = encontrarNodoMayor(raiz);
        return nodoMayor.getCliente();
    }

    private NodoABB encontrarNodoMayor(NodoABB nodo) {
        while (nodo.getHijoDerecho() != null) {
            nodo = nodo.getHijoDerecho();
        }
        return nodo;
    }


    public List<Cliente> listarClientesRangoScoring(int min, int max) {
        List<Cliente> resultado = new ArrayList<>();
        if (min > max) {
            return resultado;
        }

        listarRangoRecursivo(raiz, min, max, resultado);
        return resultado;
    }

    private void listarRangoRecursivo(NodoABB nodo, int min, int max, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }

        int scoringNodo = nodo.getScoring();

        // Si el scoring del nodo está en el rango, agregarlo
        if (scoringNodo >= min && scoringNodo <= max) {
            resultado.add(nodo.getCliente());
        }

        if (scoringNodo > min) {
            listarRangoRecursivo(nodo.getHijoIzquierdo(), min, max, resultado);
        }

        if (scoringNodo < max) {
            listarRangoRecursivo(nodo.getHijoDerecho(), min, max, resultado);
        }
    }

    private void obtenerMayorScoringRecursivo(NodoABB nodo, int scoringMinimo, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }

        int scoringNodo = nodo.getScoring();

        if (scoringNodo > scoringMinimo) {
            resultado.add(nodo.getCliente());
            obtenerMayorScoringRecursivo(nodo.getHijoIzquierdo(), scoringMinimo, resultado);
            obtenerMayorScoringRecursivo(nodo.getHijoDerecho(), scoringMinimo, resultado);
        } else {
            obtenerMayorScoringRecursivo(nodo.getHijoDerecho(), scoringMinimo, resultado);
        }
    }


    public boolean eliminar(String dni) {
        NodoABB nodoAEliminar = buscarNodoPorDni(raiz, dni);
        if (nodoAEliminar == null) {
            return false;
        }

        raiz = eliminarNodo(raiz, nodoAEliminar.getScoring(), dni);
        tamaño--;
        return true;
    }

    private NodoABB buscarNodoPorDni(NodoABB nodo, String dni) {
        if (nodo == null) {
            return null;
        }

        if (nodo.getCliente().getDni().equals(dni)) {
            return nodo;
        }

        NodoABB encontrado = buscarNodoPorDni(nodo.getHijoIzquierdo(), dni);
        if (encontrado != null) {
            return encontrado;
        }

        return buscarNodoPorDni(nodo.getHijoDerecho(), dni);
    }

    private NodoABB eliminarNodo(NodoABB nodo, int scoring, String dni) {
        if (nodo == null) {
            return null;
        }

        if (scoring < nodo.getScoring()) {
            nodo.setHijoIzquierdo(eliminarNodo(nodo.getHijoIzquierdo(), scoring, dni));
        } else if (scoring > nodo.getScoring()) {
            nodo.setHijoDerecho(eliminarNodo(nodo.getHijoDerecho(), scoring, dni));
        } else {
            if (nodo.getCliente().getDni().equals(dni)) {
                if (nodo.getHijoIzquierdo() == null) {
                    return nodo.getHijoDerecho();
                } else if (nodo.getHijoDerecho() == null) {
                    return nodo.getHijoIzquierdo();
                } else {
                    NodoABB sucesor = nodo.encontrarSucesor();
                    nodo.setCliente(sucesor.getCliente());
                    nodo.setHijoDerecho(eliminarNodo(nodo.getHijoDerecho(),
                            sucesor.getScoring(),
                            sucesor.getCliente().getDni()));
                }
            } else {
                nodo.setHijoIzquierdo(eliminarNodo(nodo.getHijoIzquierdo(), scoring, dni));
                nodo.setHijoDerecho(eliminarNodo(nodo.getHijoDerecho(), scoring, dni));
            }
        }

        return nodo;
    }

    private void recorridoInordenRecursivo(NodoABB nodo, List<Cliente> resultado) {
        if (nodo != null) {
            recorridoInordenRecursivo(nodo.getHijoIzquierdo(), resultado);
            resultado.add(nodo.getCliente());
            recorridoInordenRecursivo(nodo.getHijoDerecho(), resultado);
        }
    }

    private void recorridoPreordenRecursivo(NodoABB nodo, List<Cliente> resultado) {
        if (nodo != null) {
            resultado.add(nodo.getCliente());
            recorridoPreordenRecursivo(nodo.getHijoIzquierdo(), resultado);
            recorridoPreordenRecursivo(nodo.getHijoDerecho(), resultado);
        }
    }


    private int calcularAlturaRecursivo(NodoABB nodo) {
        if (nodo == null) {
            return -1;
        }

        int alturaIzq = calcularAlturaRecursivo(nodo.getHijoIzquierdo());
        int alturaDer = calcularAlturaRecursivo(nodo.getHijoDerecho());

        return 1 + Math.max(alturaIzq, alturaDer);
    }

    public void construirDesdeClientes(List<Cliente> clientes) {
        // Limpiar árbol actual
        raiz = null;
        tamaño = 0;

        // Insertar cada cliente
        for (Cliente cliente : clientes) {
            insertar(cliente);
        }
    }

    private void toStringRecursivo(NodoABB nodo, String prefijo, boolean esUltimo, StringBuilder sb) {
        if (nodo != null) {
            sb.append(prefijo);
            sb.append(esUltimo ? "└── " : "├── ");
            sb.append(nodo.getCliente().getNombre())
                    .append(" (Scoring: ").append(nodo.getScoring()).append(")\n");

            boolean tieneHijoIzq = nodo.getHijoIzquierdo() != null;
            boolean tieneHijoDer = nodo.getHijoDerecho() != null;

            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");

            if (tieneHijoIzq) {
                toStringRecursivo(nodo.getHijoIzquierdo(), nuevoPrefijo, !tieneHijoDer, sb);
            }
            if (tieneHijoDer) {
                toStringRecursivo(nodo.getHijoDerecho(), nuevoPrefijo, true, sb);
            }
        }
    }
}
