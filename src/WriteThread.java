import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private Client client;
    private JTextField textField;
    private JTextArea textArea;
    private String username;
    private JFrame jFrame;

    public WriteThread(Socket socket, Client client, JTextArea textArea, JTextField textField, JFrame jFrame) {
        this.socket = socket;
        this.client = client;
        this.textArea = textArea;
        this.textField = textField;
        this.jFrame = jFrame;

        try {
            OutputStream output = socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.textArea.append("\nEnter your name: ");

        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getUsername(String username) {
        this.client.setUserName(username);
        this.writer.println(username);
        this.username = username;
        this.textArea.append("\nWelcome to chat: " + username);
    }

    public void getMessage(String message) throws InterruptedException {
        this.textArea.append("\n" + this.username + ": " + message);
        if (message.equals("exit")) {
            try {
                this.writer.println("I leaves the chat");
                this.socket.close();
                this.jFrame.dispose();
            } catch (IOException ex) {
                System.out.println("Error writing to server: " + ex.getMessage());
            }
        } else {
            writer.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + ">" + message);
        }

    }

}
