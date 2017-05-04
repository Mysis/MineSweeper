package minesweeper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class MineSweeperFiles implements Serializable {
    
    public static Object readSerializedFile(File file) throws IOException, ClassNotFoundException {
        Object returnObject;
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        returnObject = objectIn.readObject();
        fileIn.close();
        objectIn.close();
        return returnObject;
    }
    
    public static void writeSerializedObject(Object object, File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(object);
        fileOut.close();
        objectOut.close();
    }
}