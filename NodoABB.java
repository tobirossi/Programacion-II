
public class NodoABB {
    private Cliente cliente;
    private NodoABB hijoIzquierdo;
    private NodoABB hijoDerecho;
    private NodoABB padre;
    private int altura; // Para el AVL

    public NodoABB(Cliente cliente) {
        this.cliente = cliente;
        this.hijoIzquierdo = null;
        this.hijoDerecho = null;
        this.padre = null;
        this.altura = 1; // Altura inicial para AVL
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public NodoABB getHijoIzquierdo() {
        return hijoIzquierdo;
    }

    public void setHijoIzquierdo(NodoABB hijoIzquierdo) {
        this.hijoIzquierdo = hijoIzquierdo;
        if (hijoIzquierdo != null) {
            hijoIzquierdo.setPadre(this);
        }
    }

    public NodoABB getHijoDerecho() {
        return hijoDerecho;
    }

    public void setHijoDerecho(NodoABB hijoDerecho) {
        this.hijoDerecho = hijoDerecho;
        if (hijoDerecho != null) {
            hijoDerecho.setPadre(this);
        }
    }


    public void setPadre(NodoABB padre) {
        this.padre = padre;
    }

    public int getAltura() {
        return altura;
    }


    // Operaciones del nodo
    public int getScoring() {
        return cliente != null ? cliente.getScoring() : 0;
    }

    public boolean esRaiz() {
        return padre == null;
    }


    // Metodos para AVL
    public void actualizarAltura() {
        int alturaIzq = hijoIzquierdo != null ? hijoIzquierdo.getAltura() : 0;
        int alturaDer = hijoDerecho != null ? hijoDerecho.getAltura() : 0;
        this.altura = 1 + Math.max(alturaIzq, alturaDer);
    }

    public int calcularFactorBalance() {
        int alturaIzq = hijoIzquierdo != null ? hijoIzquierdo.getAltura() : 0;
        int alturaDer = hijoDerecho != null ? hijoDerecho.getAltura() : 0;
        return alturaIzq - alturaDer;
    }

    public int calcularProfundidad() {
        if (esRaiz()) {
            return 0;
        }
        return 1 + padre.calcularProfundidad();
    }


    public NodoABB encontrarSucesor() {
        if (hijoDerecho == null) {
            return null;
        }

        NodoABB sucesor = hijoDerecho;
        while (sucesor.getHijoIzquierdo() != null) {
            sucesor = sucesor.getHijoIzquierdo();
        }
        return sucesor;
    }

}
