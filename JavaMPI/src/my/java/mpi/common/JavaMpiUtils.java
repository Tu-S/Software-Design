package my.java.mpi.common;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public final class JavaMpiUtils {
    private JavaMpiUtils() {}

    public static <TIn extends Serializable> byte[] Encode(TIn input) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(input);
        objectOutputStream.flush();
        var bytes = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return bytes;
    }

    public static <TOut extends Serializable> TOut Decode(byte[] input, Class<TOut> clazz) throws IOException, ClassNotFoundException {
        var objectStream = new ObjectInputStream(new ByteArrayInputStream(input));
        var object = objectStream.readObject();
        objectStream.close();
        return clazz.cast(object);
    }

    public static String GetAddress(Socket socket) {
        return  String.format("%s:%d", socket.getInetAddress().getHostName(), socket.getPort());
    }


    public static <TIn> byte[] testEncode(TIn input) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(input);
        objectOutputStream.flush();
        var bytes = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return bytes;
    }

    public static <TOut> TOut testDecode(byte[] input, Class<TOut> clazz) throws IOException, ClassNotFoundException {
        var objectStream = new ObjectInputStream(new ByteArrayInputStream(input));
        var object = objectStream.readObject();
        objectStream.close();
        return clazz.cast(object);
    }
}
