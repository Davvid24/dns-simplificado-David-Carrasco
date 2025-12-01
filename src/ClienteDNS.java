//cliente de prueba para probar el servidor
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteDNS {
    public static void main(String[] args) {
        final String host = "localhost";
        final int puerto = 5000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado al servidor DNS en " + host + ":" + puerto);

            // Hilo que lee constantemente lo que envía el servidor
            Thread lector = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("Servidor: " + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada por el servidor.");
                }
            });
            lector.start();

            // Bucle para enviar solicitudes
            while (true) {
                System.out.print("Cliente > ");
                String mensaje = sc.nextLine();
                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("exit")) {
                    System.out.println("Cerrando cliente...");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
