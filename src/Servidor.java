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

        }
        // SOLICITUD AL MAPEO CON CLAVE
       /* List<Registro> solicitud = registros.get("google.com");
        System.out.println(solicitud.getFirst().getValor());*/

        final int puerto = 5000;
        final String host = "localhost";
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor DNS iniciado. Esperando conexión en el puerto " + puerto + "...");

            while (true) {//vuelta a empezar al cerrar el cliente
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress().getHostAddress());

                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);
                while (true) {
                    try {


                        String regex = "^LOOKUP\\s+(A|CNAME|MX)\\s+([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";
                        salida.println("Por favor, realiza una solicitud con este formato: (  LOOKUP <tipo> <dominio>  )");
                        String peticion = entrada.readLine();
                        if (peticion.equalsIgnoreCase("exit")) {
                            salida.println("Cerrando conexión...");
                            cliente.close();
                            break;
                        }
//si concuerda con la regex

                        if (peticion.matches(regex)) {
                            String[] partes2 = peticion.split("\\s+");
                            String tipo = partes2[1];
                            String dominio = partes2[2];

                            List<Registro> solicitud = registros.get(dominio);
                            //si hay algun registro con la clave solicitada
                            if (solicitud != null && solicitud.getFirst().getTipo().matches(tipo)) {
                                salida.println("200 " + solicitud.getFirst().getIp());
                            } else {//si la solicitud es correcta pero la clave no existe
                                salida.println("404 Not Found");
                            }
                        } else {// si la solicitud no concuerda con la regex
                            salida.println("400 Bad Request.");
                        }

                    } catch (Exception e) {
                        salida.println("500 Server Error");
                    }
                }

            }
        }
    }
}