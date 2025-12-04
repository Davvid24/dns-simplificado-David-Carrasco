public class Registro {
    private String tipo;
    private String ip;

    public Registro(String tipo, String valor) {
        this.tipo = tipo;
        this.ip = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "Tipo: " + tipo + ", IP: " + ip;
    }
}
