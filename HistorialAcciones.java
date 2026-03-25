import java.util.Stack;

public class HistorialAcciones {
    private Stack<Accion> acciones;
    private String dniCliente;

    public HistorialAcciones(String dniCliente) {
        this.acciones = new Stack<>();
        this.dniCliente = dniCliente;
    }

    public void registrarAccion(Accion accion) {
        this.acciones.push(accion);
    }

    public Accion deshacerUltimaAccion() {
        if (!acciones.isEmpty()) {
            return acciones.pop();
        } else {
            return null;
        }
    }

    public Stack<Accion> getAcciones() {
        return acciones;
    }

}