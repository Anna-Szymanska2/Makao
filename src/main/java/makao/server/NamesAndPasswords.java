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

    public static void save(NamesAndPasswords namesAndPasswords){
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream("namesAndPasswords.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(namesAndPasswords);
            out.close(); fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NamesAndPasswords restore(){
        NamesAndPasswords namesAndPasswords;
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream("namesAndPasswords.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            namesAndPasswords = (NamesAndPasswords) in.readObject();
            in.close(); fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return namesAndPasswords;
    }
}
