import java.util.ArrayList;
import java.util.List;


public class NodoGeneral {
    private Cliente cliente;
    private List<NodoGeneral> hijos;
    private NodoGeneral padre;

    public NodoGeneral(Cliente cliente) {
        this.cliente = cliente;
        this.hijos = new ArrayList<>();
        this.padre = null;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<NodoGeneral> getHijos() {
        return hijos;
    }

    public void setPadre(NodoGeneral padre) {
        this.padre = padre;
    }

    // Operaciones del nodo
    public void agregarHijo(NodoGeneral hijo) {
        if (hijo != null && !hijos.contains(hijo)) {
            hijos.add(hijo);
            hijo.setPadre(this);
        }
    }


    public boolean esHoja() {
        return hijos.isEmpty();
    }

    public boolean esRaiz() {
        return padre == null;
    }

    public int calcularProfundidad() {
        if (esRaiz()) {
            return 0;
        }
        return 1 + padre.calcularProfundidad();
    }

    public int calcularAltura() {
        if (esHoja()) {
            return 0;
        }

        int alturaMaxima = 0;
        for (NodoGeneral hijo : hijos) {
            int alturaHijo = hijo.calcularAltura();
            if (alturaHijo > alturaMaxima) {
                alturaMaxima = alturaHijo;
            }
        }
        return 1 + alturaMaxima;
    }

}
