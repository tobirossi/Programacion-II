import java.util.Scanner;

public class Main {

    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String VERDE_BRILLANTE = "\u001B[92m";
    public static final String AZUL_BRILLANTE = "\u001B[94m";
    public static final String CYAN_BRILLANTE = "\u001B[96m";

    public static void main(String[] args) {
        RedSocial redSocial = new RedSocial();
        Scanner scanner = new Scanner(System.in);

        System.out.println(CYAN + "Iniciando sistema..." + RESET);
        String resultadoCarga = redSocial.cargarClientesDesdeJSON("clientes.json");
        System.out.println(VERDE_BRILLANTE + "✓ " + resultadoCarga + RESET);
        pausar(scanner);

        int opcion = 0;
        do {
            System.out.println();
            System.out.println(AZUL_BRILLANTE + "╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                      MENU PRINCIPAL                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println(VERDE + "1." + RESET + " Gestion de Clientes " + AMARILLO + "(registrar, buscar clientes)" + RESET);
            System.out.println(VERDE + "2." + RESET + " Relaciones y Seguimientos " + AMARILLO + "(amistades, seguir, distancias)" + RESET);
            System.out.println(VERDE + "3." + RESET + " Analisis con Arboles " + AMARILLO + "(jerarquias, ABB, AVL)" + RESET);
            System.out.println(VERDE + "4." + RESET + " Historial y Acciones " + AMARILLO + "(ver historial, deshacer)" + RESET);
            System.out.println();
            System.out.println(ROJO + "0." + RESET + " Salir");
            System.out.println();
            System.out.print(CYAN + "Ingrese una opción: " + RESET);

            try {
                String opcionStr = scanner.nextLine();
                try {
                    opcion = Integer.parseInt(opcionStr);
                } catch (NumberFormatException e) {
                    System.out.println(ROJO + "✗ Entrada inválida. Ingrese un número del 0 al 4." + RESET);
                    pausar(scanner);
                    continue;
                }

                switch (opcion) {
                    case 1:
                        char opcionGestion;
                        do {
                            System.out.println();
                            System.out.println(AZUL_BRILLANTE + "╔════════════════════════════════════════════════════════════╗");
                            System.out.println("║                   GESTION DE CLIENTES                      ║");
                            System.out.println("╚════════════════════════════════════════════════════════════╝" + RESET);
                            System.out.println();
                            System.out.println(VERDE + "a." + RESET + " Registrar nuevo cliente");
                            System.out.println(VERDE + "b." + RESET + " Buscar cliente por nombre");
                            System.out.println(VERDE + "c." + RESET + " Buscar cliente por scoring");
                            System.out.println();
                            System.out.println(ROJO + "z." + RESET + " Volver al menu principal");
                            System.out.println();
                            System.out.print(CYAN + "Ingrese una opción: " + RESET);

                            String inputGestion = scanner.nextLine();
                            opcionGestion = inputGestion.length() > 0 ? inputGestion.charAt(0) : ' ';

                            switch (opcionGestion) {
                                case 'a':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== REGISTRAR NUEVO CLIENTE ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dni = scanner.nextLine();
                                    System.out.print("Ingrese el nombre del cliente: ");
                                    String nombre = scanner.nextLine();
                                    System.out.print("Ingrese el scoring del cliente: ");
                                    String scoringStr = scanner.nextLine();

                                    System.out.println(redSocial.registrarCliente(dni, nombre, scoringStr));
                                    break;

                                case 'b':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== BUSCAR CLIENTE POR NOMBRE ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el nombre a buscar: ");
                                    String nombreBuscar = scanner.nextLine();
                                    System.out.println(redSocial.mostrarClientesPorNombre(nombreBuscar));
                                    break;

                                case 'c':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== BUSCAR CLIENTE POR SCORING ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring a buscar: ");
                                    String scoringBuscar = scanner.nextLine();
                                    System.out.println(redSocial.buscarClientePorScoringStr(scoringBuscar));
                                    break;

                                case 'z':
                                    break;
                                default:
                                    System.out.println(ROJO + "✗ Opción inválida." + RESET);
                                    pausar(scanner);
                            }

                            if (opcionGestion != 'z') {
                                pausar(scanner);
                            }

                        } while (opcionGestion != 'z');
                        break;

                    case 2:
                        char opcionRelacion;
                        do {
                            System.out.println();
                            System.out.println(AZUL_BRILLANTE + "╔════════════════════════════════════════════════════════════╗");
                            System.out.println("║              RELACIONES Y SEGUIMIENTOS                     ║");
                            System.out.println("╚════════════════════════════════════════════════════════════╝" + RESET);
                            System.out.println();
                            System.out.println(VERDE + "a." + RESET + " Seguir a otro cliente");
                            System.out.println(VERDE + "b." + RESET + " Consultar seguidos de un cliente");
                            System.out.println(VERDE + "c." + RESET + " Agregar amistad entre clientes");
                            System.out.println(VERDE + "d." + RESET + " Mostrar amistades de un cliente");
                            System.out.println(VERDE + "e." + RESET + " Calcular distancia entre clientes");
                            System.out.println();
                            System.out.println(ROJO + "z." + RESET + " Volver al menu principal");
                            System.out.println();
                            System.out.print(CYAN + "Ingrese una opción: " + RESET);

                            String inputRelacion = scanner.nextLine();
                            opcionRelacion = inputRelacion.length() > 0 ? inputRelacion.charAt(0) : ' ';

                            switch (opcionRelacion) {
                                case 'a':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== SEGUIR A OTRO CLIENTE ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente que quiere seguir: ");
                                    String dniSolicitante = scanner.nextLine();
                                    System.out.print("Ingrese el DNI del cliente a seguir: ");
                                    String dniASeguir = scanner.nextLine();

                                    System.out.println(redSocial.seguirCliente(dniSolicitante, dniASeguir));
                                    break;

                                case 'b':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CONSULTAR SEGUIDOS ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dniConsulta = scanner.nextLine();
                                    System.out.println(redSocial.mostrarSeguidos(dniConsulta));
                                    break;

                                case 'c':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== AGREGAR AMISTAD ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del primer cliente: ");
                                    String dniAmigo1 = scanner.nextLine();
                                    System.out.print("Ingrese el DNI del segundo cliente: ");
                                    String dniAmigo2 = scanner.nextLine();

                                    System.out.println(redSocial.agregarRelacion(dniAmigo1, dniAmigo2));
                                    break;

                                case 'd':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== MOSTRAR AMISTADES ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dniAmistades = scanner.nextLine();
                                    System.out.println(redSocial.mostrarAmistades(dniAmistades));
                                    break;

                                case 'e':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CALCULAR DISTANCIA ENTRE CLIENTES ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del primer cliente: ");
                                    String dniOrigen = scanner.nextLine();
                                    System.out.print("Ingrese el DNI del segundo cliente: ");
                                    String dniDestino = scanner.nextLine();
                                    System.out.println(redSocial.calcularDistancia(dniOrigen, dniDestino));
                                    break;

                                case 'z':
                                    break;
                                default:
                                    System.out.println(ROJO + "✗ Opción inválida." + RESET);
                                    pausar(scanner);
                            }

                            if (opcionRelacion != 'z') {
                                pausar(scanner);
                            }

                        } while (opcionRelacion != 'z');
                        break;

                    case 3:
                        char opcionArbol;
                        do {
                            System.out.println();
                            System.out.println(AZUL_BRILLANTE + "╔════════════════════════════════════════════════════════════╗");
                            System.out.println("║                 ANALISIS CON ARBOLES                       ║");
                            System.out.println("╚════════════════════════════════════════════════════════════╝" + RESET);
                            System.out.println();
                            System.out.println(MAGENTA + "--- Árbol General ---" + RESET);
                            System.out.println(VERDE + "a." + RESET + " Obtener clientes en nivel N");
                            System.out.println(VERDE + "b." + RESET + " Obtener profundidad máxima de cliente");
                            System.out.println();
                            System.out.println(MAGENTA + "--- Árbol Binario de Búsqueda (ABB) ---" + RESET);
                            System.out.println(VERDE + "c." + RESET + " Buscar por scoring");
                            System.out.println(VERDE + "d." + RESET + " Cliente con mayor scoring");
                            System.out.println(VERDE + "e." + RESET + " Listar clientes en rango de scoring");
                            System.out.println();
                            System.out.println(MAGENTA + "--- Árbol AVL ---" + RESET);
                            System.out.println(VERDE + "f." + RESET + " Buscar por scoring (AVL)");
                            System.out.println(VERDE + "g." + RESET + " Clientes con scoring mayor a valor");
                            System.out.println();
                            System.out.println(VERDE + "h." + RESET + " Comparar velocidad de búsqueda (ABB vs AVL)");
                            System.out.println();
                            System.out.println(ROJO + "z." + RESET + " Volver al menu principal");
                            System.out.println();
                            System.out.print(CYAN + "Ingrese una opción: " + RESET);

                            String inputArbol = scanner.nextLine();
                            opcionArbol = inputArbol.length() > 0 ? inputArbol.charAt(0) : ' ';

                            switch (opcionArbol) {
                                case 'a':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CLIENTES EN NIVEL N ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el nivel a consultar: ");
                                    String nivelStr = scanner.nextLine();
                                    System.out.println(redSocial.obtenerClientesNivelNParaMenu(nivelStr));
                                    break;

                                case 'b':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== PROFUNDIDAD MÁXIMA DE CLIENTE ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dniProfundidad = scanner.nextLine();
                                    System.out.println(redSocial.obtenerProfundidadMaximaCliente(dniProfundidad));
                                    break;

                                case 'c':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== BUSCAR POR SCORING (ABB) ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring a buscar: ");
                                    String scoringABB = scanner.nextLine();
                                    System.out.println(redSocial.buscarPorScoringABBParaMenu(scoringABB));
                                    break;

                                case 'd':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CLIENTE CON MAYOR SCORING (ABB) ===" + RESET);
                                    System.out.println();
                                    System.out.println(redSocial.obtenerClienteMayorScoringABB());
                                    break;

                                case 'e':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CLIENTES EN RANGO DE SCORING (ABB) ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring mínimo: ");
                                    String minABB = scanner.nextLine();
                                    System.out.print("Ingrese el scoring máximo: ");
                                    String maxABB = scanner.nextLine();
                                    System.out.println(redSocial.listarClientesRangoScoringABBParaMenu(minABB, maxABB));
                                    break;

                                case 'f':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== BUSCAR POR SCORING (AVL) ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring a buscar: ");
                                    String scoringAVL = scanner.nextLine();
                                    System.out.println(redSocial.buscarPorScoringAVLParaMenu(scoringAVL));
                                    break;

                                case 'g':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== CLIENTES CON SCORING MAYOR A VALOR (AVL) ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring mínimo: ");
                                    String scoringMinimoStr = scanner.nextLine();
                                    System.out.println(redSocial.obtenerClientesMayorScoringAVLParaMenu(scoringMinimoStr));
                                    break;
                                case 'h':
                                    System.out.println(CYAN_BRILLANTE + "=== COMPARAR VELOCIDAD DE BÚSQUEDA (ABB vs AVL) ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el scoring a buscar: ");
                                    String scoringCompararStr = scanner.nextLine();
                                    try {
                                        int scoringComparar = Integer.parseInt(scoringCompararStr);
                                        System.out.println(redSocial.compararRendimientoBusqueda(scoringComparar));
                                    } catch (NumberFormatException e) {
                                        System.out.println(ROJO + "Error: Ingrese un número entero válido para el scoring." + RESET);
                                    }
                                    break;
                                case 'z':
                                    break;
                                default:
                                    System.out.println(ROJO + "✗ Opción inválida." + RESET);
                                    pausar(scanner);
                            }

                            if (opcionArbol != 'z') {
                                pausar(scanner);
                            }

                        } while (opcionArbol != 'z');
                        break;

                    case 4:
                        char opcionHistorial;
                        do {
                            System.out.println();
                            System.out.println(AZUL_BRILLANTE + "╔════════════════════════════════════════════════════════════╗");
                            System.out.println("║                HISTORIAL Y ACCIONES                        ║");
                            System.out.println("╚════════════════════════════════════════════════════════════╝" + RESET);
                            System.out.println();
                            System.out.println(VERDE + "a." + RESET + " Ver historial de un cliente");
                            System.out.println(VERDE + "b." + RESET + " Deshacer última acción de un cliente");
                            System.out.println();
                            System.out.println(ROJO + "z." + RESET + " Volver al menu principal");
                            System.out.println();
                            System.out.print(CYAN + "Ingrese una opción: " + RESET);

                            String inputHistorial = scanner.nextLine();
                            opcionHistorial = inputHistorial.length() > 0 ? inputHistorial.charAt(0) : ' ';

                            switch (opcionHistorial) {
                                case 'a':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== VER HISTORIAL DE CLIENTE ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dniHistorial = scanner.nextLine();
                                    System.out.println(redSocial.mostrarHistorialCliente(dniHistorial));
                                    break;

                                case 'b':
                                    System.out.println();
                                    System.out.println(CYAN_BRILLANTE + "=== DESHACER ÚLTIMA ACCIÓN ===" + RESET);
                                    System.out.println();
                                    System.out.print("Ingrese el DNI del cliente: ");
                                    String dniDeshacer = scanner.nextLine();
                                    System.out.println(redSocial.deshacerUltimaAccion(dniDeshacer));
                                    break;

                                case 'z':
                                    break;
                                default:
                                    System.out.println(ROJO + "✗ Opción inválida." + RESET);
                                    pausar(scanner);
                            }

                            if (opcionHistorial != 'z') {
                                pausar(scanner);
                            }

                        } while (opcionHistorial != 'z');
                        break;

                    case 0:
                        System.out.println();
                        System.out.println(CYAN + "Guardando datos..." + RESET);
                        String resultadoGuardado = redSocial.guardarClientesEnJSON("clientes.json");
                        System.out.println(VERDE_BRILLANTE + "✓ " + resultadoGuardado + RESET);
                        System.out.println(CYAN + "Saliendo del programa..." + RESET);
                        break;

                    default:
                        System.out.println(ROJO + "✗ Opción inválida. Intente nuevamente." + RESET);
                        pausar(scanner);
                }
            } catch (Exception e) {
                System.out.println(ROJO + "✗ Error inesperado: " + e.getMessage() + RESET);
                pausar(scanner);
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void pausar(Scanner scanner) {
        System.out.println();
        System.out.print(AMARILLO + "Presione Enter para continuar..." + RESET);
        scanner.nextLine();
    }
}
