package makao.server;

import java.io.*;

public class SaveAndRestoreData {
    public static void save(NamesAndStoredDetails namesAndStoredDetails){
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream("namesAndStoredDetails.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(namesAndStoredDetails);
            out.close(); fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NamesAndStoredDetails restore(){
        NamesAndStoredDetails namesAndStoredDetails;
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream("namesAndStoredDetails.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            namesAndStoredDetails = (NamesAndStoredDetails) in.readObject();
            in.close(); fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return namesAndStoredDetails;
    }
}
