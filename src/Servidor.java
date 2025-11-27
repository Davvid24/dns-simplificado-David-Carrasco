import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
    public static void main(String[] args) throws IOException {
        Map<String, List<Registro>> registros = new HashMap<>();



        final int puerto = 5000;
        final String host = "localhost";
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("ClienteServidorBase.Servidor iniciado. Esperando conexión en el puerto " + puerto + "...");

            // Espera hasta que un cliente se conecte
            Socket cliente = servidor.accept();
            System.out.println("ClienteServidorBase.Cliente conectado desde: " + cliente.getInetAddress().getHostAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);

        }
    }
}