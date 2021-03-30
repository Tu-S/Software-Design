package ru.nsu.team.tools;

import java.io.*;

public class Toolkit {

    public static <TIn> byte[] Encode(TIn input) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(input);
        objectOutputStream.flush();
        var bytes = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return bytes;
    }

    public static <TOut> TOut Decode(byte[] input, Class<TOut> clazz) throws IOException, ClassNotFoundException {
        var objectStream = new ObjectInputStream(new ByteArrayInputStream(input));
        var object = objectStream.readObject();
        objectStream.close();
        return clazz.cast(object);
    }
}
