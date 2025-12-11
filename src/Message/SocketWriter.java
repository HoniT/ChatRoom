package Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread {
    private ObjectOutputStream outputStream;
    private final Scanner scanner;
    private final String username;

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
        while(true) {
            try {
                Message.sendMessage(outputStream, username, scanner.nextLine());
            } catch (IOException e) {
                System.out.println("Connection lost in Message.SocketWriter! IOException: " + e.getMessage());
                break;
            }
        }
    }
}
