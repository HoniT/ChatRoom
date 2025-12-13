package User;

import Communication.ChangeNameRequest;
import Communication.PrivateMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/// Manages user commands
public class CommandManager {
    private static final Map<String, Consumer<List<String>>> commands = Map.of(
            "/name", CommandManager::changeName,
            "/pm", CommandManager::privateMessage,
            "/help", CommandManager::help,
            "/rejoin", CommandManager::rejoin,
            "/exit", CommandManager::exit
    );

    private static ObjectOutputStream _output;

    /// Gets users current input and matches it to a command
    public static void executeCommand(ObjectOutputStream output, String input) {
        _output = output;

        List<String> tokens = tokenize(input);
        if (tokens.isEmpty()) return;

        // Getting command name from input (tokens[0])
        String command = tokens.getFirst();
        List<String> params = tokens.subList(1, tokens.size());

        // Matching and executing
        Consumer<List<String>> action = commands.get(command);
        if (action != null) {
            action.accept(params);
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    private static void changeName(List<String> params) {
        if (params.isEmpty()) {
            System.out.println("Syntax: /name <newName>");
            return;
        }

        String newName = params.getFirst();
        try {
            // Sending request to change name records in server
            ChangeNameRequest.sendNameChangeRequest(_output, ChatUser.getUsername(), newName);
        } catch (IOException e) {
            System.out.println("Couldn't change name do to IOException: " + e.getMessage());
        }
        ChatUser.changeUsername(newName);
    }

    private static void privateMessage(List<String> params) {
        // Getting params
        String destUsername = params.getFirst();
        String payload = "";
        if (params.size() > 1) {
            payload = String.join(" ", params.subList(1, params.size()));
        }

        try {
            PrivateMessage.sendPrivateMessage(_output, ChatUser.getUsername(), destUsername, payload);
        } catch (IOException e) {
            System.out.println("Couldn't send private message do to IOException: " + e.getMessage());
        }
    }

    private static void help(List<String> params) {
        System.out.println("Available commands:");

        for (String cmd : commands.keySet()) {
            System.out.println("  " + cmd);
        }
    }

    private static void rejoin(List<String> params) {
        ChatUser.rejoinChatroom();
    }

    private static void exit(List<String> params) {
        ChatUser.exitChatroom();
    }

    /// Helper method to tokenize user input
    private static List<String> tokenize(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .toList();
    }
}
