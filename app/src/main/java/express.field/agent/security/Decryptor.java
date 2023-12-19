package express.field.agent.security;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class Decryptor implements Runnable {
    Map<String, Object> props = null;
    Map<String, Object> map;

    public Decryptor(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public void run() {
        String dataToDecrypt = map.get("cipher").toString();

        byte[] decrypted = Encryption.decrypt(dataToDecrypt);


        // Parse byte array to Map
        ByteArrayInputStream byteIn = new ByteArrayInputStream(decrypted);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            props = (Map<String, Object>) in.readObject();
            props.remove("cipher");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String, Object> getValue() {
        return props;
    }
}
