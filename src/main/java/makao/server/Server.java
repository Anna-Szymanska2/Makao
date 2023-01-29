package makao.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The Server class is responsible for handling the socket connections and creating new game rooms
 * for players to play in. It also contains methods for closing the server socket and accessing the
 * NamesAndStoredDetails object containing player information.
 *
 */
public class Server {
    private ServerSocket serverSocket;
    private int port;
    private int numberOfPlayers;
    private NamesAndStoredDetails namesAndStoredDetails;
    private ArrayList<ServerGame> games = new ArrayList<>();

    public Server(int port, int numberOfPlayers, NamesAndStoredDetails namesAndStoredDetails) {
        this.port = port;
        this.numberOfPlayers = numberOfPlayers;
        this.namesAndStoredDetails = namesAndStoredDetails;
        try {
            serverSocket = new ServerSocket(port);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the server and waits for incoming socket connections. When a new connection is
     * received, a new ServerPlayer thread is created to handle it.
     *
     */
    public void startServer(){
        try{
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ServerPlayer serverPlayer = new ServerPlayer(socket, this);
                Thread clientThread = new Thread(serverPlayer);
                clientThread.start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket!=null)
                serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public NamesAndStoredDetails getNamesAndPasswords() {
        return namesAndStoredDetails;
    }

    public ArrayList<ServerGame> getGames() {
        return games;
    }

    /**
     * The main method that starts the server by creating a new Server object and calling the
     * startServer() method.
     *
     * @param args command line arguments
     * @throws IOException if an I/O error occurs when creating the server socket.
     */
    public static void main(String[] args) throws IOException{
        NamesAndStoredDetails namesAndStoredDetails = new NamesAndStoredDetails();
        File file = new File("namesAndStoredDetails.ser");
        if (file.exists()) {
            namesAndStoredDetails = SaveAndRestoreData.restore();
        }
        Server server = new Server(4444,2, namesAndStoredDetails);
        server.startServer();
    }
}

