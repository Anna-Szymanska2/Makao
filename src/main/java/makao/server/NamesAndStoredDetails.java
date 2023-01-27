package makao.server;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

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

    public void addVictory(String name){
        namesAndStoredDetails.get(name).numberWonGames=+1;
    }
    public ArrayList<String> sortVictories(){
        Set<Entry<String, PlayerStoredDetails>> entries = namesAndStoredDetails.entrySet();
        Comparator<Entry<String, PlayerStoredDetails>> victoryComparator = new Comparator<Entry<String,PlayerStoredDetails>>()
        {
            @Override public int compare(Entry<String, PlayerStoredDetails> e1, Entry<String, PlayerStoredDetails> e2) {
                int v1 = e1.getValue().getNumberWonGames(); int v2 = e2.getValue().getNumberWonGames();
                return v1 - v2;
        }
        };
        List<Entry<String, PlayerStoredDetails>>  listOfEntries = new ArrayList<Entry<String, PlayerStoredDetails>> (entries);
        listOfEntries.sort(victoryComparator);
        ArrayList<String> namesAndVictories = new ArrayList<>();
        for(Entry<String, PlayerStoredDetails> entry: listOfEntries){
            String nameAndVictories = entry.getKey() + "\t " +  "\t " + entry.getValue().getNumberWonGames();
            namesAndVictories.add(nameAndVictories);
        }
        return  namesAndVictories;
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

    public static void main(String[] args) {

    }
}
