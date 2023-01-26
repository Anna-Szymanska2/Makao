package makao.server;

import java.io.*;
import java.util.HashMap;

public class NamesAndStoredDetails implements Serializable {
    private HashMap<String,PlayerStoredDetails> namesAndStoredDetails = new HashMap<>();

    public boolean register(String name, String password, String avatarPath) {
        if (namesAndStoredDetails.containsKey(name)) {
            return false;
        } else {
            PlayerStoredDetails playerStoredDetails = new PlayerStoredDetails(password,avatarPath, 0 );
            namesAndStoredDetails.put(name, playerStoredDetails);
            return true;
        }
    }

    public boolean checkLogin(String name, String password) {
        if (namesAndStoredDetails.containsKey(name)) {
            if (namesAndStoredDetails.get(name).getPassword().equals(password)) {
                System.out.println("Logowanie przebiegło pomyślnie jako: " + name);
                    return true;
                }
            }
        return false;
    }

    public String returnAvatar(String name){
        return namesAndStoredDetails.get(name).getAvatarPath();
    }

    private class PlayerStoredDetails implements Serializable{
        private String password;
        private String avatarPath;
        private int numberWonGames;

        public PlayerStoredDetails(String password, String avatarPath, int numberWonGames) {
            this.password = password;
            this.avatarPath = avatarPath;
            this.numberWonGames = numberWonGames;
        }

        public String getPassword() {
            return password;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public int getNumberWonGames() {
            return numberWonGames;
        }
    }
}
