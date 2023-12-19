package express.field.agent.Utils;

import java.util.UUID;


public class OfflineIdGenerator {

    private OfflineIdGenerator() {
    }

    public static String generateUUID() {
        return  UUID.randomUUID().toString();
    }

}
