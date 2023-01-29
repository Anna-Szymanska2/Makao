package makao.server;

import java.io.*;

/**
 * The SaveAndRestoreData class provides methods to save and restore the state of a NamesAndStoredDetails object to/from a binary file.
 *
 */
public class SaveAndRestoreData {

    /**
     * Saves the state of a NamesAndStoredDetails object to a binary file
     *
     * @param namesAndStoredDetails the object to be saved
     */
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

    /**
     * Restores the state of a NamesAndStoredDetails object from a binary file
     *
     * @return the restored NamesAndStoredDetails object
     */
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
