package express.field.agent.Utils;



public class AuthenticationEncoder {

    public static String encode(char[] plain) {
        try {
            return PasswordHash.createHash(plain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateHash(char[] plain, String correctHash) {
        try {
            return PasswordHash.validatePassword(plain, correctHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
