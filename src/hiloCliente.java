import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class hiloCliente implements Runnable {
    private Socket cliente;
    private Map<String, List<Registro>> registros;
    private File fichero;
    private AtomicInteger conexiones;

    public hiloCliente(Socket cliente, Map<String, List<Registro>> registros, File fichero, AtomicInteger conexiones) {
        this.cliente = cliente;
        this.registros = registros;
        this.fichero = fichero;
        this.conexiones = conexiones;
    }

    @Override
    public void run() {

        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);

                while (true) {
                    try {
                        String regexBuscar = "^LOOKUP\\s+(A|CNAME|MX)\\s+([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";
                        String regexAnadir = "^REGISTER\\s+([a-zA-Z0-9.-]+)\\s+(A|CNAME|MX)\\s+(\\S+)$";

                        salida.println("Por favor, realiza una solicitud con este formato: (  LOOKUP <tipo> <dominio>  )");
                        String peticion = entrada.readLine();
                        if (peticion == null){
                            break;//si no compruebo antes de cerrar la peticion recibida sera nula y dara nullpointerexception
                        }
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
                        if (peticion.matches(regexBuscar)) {
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
                        } else if (peticion.matches(regexAnadir)) {
                            String[] partesPeticion = peticion.split(" ", 2);
                            String registroAAnadir = partesPeticion[1];
                            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichero, true))) {

                                bw.write(  System.lineSeparator() + partesPeticion[1]);
                                salida.println("200 Record Added");
                            }
                            String[] campos = registroAAnadir.split("\\s+");
                            if (campos.length == 3) {
                                String dominio = campos[0];
                                String tipo = campos[1];
                                String ip = campos[2];

                                Registro reg = new Registro(tipo, ip);
                                registros.computeIfAbsent(dominio, k -> new ArrayList<>()).add(reg);
                            }

                        } else {// si la solicitud no concuerda con la regex
                            salida.println("400 Bad Request.");
                        }

                    } catch (Exception e) {
                        salida.println("500 Server Error");
                    }
                }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }finally {
            try {
                cliente.close();
                conexiones.decrementAndGet();
            } catch (IOException i) {
                i.printStackTrace();
            }

        }
    }

}
