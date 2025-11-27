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


        File file = new File("registros.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] lineas = line.split(",");
            registros.put(lineas[0], new ArrayList<>());
        }



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