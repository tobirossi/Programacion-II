public class SolicitudSeguimiento {

    private Cliente clienteSolicitante;
    private Cliente clienteASeguir;

    public SolicitudSeguimiento(Cliente clienteSolicitante, Cliente clienteASeguir) {
        this.clienteSolicitante = clienteSolicitante;
        this.clienteASeguir = clienteASeguir;
    }

    public Cliente getClienteSolicitante() {
        return clienteSolicitante;
    }

    public Cliente getClienteASeguir() {
        return clienteASeguir;
    }

}
