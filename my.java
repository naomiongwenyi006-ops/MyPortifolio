import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Map;

public class PortfolioServer {

    private static final int PORT = 8080;
    private static final Path PUBLIC_DIR = Paths.get("public").toAbsolutePath().normalize();
    private static final Path MESSAGES_FILE = Paths.get("messages.txt");

    private static final Map<String, String> MIME_TYPES = Map.of(
            ".html", "text/html; charset=UTF-8",
            ".css", "text/css",
            ".js", "application/javascript",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".svg", "image/svg+xml",
            ".ico", "image/x-icon"
    );

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", PortfolioServer::handleStatic);
        server.createContext("/contact", PortfolioServer::handleContact);

        server.setExecutor(null);
        server.start();
        System.out.println("Portfolio running at http://localhost:" + PORT);
    }

    private static void handleStatic(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            send(exchange, 405, "text/plain", "Method not allowed".getBytes());
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path file = PUBLIC_DIR.resolve(requestPath.substring(1)).normalize();

        // Block path traversal such as /../secret.txt
        if (!file.startsWith(PUBLIC_DIR) || !Files.exists(file) || Files.isDirectory(file)) {
            send(exchange, 404, "text/plain", "404 Not Found".getBytes());
            return;
        }

        String name = file.getFileName().toString();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : "";
        String type = MIME_TYPES.getOrDefault(ext, "application/octet-stream");

        send(exchange, 200, type, Files.readAllBytes(file));
    }

    private static void handleContact(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            send(exchange, 405, "text/plain", "Method not allowed".getBytes());
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        // Keep messages to a sane size so nobody can fill your disk
        if (body.length() > 5000) {
            send(exchange, 413, "text/plain", "Message too long".getBytes());
            return;
        }

        String entry = LocalDateTime.now() + " | " + body.replace("\n", " ") + System.lineSeparator();
        Files.writeString(MESSAGES_FILE, entry,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        send(exchange, 200, "application/json", "{\"ok\":true}".getBytes());
    }

    private static void send(HttpExchange exchange, int status, String type, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}