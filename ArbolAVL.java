import java.util.ArrayList;
import java.util.List;


public class ArbolAVL {
    private NodoABB raiz;
    private int tamaño;

    public ArbolAVL() {
        this.raiz = null;
        this.tamaño = 0;
    }


    public boolean estaVacio() {
        return raiz == null;
    }


    private NodoABB rotacionDerecha(NodoABB nodo) {
        NodoABB nuevaRaiz = nodo.getHijoIzquierdo();
        NodoABB subArbolDerecho = nuevaRaiz.getHijoDerecho();

        nuevaRaiz.setHijoDerecho(nodo);
        nodo.setHijoIzquierdo(subArbolDerecho);

        nodo.actualizarAltura();
        nuevaRaiz.actualizarAltura();

        return nuevaRaiz;
    }


    private NodoABB rotacionIzquierda(NodoABB nodo) {
        NodoABB nuevaRaiz = nodo.getHijoDerecho();
        NodoABB subArbolIzquierdo = nuevaRaiz.getHijoIzquierdo();

        nuevaRaiz.setHijoIzquierdo(nodo);
        nodo.setHijoDerecho(subArbolIzquierdo);

        nodo.actualizarAltura();
        nuevaRaiz.actualizarAltura();

        return nuevaRaiz;
    }


    private NodoABB rotacionIzquierdaDerecha(NodoABB nodo) {
        nodo.setHijoIzquierdo(rotacionIzquierda(nodo.getHijoIzquierdo()));
        return rotacionDerecha(nodo);
    }


    private NodoABB rotacionDerechaIzquierda(NodoABB nodo) {
        nodo.setHijoDerecho(rotacionDerecha(nodo.getHijoDerecho()));
        return rotacionIzquierda(nodo);
    }


    public boolean insertar(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        int tamañoAnterior = tamaño;
        raiz = insertarRecursivo(raiz, cliente);

        return tamaño > tamañoAnterior;
    }

    private NodoABB insertarRecursivo(NodoABB nodo, Cliente cliente) {
        if (nodo == null) {
            tamaño++;
            return new NodoABB(cliente);
        }

        int scoringCliente = cliente.getScoring();
        int scoringNodo = nodo.getScoring();

        if (scoringCliente < scoringNodo) {
            nodo.setHijoIzquierdo(insertarRecursivo(nodo.getHijoIzquierdo(), cliente));
        } else if (scoringCliente > scoringNodo) {
            nodo.setHijoDerecho(insertarRecursivo(nodo.getHijoDerecho(), cliente));
        } else {
            nodo.setHijoDerecho(insertarRecursivo(nodo.getHijoDerecho(), cliente));
        }

        nodo.actualizarAltura();

        int factorBalance = nodo.calcularFactorBalance();


        if (factorBalance > 1 && scoringCliente < nodo.getHijoIzquierdo().getScoring()) {
            return rotacionDerecha(nodo);
        }

        if (factorBalance < -1 && scoringCliente >= nodo.getHijoDerecho().getScoring()) {
            return rotacionIzquierda(nodo);
        }

        if (factorBalance > 1 && scoringCliente >= nodo.getHijoIzquierdo().getScoring()) {
            return rotacionIzquierdaDerecha(nodo);
        }

        if (factorBalance < -1 && scoringCliente < nodo.getHijoDerecho().getScoring()) {
            return rotacionDerechaIzquierda(nodo);
        }

        return nodo;
    }


    public boolean eliminar(String dni) {
        if (estaVacio()) {
            return false;
        }

        int tamañoAnterior = tamaño;
        raiz = eliminarRecursivo(raiz, dni);

        return tamaño < tamañoAnterior;
    }

    private NodoABB eliminarRecursivo(NodoABB nodo, String dni) {
        if (nodo == null) {
            return null;
        }

        if (nodo.getCliente().getDni().equals(dni)) {
            tamaño--;

            if (nodo.getHijoIzquierdo() == null || nodo.getHijoDerecho() == null) {
                NodoABB temp = nodo.getHijoIzquierdo() != null ?
                        nodo.getHijoIzquierdo() : nodo.getHijoDerecho();

                if (temp == null) {
                    // Sin hijos
                    return null;
                } else {
                    // Un hijo
                    return temp;
                }
            } else {
                NodoABB sucesor = nodo.encontrarSucesor();
                nodo.setCliente(sucesor.getCliente());
                nodo.setHijoDerecho(eliminarRecursivo(nodo.getHijoDerecho(),
                        sucesor.getCliente().getDni()));
                tamaño++;
            }
        } else {
            // Buscar en subarboles
            nodo.setHijoIzquierdo(eliminarRecursivo(nodo.getHijoIzquierdo(), dni));
            nodo.setHijoDerecho(eliminarRecursivo(nodo.getHijoDerecho(), dni));
        }

        nodo.actualizarAltura();

        int factorBalance = nodo.calcularFactorBalance();


        if (factorBalance > 1 && nodo.getHijoIzquierdo().calcularFactorBalance() >= 0) {
            return rotacionDerecha(nodo);
        }

        if (factorBalance > 1 && nodo.getHijoIzquierdo().calcularFactorBalance() < 0) {
            return rotacionIzquierdaDerecha(nodo);
        }

        if (factorBalance < -1 && nodo.getHijoDerecho().calcularFactorBalance() <= 0) {
            return rotacionIzquierda(nodo);
        }

        if (factorBalance < -1 && nodo.getHijoDerecho().calcularFactorBalance() > 0) {
            return rotacionDerechaIzquierda(nodo);
        }

        return nodo;
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
            // Buscar duplicados en ambos subárboles
            buscarPorScoringRecursivo(nodo.getHijoIzquierdo(), scoring, resultado);
            buscarPorScoringRecursivo(nodo.getHijoDerecho(), scoring, resultado);
        } else if (scoring < scoringNodo) {
            buscarPorScoringRecursivo(nodo.getHijoIzquierdo(), scoring, resultado);
        } else {
            buscarPorScoringRecursivo(nodo.getHijoDerecho(), scoring, resultado);
        }
    }


    private void listarRangoRecursivo(NodoABB nodo, int min, int max, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }

        int scoringNodo = nodo.getScoring();

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


    public List<Cliente> obtenerClientesMayorScoring(int scoringMinimo) {
        List<Cliente> resultado = new ArrayList<>();
        obtenerMayorScoringRecursivo(raiz, scoringMinimo, resultado);
        return resultado;
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


    private boolean verificarBalanceRecursivo(NodoABB nodo) {
        if (nodo == null) {
            return true;
        }

        int factorBalance = nodo.calcularFactorBalance();

        if (Math.abs(factorBalance) > 1) {
            return false;
        }

        return verificarBalanceRecursivo(nodo.getHijoIzquierdo()) &&
                verificarBalanceRecursivo(nodo.getHijoDerecho());
    }



    private void recorridoInordenRecursivo(NodoABB nodo, List<Cliente> resultado) {
        if (nodo != null) {
            recorridoInordenRecursivo(nodo.getHijoIzquierdo(), resultado);
            resultado.add(nodo.getCliente());
            recorridoInordenRecursivo(nodo.getHijoDerecho(), resultado);
        }
    }


    public void construirDesdeClientes(List<Cliente> clientes) {
        raiz = null;
        tamaño = 0;

        // Insertar cada cliente (se auto-balancea)
        for (Cliente cliente : clientes) {
            insertar(cliente);
        }
    }

    private void toStringRecursivo(NodoABB nodo, String prefijo, boolean esUltimo, StringBuilder sb) {
        if (nodo != null) {
            sb.append(prefijo);
            sb.append(esUltimo ? "└── " : "├── ");
            sb.append(nodo.getCliente().getNombre())
                    .append(" (S:").append(nodo.getScoring())
                    .append(", A:").append(nodo.getAltura())
                    .append(", FB:").append(nodo.calcularFactorBalance()).append(")\n");

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
