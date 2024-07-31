package vvkm;

import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.FileReader;

public class VVKM {

    // Hardcoded credentials
    String password = "123456";

    public static void main(String[] args) throws IOException {
        if (args[1] == "Brot") {
            System.out.println("Brot");
        }
        VVKM main = new VVKM();
        main.web_server();

        // SQL Injection vulnerability
        String userInput = "1 OR 1=1";
        String query = "SELECT * FROM users WHERE id = " + userInput;

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insecure deserialization
        try {
            byte[] data = new byte[]{/* serialized object data */};
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Null Pointer Dereference
        String nullString = null;
        System.out.println(nullString.length());

        // Resource Leak
        try {
            FileInputStream fis = new FileInputStream("somefile.txt");
            // No close() call
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Buffer Overflow
        int[] array = new int[10];
        for (int i = 0; i <= 10; i++) {
            array[i] = i;
        }

        // Command Injection
        String command = args[1];
        Runtime.getRuntime().exec(command);

        // Path Traversal
        String filePath = args[2];
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // XSS vulnerability
    public String getUserInput(String input) {
        return "<html><body>" + input + "</body></html>";
    }

    public void web_server() throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(null, 8080);

        server.createContext("/", exchange -> {
            String response = getUserInput(exchange.getRequestURI().getQuery());
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
    }
}