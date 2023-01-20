package makao.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private NamesAndPasswords namesAndPasswords;

    ClientHandler(Socket socket,NamesAndPasswords namesAndPasswords) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.namesAndPasswords = namesAndPasswords;
    }

    public void run() {
        try {
            ClientMessage clientMessage;
            clientMessage = (ClientMessage) in.readObject();
            String action = clientMessage.getActionID();
            String username = clientMessage.getPlayerName();
            String password = clientMessage.getPassword();
            if (action.equals("REGISTER")) {
                if(namesAndPasswords.register(username, password)){
                    ServerMessage serverMessage = new ServerMessage("REGISTER_OK");
                    sendServerMessage(serverMessage);
                }
                ServerMessage serverMessage = new ServerMessage("REGISTER_WRONG");
                sendServerMessage(serverMessage);
            } else if (action.equals("login")) {
                if(namesAndPasswords.checkLogin(username, password)){
                    ServerMessage serverMessage = new ServerMessage("LOGIN_OK");
                    sendServerMessage(serverMessage);
                }
                ServerMessage serverMessage = new ServerMessage("LOGIN_WRONG");
                sendServerMessage(serverMessage);
            }
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendServerMessage(ServerMessage serverMessage){
        try {
            out.writeObject(serverMessage);
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
