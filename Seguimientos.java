import java.util.LinkedList;
import java.util.Queue;

public class Seguimientos {
    private Queue<SolicitudSeguimiento> solicitudes;

    public Seguimientos() {
        this.solicitudes = new LinkedList<>();
    }

    public void agregarSolicitud(SolicitudSeguimiento solicitud) {
        this.solicitudes.offer(solicitud);
    }

    public SolicitudSeguimiento procesarSolicitud() {
        return this.solicitudes.poll();
    }

    public boolean tieneSolicitudes() {
        return !this.solicitudes.isEmpty();
    }
}
