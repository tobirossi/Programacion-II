import java.util.Date;
//
public class Accion {
    private String tipo;
    private String detalles;
    private Date fechaHora;
    private Cliente cliente;
    private SolicitudSeguimiento solicitudSeguimiento;

    public Accion(String tipo, String detalles, Cliente cliente) {
        this.tipo = tipo;
        this.detalles = detalles;
        this.fechaHora = new Date(); // Fecha y hora actual
        this.cliente = cliente;
        this.solicitudSeguimiento = null;
    }

    public Accion(String tipo, String detalles, Cliente cliente, SolicitudSeguimiento solicitudSeguimiento) {
        this.tipo = tipo;
        this.detalles = detalles;
        this.fechaHora = new Date(); // Fecha y hora actual
        this.cliente = cliente;
        this.solicitudSeguimiento = solicitudSeguimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDetalles() {
        return detalles;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public SolicitudSeguimiento getSolicitudSeguimiento() {
        return solicitudSeguimiento;
    }
}
