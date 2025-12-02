import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread {
    private final DataOutputStream output;
    private final Scanner scanner;
    private final String username;

    public SocketWriter(Socket socket, String username, Scanner scanner) throws IOException {
        this.output = new DataOutputStream(socket.getOutputStream());
        this.scanner = scanner;
        this.username = username;
    }

    @Override
    public void run() {
        while(true) {
            Message.sendMessage(output, username, scanner.nextLine());
        }
    }
}
