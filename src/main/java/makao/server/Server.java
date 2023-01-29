package makao.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

