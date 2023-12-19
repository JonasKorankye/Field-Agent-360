package express.field.agent.Utils;

import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class StreamUtils {

    public static byte[] read(InputStream in) throws IOException {
        InputStream bin = new BufferedInputStream(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buff = new byte[4096];
        int n = 0;
        while ((n = in.read(buff)) >= 0) {
            out.write(buff, 0, n);
        }
        bin.close();

        return out.toByteArray();
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }

        return out.toString();
    }

    /**
     * Securely extract String from editText view, e.g. username and password.
     * Avoids String so no caching in the String Pool happens
     * @param exitText
     * @return - char array of the content
     */
    public static char[] toCharArray(EditText exitText) {
        int length = exitText.length();
        char[] value = new char[length];
        exitText.getText().getChars(0, length, value, 0);

        return value;
    }


    public static char[] toChars(byte[] chars) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(chars);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);

        char[] res = new char[charBuffer.length()];

        for (int i = 0; i < charBuffer.length(); i++) {
            if (charBuffer.array()[i] != '\u0000') {
                res[i] = charBuffer.get(i);
            }
        }

        return res;
    }
}
