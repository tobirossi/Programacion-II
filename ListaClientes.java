import java.util.List;
import java.util.ArrayList;

public class ListaClientes {
    private List<Cliente> clientes;
    private List<Relacion> relaciones; // Nueva estructura para almacenar relaciones

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }


    public static class Relacion {
        private String dni1;
        private String dni2;


        public String getDni1() {
            return dni1;
        }

        public String getDni2() {
            return dni2;
        }
    }
}
