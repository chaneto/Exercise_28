import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends JFrame {
    private String hostname;
    private int port;
    private String userName;
    private WriteThread writeThread;
    private ReadThread readThread;
    public JTextArea textArea;
    public JTextField textField;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        setTitle("USERNAME");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 10, 700, 400);

        panel.add(scrollPane);

        this.textField = new JTextField();
        this.textField.setBounds(10, 410, 625, 35);
        this.textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField.setText("");
            }
        });

        panel.add(this.textField);

        JButton submit = new JButton("Send");
        submit.setBounds(635, 410, 75, 33);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeThread.run();
                if (getTitle().equals("USERNAME")) {
                    writeThread.getUsername(textField.getText());
                    readThread.start();
                    textField.setText("");
                    setTitle("CHAT");
                } else {
                    try {
                        writeThread.getMessage(textField.getText());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    textField.setText("");
                }
            }

        });
        panel.add(submit);
        setVisible(true);

    }

    public void execute(JTextArea textArea, JTextField textField, JFrame jFrame) {
        try {
            Socket socket = new Socket(hostname, port);
            textArea.append("Connected to the chat server\n");

            this.readThread = new ReadThread(socket, this, textArea);
            this.writeThread = new WriteThread(socket, this, textArea, textField, jFrame);

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Port or hostname is  null!!!");
            return;
        }
        String hostname = args[1];
        int port = Integer.parseInt(args[0]);
        Client client = new Client(hostname, port);
        client.execute(client.textArea, client.textField, client);
    }
}
