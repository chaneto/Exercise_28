import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            printUsers();
            String userName = reader.readLine();
            this.server.addUserName(userName);
            String serverMessage = userName + " - joined";
            this.server.broadcast(serverMessage, this);

            while (true) {
                String clientMessage = reader.readLine();
                if (clientMessage.equals("I leaves the chat")) {
                    this.server.removeUser(userName, this);
                    this.socket.close();
                    serverMessage = userName + " - left the chat";
                    this.server.broadcast(serverMessage, this);
                } else {
                    String[] message = clientMessage.split(">");
                    serverMessage = "<" + message[0] + ">" + "-<" + userName + ">:" + "<" + message[1] + ">";
                    this.server.broadcast(serverMessage, this);
                }
            }

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
        }
    }

    void printUsers() {
        if (this.server.hasUsers()) {
            this.writer.println("Connected users: " + this.server.getUserNames());
            for (String out : server.getChatHistory()) {
                this.writer.println(out);
            }
        } else {
            this.writer.println("No users in the chat");
        }
    }

    void sendMessage(String message) {
        this.writer.println(message);
    }
}
