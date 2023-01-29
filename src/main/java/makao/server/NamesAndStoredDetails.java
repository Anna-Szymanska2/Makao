package makao.server;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * The NamesAndStoredDetails class implements Serializable, and is responsible for storing player information such as name, password, avatar path, and number of won games.
 * The class provides methods for registering new players, checking login credentials, adding victories to a player, sorting players by victories, and returning the avatar path of a player.
 *
 */
public class NamesAndStoredDetails implements Serializable {
    private HashMap<String,PlayerStoredDetails> namesAndStoredDetails = new HashMap<>();

    /**
     * The register method adds a new player to the HashMap with their name, password, and avatar path.
     *
     * @param name The name of the player being registered.
     * @param password The password of the player being registered.
     * @param avatarPath The avatar path of the player being registered.
     * @return A boolean indicating whether the registration was successful or not.
     */
    public boolean register(String name, String password, String avatarPath) {
        if (namesAndStoredDetails.containsKey(name)) {
            return false;
        } else {
            PlayerStoredDetails playerStoredDetails = new PlayerStoredDetails(password,avatarPath, 0 );
            namesAndStoredDetails.put(name, playerStoredDetails);
            return true;
        }
    }

    /**
     * The checkLogin method checks if the provided name and password match a player in the HashMap.
     *
     * @param name The name of the player attempting to login.
     * @param password The password of the player attempting to login.
     * @return A boolean indicating whether the login was successful or not.
     */

    public boolean checkLogin(String name, String password) {
        if (namesAndStoredDetails.containsKey(name)) {
            if (namesAndStoredDetails.get(name).getPassword().equals(password)) {
                    return true;
                }
            }
        return false;
    }

    public void addVictory(String name){
        namesAndStoredDetails.get(name).numberWonGames+=1;
    }

    /**
     * The sortVictories method sorts the players in the HashMap by number of won games in descending order, and returns a list of their names and number of victories.
     *
     * @return An ArrayList of strings containing the names and number of victories of the players in the HashMap.
     */

    public ArrayList<String> sortVictories(){
        Set<Entry<String, PlayerStoredDetails>> entries = namesAndStoredDetails.entrySet();
        Comparator<Entry<String, PlayerStoredDetails>> victoryComparator = new Comparator<Entry<String,PlayerStoredDetails>>()
        {
            @Override public int compare(Entry<String, PlayerStoredDetails> e1, Entry<String, PlayerStoredDetails> e2) {
                int v1 = e1.getValue().getNumberWonGames(); int v2 = e2.getValue().getNumberWonGames();
                return v2 - v1;
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

    /**
     * This is a helper class for NamesAndStoredDetails that stores the details of a player.
     *
     */
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
