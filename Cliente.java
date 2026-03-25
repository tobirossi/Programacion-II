import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cliente {

    private String dni;
    private String nombre;
    private int scoring;
    private List<String> siguiendo;
    private Set<String> amistades; // Nueva estructura para relaciones bidireccionales

    public Cliente(String dni, String nombre, int scoring) {
        if (!validarDNI(dni)) {
            throw new IllegalArgumentException("DNI inválido. Debe tener 8 dígitos numéricos positivos.");
        }
        this.dni = dni;
        this.nombre = nombre;
        this.scoring = scoring;
        this.siguiendo = new ArrayList<>();
        this.amistades = new HashSet<>(); // Inicialización de amistades
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public int getScoring() {
        return scoring;
    }

    public List<String> getSiguiendo() {
        return siguiendo;
    }

    public Set<String> getAmistades() {
        return amistades;
    }

    public void inicializarAmistadesSiNull() {
        if (this.amistades == null) {
            this.amistades = new HashSet<>();
        }
    }

    public void seguirPorDni(String dni) {
        if (!this.siguiendo.contains(dni)) {
            this.siguiendo.add(dni);
        }
    }

    public void dejarDeSeguir(String dni) {
        this.siguiendo.remove(dni);
    }

    public void agregarAmistad(String dni) {
        inicializarAmistadesSiNull(); // Asegurar que amistades no sea null
        if (!this.dni.equals(dni) && !this.amistades.contains(dni)) {
            this.amistades.add(dni);
        }
    }

    public void eliminarAmistad(String dni) {
        inicializarAmistadesSiNull(); // Asegurar que amistades no sea null
        this.amistades.remove(dni);
    }

    public int cantidadSeguidos() {
        return this.siguiendo.size();
    }

    public static boolean validarDNI(String dni) {
        return dni != null && dni.length() == 8 && dni.matches("[0-9]+");
    }
}
