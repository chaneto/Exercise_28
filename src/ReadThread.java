import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread{
    private BufferedReader reader;
    private Socket socket;
    private Client client;
    private JTextArea textArea;

    public ReadThread(Socket socket, Client client, JTextArea textArea) {
        this.socket = socket;
        this.client = client;
        this.textArea = textArea;

        try {
            InputStream input = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                String response = this.reader.readLine();
                System.out.println("\n" + response);
                this.textArea.append("\n" + response);
                if (this.client.getUserName() != null) {
                   // textArea.append(client.getUserName() + ": ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                break;
            }
        }
    }
}
