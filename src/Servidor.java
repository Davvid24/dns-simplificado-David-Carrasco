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
                        if (peticion.equalsIgnoreCase("List")) {
                            salida.println("150 Inicio  Listado: ");

                            registros.forEach((clave, lista) -> {
                                salida.println("Nombre: " + clave);
                                lista.forEach(reg -> salida.println("  " + reg));
                                salida.println("---------------------------------------");
                            });
                            salida.println("226 Fin Listado: ");

                        continue;
                        }
//si concuerda con la regex


                        peticion = peticion.trim();
                        if (peticion.matches(regex)) {
                            String[] partes2 = peticion.split("\\s+");
                            String tipo = partes2[1];
                            String dominio = partes2[2];

                            List<Registro> solicitud = registros.get(dominio);

                            boolean encontrado = false;

                            //si hay registros imprime todo lo que coincida
                            if (solicitud != null) {
                                for (Registro r : solicitud) {
                                    if (r.getTipo().equalsIgnoreCase(tipo)) {
                                        salida.println("200 " + r.getIp());
                                        encontrado = true;
                                    }
                                }
                            }
                            if (!encontrado) {
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