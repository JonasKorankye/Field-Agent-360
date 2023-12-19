package express.field.agent.Request.net;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UtResponseJWE extends JsonObject {

    @JsonProperty("protected")
    private String protectedField;

    private String iv;

    @JsonProperty("ciphertext")
    private String cipherText;

    private String tag;
    private List<Recipient> recipients;

    public static class Recipient {
        @JsonProperty("encrypted_key")
        private String encryptedKey;

        public String getEncryptedKey() {
            return encryptedKey;
        }

        public void setEncryptedKey(String encryptedKey) {
            this.encryptedKey = encryptedKey;
        }
    }

    public String getProtectedField() {
        return protectedField;
    }

    public void setProtectedField(String protectedField) {
        this.protectedField = protectedField;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }
}
