package makao.server;

import java.io.*;
import java.util.HashMap;

public class NamesAndPasswords implements Serializable {
    private HashMap<String,String> namesAndPasswords = new HashMap<>();

    public boolean register(String name, String password) {
        if (namesAndPasswords.containsKey(name)) {
            return false;
        } else {
            namesAndPasswords.put(name, password);
            return true;
        }
    }

    public boolean checkLogin(String name, String password) {
        if (namesAndPasswords.containsKey(name)) {
            if (namesAndPasswords.get(name).equals(password)) {
                System.out.println("Logowanie przebiegło pomyślnie jako: " + name);
                    return true;
                }
            }
        return false;
    }
}
