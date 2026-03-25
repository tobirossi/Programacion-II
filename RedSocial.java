import java.util.*;

public class RedSocial {
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String VERDE_BRILLANTE = "\u001B[92m";
    public static final String AZUL_BRILLANTE = "\u001B[94m";
    public static final String CYAN_BRILLANTE = "\u001B[96m";

    private Map<String, Cliente> clientesPorDni;
    private Map<String, HistorialAcciones> historialesPorCliente;
    private Seguimientos seguimientos;
    private JSONManager jsonManager;
    private IndicesClientes indices;
    private GrafoRelaciones grafoRelaciones;

    // Nuevas estructuras de árboles
    private ArbolGeneral arbolJerarquia;
    private ArbolBinarioBusqueda arbolABB;
    private ArbolAVL arbolAVL;

    private static final int LIMITE_SEGUIDOS = 2;

    public RedSocial() {
        this.clientesPorDni = new HashMap<>();
        this.historialesPorCliente = new HashMap<>();
        this.seguimientos = new Seguimientos();
        this.jsonManager = new JSONManager();
        this.indices = new IndicesClientes();
        this.grafoRelaciones = new GrafoRelaciones();

        // Inicializar árboles
        this.arbolJerarquia = new ArbolGeneral();
        this.arbolABB = new ArbolBinarioBusqueda();
        this.arbolAVL = new ArbolAVL();
    }

    public String cargarClientesDesdeJSON(String rutaArchivo) {
        try {
            Map<String, Cliente> clientesCargados = jsonManager.cargarClientesDesdeJSON(rutaArchivo);
            if (clientesCargados != null && !clientesCargados.isEmpty()) {
                for (Cliente cliente : clientesCargados.values()) {
                    if (cliente.getAmistades() == null) {
                        cliente.inicializarAmistadesSiNull();
                    }

                    clientesPorDni.put(cliente.getDni(), cliente);
                    indices.agregarCliente(cliente);
                    grafoRelaciones.agregarCliente(cliente.getDni());

                    if (!historialesPorCliente.containsKey(cliente.getDni())) {
                        historialesPorCliente.put(cliente.getDni(), new HistorialAcciones(cliente.getDni()));
                    }
                }

                jsonManager.cargarRelacionesDesdeJSON(rutaArchivo, this);

                construirArboles();

                return VERDE + "Se cargaron " + CYAN + clientesCargados.size() + VERDE + " clientes desde el archivo JSON y se construyeron los árboles." + RESET;
            } else {
                return AMARILLO + "No se encontraron clientes en el archivo JSON o el archivo está vacío." + RESET;
            }
        } catch (RuntimeException e) {
            return ROJO + "Error al cargar clientes: " + e.getMessage() + RESET;
        }
    }

    public String guardarClientesEnJSON(String rutaArchivo) {
        try {
            jsonManager.guardarClientesYRelacionesEnJSON(rutaArchivo, new ArrayList<>(clientesPorDni.values()), grafoRelaciones);
            return VERDE + "Clientes y relaciones guardados correctamente en " + CYAN + rutaArchivo + RESET;
        } catch (RuntimeException e) {
            return ROJO + "Error al guardar clientes: " + e.getMessage() + RESET;
        }
    }

    public String registrarCliente(String dni, String nombre, int scoring) {
        try {
            if (!Cliente.validarDNI(dni)) {
                return ROJO + "DNI inválido. Debe tener 8 dígitos numéricos positivos." + RESET;
            }

            // Verificar si ya existe
            if (clientesPorDni.containsKey(dni)) {
                return ROJO + "Ya existe un cliente con el DNI " + dni + "." + RESET;
            }

            // Crear cliente
            Cliente nuevoCliente = new Cliente(dni, nombre, scoring);
            clientesPorDni.put(dni, nuevoCliente);

            indices.agregarCliente(nuevoCliente);

            grafoRelaciones.agregarCliente(dni);

            arbolABB.insertar(nuevoCliente);
            arbolAVL.insertar(nuevoCliente);

            HistorialAcciones historial = new HistorialAcciones(dni);
            historialesPorCliente.put(dni, historial);

            Accion accion = new Accion("Registro", "Se registró el cliente " + nombre, nuevoCliente);
            historial.registrarAccion(accion);

            reconstruirArbolJerarquia();

            return VERDE_BRILLANTE + "Cliente " + CYAN + nombre + VERDE_BRILLANTE + " registrado correctamente y agregado a todas las estructuras." + RESET;
        } catch (IllegalArgumentException e) {
            return ROJO + "Error: " + e.getMessage() + RESET;
        } catch (Exception e) {
            return ROJO + "Error inesperado al registrar cliente: " + e.getMessage() + RESET;
        }
    }

    public String registrarCliente(String dni, String nombre, String scoringStr) {
        try {
            int scoring = Integer.parseInt(scoringStr);
            return registrarCliente(dni, nombre, scoring);
        } catch (NumberFormatException e) {
            return ROJO + "Error: El scoring debe ser un número entero." + RESET;
        }
    }

    public String buscarClientePorScoringStr(String scoringStr) {
        try {
            int scoring = Integer.parseInt(scoringStr);
            return mostrarClientesPorScoring(scoring);
        } catch (NumberFormatException e) {
            return ROJO + "Error: El scoring debe ser un número entero." + RESET;
        } catch (Exception e) {
            return ROJO + "Error inesperado al buscar por scoring: " + e.getMessage() + RESET;
        }
    }

    public Cliente buscarClientePorDni(String dni) {
        return clientesPorDni.get(dni);
    }

    public List<Cliente> buscarClientesPorNombre(String nombre) {
        List<Cliente> clientesEncontrados = new ArrayList<>();
        List<String> dnis = indices.buscarPorNombre(nombre);
        for (String dni : dnis) {
            Cliente cliente = clientesPorDni.get(dni);
            if (cliente != null) {
                clientesEncontrados.add(cliente);
            }
        }
        return clientesEncontrados;
    }

    public List<Cliente> buscarClientesPorScoring(int scoringABuscar) {
        List<Cliente> clientesEncontrados = new ArrayList<>();
        List<String> dnis = indices.buscarPorScoring(scoringABuscar);
        for (String dni : dnis) {
            Cliente cliente = clientesPorDni.get(dni);
            if (cliente != null) {
                clientesEncontrados.add(cliente);
            }
        }
        return clientesEncontrados;
    }

    public String seguirCliente(String dniSolicitante, String dniASeguir) {
        try {
            if (dniSolicitante.equals(dniASeguir)) {
                return ROJO + "Error: Un cliente no puede seguirse a sí mismo." + RESET;
            }

            Cliente clienteSolicitante = buscarClientePorDni(dniSolicitante);
            Cliente clienteASeguir = buscarClientePorDni(dniASeguir);

            if (clienteSolicitante == null || clienteASeguir == null) {
                return ROJO + "No se encontraron los clientes especificados." + RESET;
            }

            if (clienteSolicitante.getSiguiendo().contains(dniASeguir)) {
                return AMARILLO + "El cliente " + clienteSolicitante.getNombre() + " ya sigue a " + clienteASeguir.getNombre() + "." + RESET;
            }

            if (clienteSolicitante.cantidadSeguidos() >= LIMITE_SEGUIDOS) {
                return ROJO + "El cliente ya sigue a " + LIMITE_SEGUIDOS + " clientes (límite máximo)." + RESET;
            }

            SolicitudSeguimiento solicitud = new SolicitudSeguimiento(clienteSolicitante, clienteASeguir);
            seguimientos.agregarSolicitud(solicitud);

            HistorialAcciones historial = historialesPorCliente.get(dniSolicitante);
            Accion accion = new Accion("Solicitud de seguimiento",
                    "El cliente " + clienteSolicitante.getNombre() + " quiere seguir a " + clienteASeguir.getNombre(),
                    clienteSolicitante, solicitud);
            historial.registrarAccion(accion);

            return procesarSolicitudSeguimiento();
        } catch (Exception e) {
            return ROJO + "Error al procesar solicitud de seguimiento: " + e.getMessage() + RESET;
        }
    }

    public String agregarRelacion(String dni1, String dni2) {
        try {
            if (dni1.equals(dni2)) {
                return ROJO + "Error: Un cliente no puede ser amigo de sí mismo." + RESET;
            }

            Cliente cliente1 = buscarClientePorDni(dni1);
            Cliente cliente2 = buscarClientePorDni(dni2);

            if (cliente1 == null || cliente2 == null) {
                return ROJO + "No se encontraron los clientes especificados." + RESET;
            }

            // Inicializar amistades si es null
            cliente1.inicializarAmistadesSiNull();
            cliente2.inicializarAmistadesSiNull();

            if (grafoRelaciones.existeRelacion(dni1, dni2)) {
                return AMARILLO + "Los clientes " + cliente1.getNombre() + " y " + cliente2.getNombre() + " ya son amigos." + RESET;
            }

            grafoRelaciones.agregarRelacion(dni1, dni2);

            cliente1.agregarAmistad(dni2);
            cliente2.agregarAmistad(dni1);

            HistorialAcciones historial1 = historialesPorCliente.get(dni1);
            Accion accion1 = new Accion("Nueva Amistad",
                    "El cliente " + cliente1.getNombre() + " ahora es amigo de " + cliente2.getNombre(),
                    cliente1);
            historial1.registrarAccion(accion1);

            HistorialAcciones historial2 = historialesPorCliente.get(dni2);
            Accion accion2 = new Accion("Nueva Amistad",
                    "El cliente " + cliente2.getNombre() + " ahora es amigo de " + cliente1.getNombre(),
                    cliente2);
            historial2.registrarAccion(accion2);

            return VERDE_BRILLANTE + "Se ha establecido una amistad entre " + CYAN + cliente1.getNombre() + VERDE_BRILLANTE + " y " + CYAN + cliente2.getNombre() + VERDE_BRILLANTE + "." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al establecer amistad: " + e.getMessage() + RESET;
        }
    }

    public List<Cliente> obtenerAmistades(String dni) {
        List<Cliente> amistades = new ArrayList<>();
        try {
            Set<String> dnisAmigos = grafoRelaciones.obtenerRelaciones(dni);

            for (String dniAmigo : dnisAmigos) {
                Cliente amigo = clientesPorDni.get(dniAmigo);
                if (amigo != null) {
                    amistades.add(amigo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener amistades: " + e.getMessage());
        }
        return amistades;
    }

    public int calcularDistanciaEntreClientes(String dni1, String dni2) {
        try {
            return grafoRelaciones.calcularDistancia(dni1, dni2);
        } catch (Exception e) {
            System.err.println("Error al calcular distancia: " + e.getMessage());
            return -1;
        }
    }

    public String procesarSolicitudSeguimiento() {
        try {
            if (seguimientos.tieneSolicitudes()) {
                SolicitudSeguimiento solicitud = seguimientos.procesarSolicitud();
                Cliente clienteSolicitante = solicitud.getClienteSolicitante();
                Cliente clienteASeguir = solicitud.getClienteASeguir();

                clienteSolicitante.seguirPorDni(clienteASeguir.getDni());

                HistorialAcciones historial = historialesPorCliente.get(clienteSolicitante.getDni());
                Accion accion = new Accion("Seguimiento",
                        "El cliente " + clienteSolicitante.getNombre() + " ahora sigue a " + clienteASeguir.getNombre(),
                        clienteSolicitante, solicitud);
                historial.registrarAccion(accion);

                return VERDE_BRILLANTE + clienteSolicitante.getNombre() + " ahora sigue a " + clienteASeguir.getNombre() + "." + RESET;
            }

            return AMARILLO + "No hay solicitudes de seguimiento pendientes." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al procesar solicitud de seguimiento: " + e.getMessage() + RESET;
        }
    }

    public String deshacerUltimaAccion(String dni) {
        try {
            if (!historialesPorCliente.containsKey(dni)) {
                return ROJO + "No se encontró historial para el cliente con DNI " + dni + "." + RESET;
            }

            HistorialAcciones historial = historialesPorCliente.get(dni);
            Accion accion = historial.deshacerUltimaAccion();

            if (accion != null) {
                String tipoAccion = accion.getTipo();

                switch (tipoAccion) {
                    case "Registro":
                        Cliente cliente = accion.getCliente();
                        indices.removerCliente(cliente); // Remover de índices
                        grafoRelaciones.eliminarCliente(dni); // Remover del grafo

                        arbolABB.eliminar(dni);
                        arbolAVL.eliminar(dni);

                        clientesPorDni.remove(cliente.getDni());
                        historialesPorCliente.remove(cliente.getDni());

                        reconstruirArbolJerarquia();

                        return VERDE + "Se eliminó el registro del cliente " + CYAN + cliente.getNombre() + VERDE + "." + RESET;

                    case "Solicitud de seguimiento":
                        return VERDE + "Se eliminó la solicitud de seguimiento." + RESET;

                    case "Seguimiento":
                        SolicitudSeguimiento solicitudSeguimiento = accion.getSolicitudSeguimiento();
                        if (solicitudSeguimiento != null) {
                            Cliente clienteSolicitante = accion.getCliente();
                            Cliente clienteASeguir = solicitudSeguimiento.getClienteASeguir();
                            clienteSolicitante.dejarDeSeguir(clienteASeguir.getDni());
                            return VERDE + clienteSolicitante.getNombre() + " ya no sigue a " + clienteASeguir.getNombre() + "." + RESET;
                        }
                        return ROJO + "No se pudo deshacer el seguimiento." + RESET;

                    case "Nueva Amistad":
                        String[] partes = accion.getDetalles().split(" ahora es amigo de ");
                        String nombreCliente2 = partes[1];

                        Cliente cliente2 = null;
                        for (Cliente c : clientesPorDni.values()) {
                            if (c.getNombre().equals(nombreCliente2)) {
                                cliente2 = c;
                                break;
                            }
                        }

                        if (cliente2 != null) {
                            eliminarRelacion(dni, cliente2.getDni());
                            return VERDE + "Se deshizo la amistad entre " + accion.getCliente().getNombre() +
                                    " y " + cliente2.getNombre() + "." + RESET;
                        }
                        return ROJO + "No se pudo deshacer la amistad." + RESET;

                    default:
                        return ROJO + "Acción no reconocida: " + tipoAccion + RESET;
                }
            }

            return AMARILLO + "No hay acciones para deshacer en el historial del cliente." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al deshacer acción: " + e.getMessage() + RESET;
        }
    }

    public List<Cliente> obtenerSeguidosPorCliente(String dni) {
        List<Cliente> seguidos = new ArrayList<>();
        try {
            Cliente cliente = clientesPorDni.get(dni);
            if (cliente == null) {
                return seguidos;
            }

            for (String dniSeguido : cliente.getSiguiendo()) {
                Cliente clienteSeguido = clientesPorDni.get(dniSeguido);
                if (clienteSeguido != null) {
                    seguidos.add(clienteSeguido);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener seguidos: " + e.getMessage());
        }
        return seguidos;
    }

    public HistorialAcciones getHistorialCliente(String dni) {
        return historialesPorCliente.get(dni);
    }


    private void construirArboles() {
        List<Cliente> clientes = new ArrayList<>(clientesPorDni.values());

        // Construir árbol de jerarquía por scoring
        arbolJerarquia.construirJerarquiaPorScoring(clientes);

        // Construir ABB
        arbolABB.construirDesdeClientes(clientes);

        // Construir AVL
        arbolAVL.construirDesdeClientes(clientes);
    }


    private void reconstruirArbolJerarquia() {
        List<Cliente> clientes = new ArrayList<>(clientesPorDni.values());
        arbolJerarquia.construirJerarquiaPorScoring(clientes);
    }

    public String obtenerClientesNivelNParaMenu(String nivelStr) {
        try {
            int nivel = Integer.parseInt(nivelStr);
            List<Cliente> clientesEnNivel = arbolJerarquia.obtenerClientesNivelN(nivel);

            if (clientesEnNivel.isEmpty()) {
                return AMARILLO + "No hay clientes en el nivel " + nivel + "." + RESET;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(CYAN_BRILLANTE + "Clientes en el nivel " + nivel + ":" + RESET + "\n");
            for (Cliente cliente : clientesEnNivel) {
                resultado.append(VERDE + "- " + RESET).append(cliente.getNombre())
                        .append(AMARILLO + " (DNI: " + RESET).append(cliente.getDni())
                        .append(AMARILLO + ", Scoring: " + RESET).append(cliente.getScoring()).append(")\n");
            }

            return resultado.toString();
        } catch (NumberFormatException e) {
            return ROJO + "Error: El nivel debe ser un número entero." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al obtener clientes del nivel: " + e.getMessage() + RESET;
        }
    }

    public String obtenerProfundidadMaximaCliente(String dni) {
        try {
            Cliente cliente = buscarClientePorDni(dni);
            if (cliente == null) {
                return ROJO + "No se encontró el cliente con DNI " + dni + "." + RESET;
            }

            int profundidadMaxima = arbolJerarquia.obtenerProfundidadMaxima(dni);

            if (profundidadMaxima == -1) {
                return AMARILLO + "El cliente " + cliente.getNombre() + " no se encuentra en el árbol de jerarquía." + RESET;
            }

            return CYAN + "La profundidad máxima desde " + VERDE + cliente.getNombre() + CYAN + " es: " + MAGENTA + profundidadMaxima + RESET;
        } catch (Exception e) {
            return ROJO + "Error al calcular profundidad máxima: " + e.getMessage() + RESET;
        }
    }

    public String buscarPorScoringABBParaMenu(String scoringStr) {
        try {
            int scoring = Integer.parseInt(scoringStr);
            List<Cliente> clientes = arbolABB.buscarPorScoring(scoring);

            if (clientes.isEmpty()) {
                return AMARILLO + "No se encontraron clientes con scoring " + scoring + " (búsqueda ABB)." + RESET;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(CYAN_BRILLANTE + "Clientes encontrados con scoring " + scoring + " (ABB):" + RESET + "\n");
            for (Cliente cliente : clientes) {
                resultado.append(VERDE + "- " + RESET).append(cliente.getNombre())
                        .append(AMARILLO + " (DNI: " + RESET).append(cliente.getDni()).append(")\n");
            }

            return resultado.toString();
        } catch (NumberFormatException e) {
            return ROJO + "Error: El scoring debe ser un número entero." + RESET;
        } catch (Exception e) {
            return ROJO + "Error en búsqueda ABB: " + e.getMessage() + RESET;
        }
    }

    public String obtenerClienteMayorScoringABB() {
        try {
            Cliente cliente = arbolABB.obtenerClienteMayorScoring();

            if (cliente == null) {
                return AMARILLO + "No hay clientes registrados en el ABB." + RESET;
            }

            return CYAN + "Cliente con mayor scoring (ABB): " + VERDE + cliente.getNombre() +
                    AMARILLO + " (DNI: " + RESET + cliente.getDni() + AMARILLO + ", Scoring: " + MAGENTA + cliente.getScoring() + AMARILLO + ")" + RESET;
        } catch (Exception e) {
            return ROJO + "Error al obtener cliente con mayor scoring: " + e.getMessage() + RESET;
        }
    }

    public String listarClientesRangoScoringABBParaMenu(String minStr, String maxStr) {
        try {
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);

            if (min > max) {
                return ROJO + "Error: El valor mínimo no puede ser mayor al máximo." + RESET;
            }

            List<Cliente> clientes = arbolABB.listarClientesRangoScoring(min, max);

            if (clientes.isEmpty()) {
                return AMARILLO + "No se encontraron clientes en el rango [" + min + ", " + max + "] (ABB)." + RESET;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(CYAN_BRILLANTE + "Clientes en el rango [" + min + ", " + max + "] (ABB):" + RESET + "\n");
            for (Cliente cliente : clientes) {
                resultado.append(VERDE + "- " + RESET).append(cliente.getNombre())
                        .append(AMARILLO + " (DNI: " + RESET).append(cliente.getDni())
                        .append(AMARILLO + ", Scoring: " + MAGENTA + RESET).append(cliente.getScoring()).append(")\n");
            }

            return resultado.toString();
        } catch (NumberFormatException e) {
            return ROJO + "Error: Los valores deben ser números enteros." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al listar clientes en rango: " + e.getMessage() + RESET;
        }
    }

    public String buscarPorScoringAVLParaMenu(String scoringStr) {
        try {
            int scoring = Integer.parseInt(scoringStr);
            List<Cliente> clientes = arbolAVL.buscarPorScoring(scoring);

            if (clientes.isEmpty()) {
                return AMARILLO + "No se encontraron clientes con scoring " + scoring + " (búsqueda AVL)." + RESET;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(CYAN_BRILLANTE + "Clientes encontrados con scoring " + scoring + " (AVL):" + RESET + "\n");
            for (Cliente cliente : clientes) {
                resultado.append(VERDE + "- " + RESET).append(cliente.getNombre())
                        .append(AMARILLO + " (DNI: " + RESET).append(cliente.getDni()).append(")\n");
            }

            return resultado.toString();
        } catch (NumberFormatException e) {
            return ROJO + "Error: El scoring debe ser un número entero." + RESET;
        } catch (Exception e) {
            return ROJO + "Error en búsqueda AVL: " + e.getMessage() + RESET;
        }
    }

    public String obtenerClientesMayorScoringAVLParaMenu(String scoringMinimoStr) {
        try {
            int scoringMinimo = Integer.parseInt(scoringMinimoStr);
            List<Cliente> clientes = arbolAVL.obtenerClientesMayorScoring(scoringMinimo);

            if (clientes.isEmpty()) {
                return AMARILLO + "No se encontraron clientes con scoring mayor a " + scoringMinimo + " (AVL)." + RESET;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(CYAN_BRILLANTE + "Clientes con scoring mayor a " + scoringMinimo + " (AVL):" + RESET + "\n");
            for (Cliente cliente : clientes) {
                resultado.append(VERDE + "- " + RESET).append(cliente.getNombre())
                        .append(AMARILLO + " (DNI: " + RESET).append(cliente.getDni())
                        .append(AMARILLO + ", Scoring: " + MAGENTA + RESET).append(cliente.getScoring()).append(")\n");
            }

            return resultado.toString();
        } catch (NumberFormatException e) {
            return ROJO + "Error: El valor debe ser un número entero." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al obtener clientes con mayor scoring: " + e.getMessage() + RESET;
        }
    }
    public String compararRendimientoBusqueda(int scoring) {
        try {
            // Medir tiempo de búsqueda en ABB
            long inicioABB = System.nanoTime();
            arbolABB.buscarPorScoring(scoring);
            long finABB = System.nanoTime() - inicioABB;

            // Medir tiempo de búsqueda en AVL
            long inicioAVL = System.nanoTime();
            arbolAVL.buscarPorScoring(scoring);
            long finAVL = System.nanoTime() - inicioAVL;

            return CYAN + "Comparación de rendimiento:" + RESET + "\n" +
                    VERDE + "ABB: " + RESET + finABB + " nanosegundos\n" +
                    VERDE + "AVL: " + RESET + finAVL + " nanosegundos";

        } catch (Exception e) {
            return ROJO + "Error al comparar rendimiento: " + e.getMessage() + RESET;
        }
    }


    public String mostrarClientesPorNombre(String nombreABuscar) {
        try {
            StringBuilder resultado = new StringBuilder();
            List<Cliente> clientesEncontrados = buscarClientesPorNombre(nombreABuscar);
            if (clientesEncontrados != null && !clientesEncontrados.isEmpty()) {
                resultado.append(CYAN_BRILLANTE + "Clientes encontrados con el nombre " + nombreABuscar + ":" + RESET + "\n");
                for (Cliente cliente : clientesEncontrados) {
                    resultado.append(CYAN + "Nombre: " + RESET).append(cliente.getNombre())
                            .append(CYAN + ", DNI: " + RESET).append(cliente.getDni())
                            .append(CYAN + ", Scoring: " + MAGENTA + RESET).append(cliente.getScoring()).append("\n");
                }
            } else {
                resultado.append(AMARILLO + "No se encontró ningún cliente con ese nombre." + RESET);
            }
            return resultado.toString();
        } catch (Exception e) {
            return ROJO + "Error al buscar clientes por nombre: " + e.getMessage() + RESET;
        }
    }

    public String mostrarClientesPorScoring(int scoringABuscar) {
        try {
            StringBuilder resultado = new StringBuilder();
            List<Cliente> clientesEncontrados = buscarClientesPorScoring(scoringABuscar);
            if (clientesEncontrados != null && !clientesEncontrados.isEmpty()) {
                resultado.append(CYAN_BRILLANTE + "Clientes con scoring " + scoringABuscar + ":" + RESET + "\n");
                for (Cliente cliente : clientesEncontrados) {
                    resultado.append(CYAN + "Nombre: " + RESET).append(cliente.getNombre())
                            .append(CYAN + ", DNI: " + RESET).append(cliente.getDni())
                            .append(CYAN + ", Scoring: " + MAGENTA + RESET).append(cliente.getScoring()).append("\n");
                }
            } else {
                resultado.append(AMARILLO + "No se encontró ningún cliente con ese scoring." + RESET);
            }
            return resultado.toString();
        } catch (Exception e) {
            return ROJO + "Error al buscar clientes por scoring: " + e.getMessage() + RESET;
        }
    }

    public String mostrarSeguidos(String dni) {
        try {
            StringBuilder resultado = new StringBuilder();
            List<Cliente> seguidos = obtenerSeguidosPorCliente(dni);
            if (!seguidos.isEmpty()) {
                resultado.append(CYAN_BRILLANTE + "El cliente sigue a:" + RESET + "\n");
                for (Cliente seguido : seguidos) {
                    resultado.append(CYAN + "Nombre: " + RESET).append(seguido.getNombre())
                            .append(CYAN + ", DNI: " + RESET).append(seguido.getDni())
                            .append(CYAN + ", Scoring: " + MAGENTA + RESET).append(seguido.getScoring()).append("\n");
                }
            } else {
                resultado.append(AMARILLO + "El cliente no sigue a nadie o no existe." + RESET);
            }
            return resultado.toString();
        } catch (Exception e) {
            return ROJO + "Error al mostrar seguidos: " + e.getMessage() + RESET;
        }
    }

    public String mostrarAmistades(String dni) {
        try {
            StringBuilder resultado = new StringBuilder();
            List<Cliente> amistades = obtenerAmistades(dni);

            Cliente cliente = buscarClientePorDni(dni);
            if (cliente == null) {
                return ROJO + "El cliente no existe." + RESET;
            }

            if (!amistades.isEmpty()) {
                resultado.append(CYAN_BRILLANTE + "Amistades de " + cliente.getNombre() + ":" + RESET + "\n");
                for (Cliente amigo : amistades) {
                    resultado.append(CYAN + "Nombre: " + RESET).append(amigo.getNombre())
                            .append(CYAN + ", DNI: " + RESET).append(amigo.getDni())
                            .append(CYAN + ", Scoring: " + MAGENTA + RESET).append(amigo.getScoring()).append("\n");
                }
            } else {
                resultado.append(AMARILLO + cliente.getNombre() + " no tiene amistades registradas." + RESET);
            }
            return resultado.toString();
        } catch (Exception e) {
            return ROJO + "Error al mostrar amistades: " + e.getMessage() + RESET;
        }
    }

    public String calcularDistancia(String dni1, String dni2) {
        try {
            Cliente cliente1 = buscarClientePorDni(dni1);
            Cliente cliente2 = buscarClientePorDni(dni2);

            if (cliente1 == null || cliente2 == null) {
                return ROJO + "Uno o ambos clientes no existen." + RESET;
            }

            int distancia = calcularDistanciaEntreClientes(dni1, dni2);

            if (distancia == 0) {
                return AMARILLO + "Los clientes son la misma persona." + RESET;
            } else if (distancia == 1) {
                return VERDE + cliente1.getNombre() + " y " + cliente2.getNombre() + " son amigos directos." + RESET;
            } else if (distancia > 1) {
                return CYAN + "La distancia entre " + cliente1.getNombre() + " y " + cliente2.getNombre() + " es de " + MAGENTA + distancia + CYAN + " saltos." + RESET;
            } else {
                return AMARILLO + "No existe un camino entre " + cliente1.getNombre() + " y " + cliente2.getNombre() + "." + RESET;
            }
        } catch (Exception e) {
            return ROJO + "Error al calcular distancia: " + e.getMessage() + RESET;
        }
    }

    public String mostrarHistorialCliente(String dni) {
        try {
            StringBuilder resultado = new StringBuilder();
            HistorialAcciones historial = getHistorialCliente(dni);
            if (historial != null && !historial.getAcciones().isEmpty()) {
                resultado.append(CYAN_BRILLANTE + "Historial de acciones para el cliente con DNI " + dni + ":" + RESET + "\n");
                for (Accion accion : historial.getAcciones()) {
                    resultado.append(VERDE + accion.getTipo() + ": " + RESET)
                            .append(accion.getDetalles()).append(AMARILLO + " - " + RESET)
                            .append(accion.getFechaHora()).append("\n");
                }
            } else {
                resultado.append(AMARILLO + "No hay historial para el cliente o no existe." + RESET);
            }
            return resultado.toString();
        } catch (Exception e) {
            return ROJO + "Error al mostrar historial: " + e.getMessage() + RESET;
        }
    }

    public String eliminarRelacion(String dni1, String dni2) {
        try {
            Cliente cliente1 = buscarClientePorDni(dni1);
            Cliente cliente2 = buscarClientePorDni(dni2);

            if (cliente1 == null || cliente2 == null) {
                return ROJO + "No se encontraron los clientes especificados." + RESET;
            }

            // Inicializar amistades si es null
            cliente1.inicializarAmistadesSiNull();
            cliente2.inicializarAmistadesSiNull();

            if (!grafoRelaciones.existeRelacion(dni1, dni2)) {
                return AMARILLO + "Los clientes " + cliente1.getNombre() + " y " + cliente2.getNombre() + " no son amigos." + RESET;
            }

            grafoRelaciones.eliminarRelacion(dni1, dni2);

            cliente1.eliminarAmistad(dni2);
            cliente2.eliminarAmistad(dni1);

            return VERDE + "Se ha eliminado la amistad entre " + cliente1.getNombre() + " y " + cliente2.getNombre() + "." + RESET;
        } catch (Exception e) {
            return ROJO + "Error al eliminar amistad: " + e.getMessage() + RESET;
        }
    }

}
