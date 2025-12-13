package Communication;

import User.CommandManager;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread {
    private ObjectOutputStream outputStream;
    private final Scanner scanner;
    private String username;

    public SocketWriter(Socket socket, Scanner scanner, String username) {
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Couldn't start writing! IOException: " + e.getMessage());
        }
        this.scanner = scanner;
        this.username = username;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                String input = scanner.nextLine();
                // If user entered a command character
                if(input.charAt(0) == '/') {
                    CommandManager.executeCommand(outputStream, input);
                    continue;
                }
                ChatMessage.sendMessage(outputStream, username, input);
            } catch (IOException e) {
                System.out.println("Connection lost in SocketWriter! IOException: " + e.getMessage());
                break;
            }
        }
    }

    public void updateUsername(String username) {
        this.username = username;
    }
}
