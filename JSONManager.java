import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JSONManager {

    public Map<String, Cliente> cargarClientesDesdeJSON(String rutaArchivo) {
        Gson gson = new Gson();
        Map<String, Cliente> clientesPorDni = new HashMap<>();

        try (FileReader reader = new FileReader(rutaArchivo)) {
            ListaClientes listaClientes = gson.fromJson(reader, ListaClientes.class);
            if (listaClientes != null && listaClientes.getClientes() != null) {
                for (Cliente cliente : listaClientes.getClientes()) {
                    clientesPorDni.put(cliente.getDni(), cliente);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar clientes desde JSON: " + e.getMessage());
            return new HashMap<>();
        }

        return clientesPorDni;
    }

    public void cargarRelacionesDesdeJSON(String rutaArchivo, RedSocial redSocial) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("relaciones")) {
                    JsonArray relacionesArray = jsonObject.getAsJsonArray("relaciones");
                    for (JsonElement relacion : relacionesArray) {
                        if (relacion.isJsonObject()) {
                            JsonObject relacionObj = relacion.getAsJsonObject();
                            String dni1 = relacionObj.get("dni1").getAsString();
                            String dni2 = relacionObj.get("dni2").getAsString();
                            redSocial.agregarRelacion(dni1, dni2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar relaciones desde JSON: " + e.getMessage());
        }
    }

    public void guardarClientesYRelacionesEnJSON(String rutaArchivo, List<Cliente> clientes, GrafoRelaciones grafo) {
        try {
            JsonObject jsonPrincipal = new JsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray clientesArray = new JsonArray();

            for (Cliente cliente : clientes) {
                JsonObject clienteObj = new JsonObject();
                clienteObj.addProperty("dni", cliente.getDni());
                clienteObj.addProperty("nombre", cliente.getNombre());
                clienteObj.addProperty("scoring", cliente.getScoring());

                // Agregar lista de seguidos
                JsonArray siguiendoArray = new JsonArray();
                for (String dniSeguido : cliente.getSiguiendo()) {
                    siguiendoArray.add(dniSeguido);
                }
                clienteObj.add("siguiendo", siguiendoArray);

                clientesArray.add(clienteObj);
            }

            jsonPrincipal.add("clientes", clientesArray);

            // Agregar relaciones (amistades)
            JsonArray relacionesArray = new JsonArray();
            Set<String> relacionesRegistradas = new HashSet<>();

            for (Cliente cliente : clientes) {
                String dni1 = cliente.getDni();
                Set<String> amistades = cliente.getAmistades();

                for (String dni2 : amistades) {
                    String relacion = dni1.compareTo(dni2) < 0 ? dni1 + "-" + dni2 : dni2 + "-" + dni1;

                    if (!relacionesRegistradas.contains(relacion)) {
                        JsonObject relacionObj = new JsonObject();
                        relacionObj.addProperty("dni1", dni1);
                        relacionObj.addProperty("dni2", dni2);
                        relacionesArray.add(relacionObj);

                        relacionesRegistradas.add(relacion);
                    }
                }
            }

            jsonPrincipal.add("relaciones", relacionesArray);

            // Escribir JSON completo al archivo
            try (FileWriter writer = new FileWriter(rutaArchivo)) {
                gson.toJson(jsonPrincipal, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar clientes y relaciones en JSON", e);
        }
    }
}
