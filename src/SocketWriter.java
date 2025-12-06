import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class SocketWriter extends Thread {
    private final ObjectOutputStream output;
    private final Scanner scanner;
    private final String username;

    public SocketWriter(Socket socket, String username, Scanner scanner) throws IOException {
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.scanner = scanner;
        this.username = username;
    }

    @Override
    public void run() {
        while(true) {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Message.sendMessage(output, new Message(username, time, scanner.nextLine()));
        }
    }
}
