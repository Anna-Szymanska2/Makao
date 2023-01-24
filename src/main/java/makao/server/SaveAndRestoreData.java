package makao.server;

import java.io.*;

public class SaveAndRestoreData {
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
