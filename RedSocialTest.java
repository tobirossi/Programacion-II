import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RedSocialTest {

    private RedSocial redSocial;

    @BeforeEach
    public void setUp() {
        redSocial = new RedSocial();
        redSocial.registrarCliente("12345678", "Juan", 85);
        redSocial.registrarCliente("87654321", "María", 90);
        redSocial.registrarCliente("11223344", "Carlos", 85);
        redSocial.registrarCliente("55667788", "Ana", 95);
        redSocial.registrarCliente("99887766", "Pedro", 80);
        redSocial.registrarCliente("44556677", "Laura", 92);
    }


    @Test
    public void testRegistrarCliente() {
        String resultado = redSocial.registrarCliente("99999999", "TestCliente", 88);
        assertTrue(resultado.contains("correctamente"));
    }

    @Test
    public void testRegistrarClienteDNIInvalido() {
        String resultado = redSocial.registrarCliente("123", "Pedro", 80);
        assertTrue(resultado.contains("DNI inválido"));
    }

    @Test
    public void testBuscarClientePorNombre() {
        String resultado = redSocial.mostrarClientesPorNombre("Juan");
        assertTrue(resultado.contains("Juan"));
        assertTrue(resultado.contains("12345678"));
    }

    @Test
    public void testBuscarClientePorScoring() {
        String resultado = redSocial.buscarClientePorScoringStr("85");
        assertTrue(resultado.contains("Juan") || resultado.contains("Carlos"));
    }

    @Test
    public void testSeguirCliente() {
        String resultado = redSocial.seguirCliente("12345678", "87654321");
        assertTrue(resultado.contains("ahora sigue a"));
    }

    @Test
    public void testAgregarRelacion() {
        String resultado = redSocial.agregarRelacion("12345678", "87654321");
        assertTrue(resultado.contains("Se ha establecido una amistad"));
    }

    @Test
    public void testDeshacerUltimaAccion() {
        redSocial.agregarRelacion("12345678", "87654321");
        String resultado = redSocial.deshacerUltimaAccion("12345678");
        assertTrue(resultado.contains("Se deshizo la amistad"));
    }


    @Test
    public void testObtenerClientesNivelN() {
        String resultado = redSocial.obtenerClientesNivelNParaMenu("0");
        assertTrue(resultado.contains("nivel 0"));
    }

    @Test
    public void testObtenerProfundidadMaxima() {
        String resultado = redSocial.obtenerProfundidadMaximaCliente("55667788");
        assertTrue(resultado.contains("profundidad máxima"));
    }

    @Test
    public void testBuscarPorScoringABB() {
        String resultado = redSocial.buscarPorScoringABBParaMenu("85");
        assertTrue(resultado.contains("Juan") || resultado.contains("Carlos"));
    }

    @Test
    public void testObtenerClienteMayorScoringABB() {
        String resultado = redSocial.obtenerClienteMayorScoringABB();
        assertTrue(resultado.contains("Ana"));
        assertTrue(resultado.contains("95"));
    }

    @Test
    public void testListarClientesRangoScoringABB() {
        String resultado = redSocial.listarClientesRangoScoringABBParaMenu("85", "92");
        assertTrue(resultado.contains("Juan") || resultado.contains("Carlos"));
        assertTrue(resultado.contains("María"));
        assertTrue(resultado.contains("Laura"));
        assertFalse(resultado.contains("Ana"));
    }

    @Test
    public void testBuscarPorScoringAVL() {
        String resultado = redSocial.buscarPorScoringAVLParaMenu("90");
        assertTrue(resultado.contains("María"));
        assertTrue(resultado.contains("AVL"));
    }

    @Test
    public void testObtenerClientesMayorScoringAVL() {
        String resultado = redSocial.obtenerClientesMayorScoringAVLParaMenu("90");
        assertTrue(resultado.contains("Laura"));
        assertTrue(resultado.contains("Ana"));
        assertFalse(resultado.contains("María"));
        assertFalse(resultado.contains("Juan"));
    }


    @Test
    public void testIntegracionArbolesConRegistro() {
        String resultado = redSocial.registrarCliente("88888888", "TestIntegracion", 88);
        assertTrue(resultado.contains("correctamente"));

        String resultadoABB = redSocial.buscarPorScoringABBParaMenu("88");
        assertTrue(resultadoABB.contains("TestIntegracion"));

        String resultadoAVL = redSocial.buscarPorScoringAVLParaMenu("88");
        assertTrue(resultadoAVL.contains("TestIntegracion"));
    }
}
