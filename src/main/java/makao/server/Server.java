package makao.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    private int numberOfPlayers;
    public Server(int port, int numberOfPlayers) {
        this.port = port;
        this.numberOfPlayers = numberOfPlayers;
        try {
            serverSocket = new ServerSocket(port);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(){
        try{
            ServerGame serverGame = new ServerGame();
            Thread gameThread = new Thread(serverGame);
            for(int i = 0; i < numberOfPlayers; i++){
                Socket socket = serverSocket.accept();
                ServerPlayer serverPlayer = new ServerPlayer(socket,serverGame);
                System.out.println("A new client has connected: " + serverPlayer.getClientName());
                serverGame.addServerPlayer(serverPlayer);
                Thread clientThread = new Thread(serverPlayer);
                clientThread.start();
            }
            gameThread.start();
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

    public static void main(String[] args) throws IOException{
        Server server = new Server(4444,2);
        server.startServer();
    }
}

