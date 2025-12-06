import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
    public static void main(String[] args) throws IOException {
        Map<String, List<Registro>> registros = new HashMap<>();


        File file = new File("direcciones.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] partes = line.split("\\s+");
            String dominio = partes[0];
            String tipo = partes[1];
            String ip = partes[2];
            Registro reg = new Registro(tipo, ip);
            registros.computeIfAbsent(dominio, k -> new ArrayList<>()).add(reg);
            // Por lo que he visto computeIfAbsent si la clave esta repetida unicamente añade el valor
        }


        final int puerto = 5000;
        final String host = "localhost";
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor DNS iniciado. Esperando conexión en el puerto " + puerto + "...");
            int maxClientes = 5;
            int clientesAceptados = 0;
            while (clientesAceptados < maxClientes) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                hiloCliente clienteRunnable = new hiloCliente(cliente, registros, file);
                Thread hilo = new Thread(clienteRunnable);
                hilo.start();

                clientesAceptados++;
            }

            System.out.println("Se ha alcanzado el máximo de clientes.");

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
        }
    }
