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
    private NamesAndPasswords namesAndPasswords;
    private ArrayList<ServerGame> games = new ArrayList<>();
    public Server(int port, int numberOfPlayers) {
        this.port = port;
        this.numberOfPlayers = numberOfPlayers;
        try {
            serverSocket = new ServerSocket(port);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public Server(int port, int numberOfPlayers, NamesAndPasswords namesAndPasswords) {
        this.port = port;
        this.numberOfPlayers = numberOfPlayers;
        this.namesAndPasswords = namesAndPasswords;
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
                System.out.println("A new client has connected: " + serverPlayer.getClientName());
                Thread clientThread = new Thread(serverPlayer);
                clientThread.start();

                /*ServerGame serverGame = new ServerGame();
                Thread gameThread = new Thread(serverGame);
                for (int i = 0; i < numberOfPlayers; i++) {
                    //Socket socket = serverSocket.accept();
                    serverGame.addServerPlayer(serverPlayer);
                }
                gameThread.start();*/
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

    public NamesAndPasswords getNamesAndPasswords() {
        return namesAndPasswords;
    }

    public ArrayList<ServerGame> getGames() {
        return games;
    }

    public static void main(String[] args) throws IOException{
        NamesAndPasswords namesAndPasswords = new NamesAndPasswords();
        File file = new File("namesAndPasswords.ser");
        if (file.exists()) {
            namesAndPasswords = SaveAndRestoreData.restore();
        }
        Server server = new Server(4444,2, namesAndPasswords);
        server.startServer();
    }
}

