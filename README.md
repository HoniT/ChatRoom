# Java Chatroom Application

A simple console-based chat system implemented in Java, featuring a multi-client chatroom server and individual user clients. Users join anonymously, can set custom usernames, broadcast messages to everyone, or start private conversations with specific users.

---

## Overview

The application is built around two main classes:

### 1. Chatroom (Server)
The Chatroom class represents the central server. It:

- Accepts multiple user connections
- Forwards messages to all connected users
- Handles private message routing
- Manages join/leave notifications

### 2. User.ChatUser (Client)
The User.ChatUser class represents each individual user. It:

- Connects to the Chatroom server
- Sends messages or commands
- Receives real-time messages from the server
- Allows changing the display name
- Supports private messaging to specific users

---

## How It Works

1. **Start the server**  
   Run the Chatroom class to open a listening socket.

2. **Start any number of clients**  
   Each User.ChatUser instance connects to the running server.

3. **Users start with anonymous names**  
   Example: `Anonymous User 123`

4. **Users can set a custom name**  
   Using a command (see command table).

5. **Public Chat**  
   By default, every message a user sends is broadcast to the whole chatroom.

6. **Private Messaging**  
   Users can directly message a specific person using the private message command.

---

## Features

✔ Multi-client chat support  
✔ Real-time messaging  
✔ Anonymous auto-naming  
✔ User-defined name changes  
✔ Private messaging between users  
✔ Clean, extensible code structure

---

## Commands

| Command                    | Description                               | Example                 |
|----------------------------|-------------------------------------------|-------------------------|
| `/name <newName>`          | Change your current username              | `/name George`          |
| `/pm <username> <message>` | Send a private message to a specific user | `/pm Alex hello there!` |
| `/help`                    | Show available commands                   | `/help`                 |
| `/rejoin`                  | Reconnects to server                      | `/rejoin`               |
| `/exit`                    | Disconnect from the chatroom              | `/exit`                 |

