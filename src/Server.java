import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private int port;
    public PrintWriter writer;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private List<String> chatHistory = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("New user connected");
                this.writer.println("New user connected");
                UserThread newUser = new UserThread(socket, this);
                this.userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Port is null!!!");
            return;
        }

        Server server = new Server(Integer.parseInt(args[0]));
        server.execute();
    }

    void broadcast(String message, UserThread excludeUser) {
        this.chatHistory.add(message);
        for (UserThread user : this.userThreads) {
            if (user != excludeUser) {
                user.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        this.userNames.add(userName);
    }

    void removeUser(String userName, UserThread user) {
        boolean removed = this.userNames.remove(userName);
        if (removed) {
            this.userThreads.remove(user);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    public List<String> getChatHistory() {
        return this.chatHistory;
    }
}
