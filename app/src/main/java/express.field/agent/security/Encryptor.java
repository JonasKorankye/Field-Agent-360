package express.field.agent.security;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Encryptor implements Runnable {
    Map<String, Object> props = new HashMap<>();
    Map<String, Object> currProps;

    public Encryptor(Map<String, Object> currProps) {
        this.currProps = currProps;
    }

    @Override
    public void run() {
         /*
                1. encrypt currProps to string
                2. make new linkedhashmap with data -> encrypted currProps
                3. replace currProps with this new encrypted value

                 */
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(currProps);
        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] encrypted = Encryption.encrypt(byteOut.toByteArray());
        String data = Base64.encodeToString(
            encrypted,
            Base64.DEFAULT
        );
        props.put("cipher", data);

    }

    public Map<String, Object> getValue() {
        return props;
    }
}
