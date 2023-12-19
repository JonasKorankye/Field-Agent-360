package express.field.agent.Model;


import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Profile {

    private Object modelname;
    private String id;
    private String actorId;
    private String accountOfficer;
    private List<ProfilePermission> permissions;
    private ProfilePerson person;
    private ProfileLanguage language;
    private List<ProfileRole> roles;
    private List<ProfileEmail> emails;
    private Object pushNotificationToken;

    private List<Account> bridgeAccount;
    private List<Account> floatRequestAccount;
    @JsonProperty("commissionAccount")
    private List<Account> commissionAccounts;
    private List<WalletAccount> walletAccounts;
    private BusinessUnit businessUnit;
    private ProfileMetadata metadata;
    private String agentCode;
    private List<ProfilePhone> phones;
    private String transactionThreshold;
    private List<OperatorPermission> allowedOperations;
    @JsonProperty("isOperator")
    private boolean operator;
//    private ProfileOperator operator;

    public ProfileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ProfileMetadata metadata) {
        this.metadata = metadata;
    }

    public List<ProfilePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ProfilePermission> permissions) {
        this.permissions = permissions;
    }

    public ProfilePerson getPerson() {
        return person;
    }

    public void setPerson(ProfilePerson person) {
        this.person = person;
    }

    public ProfileLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProfileLanguage language) {
        this.language = language;
    }

    public List<ProfileRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ProfileRole> roles) {
        this.roles = roles;
    }

    public List<ProfileEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<ProfileEmail> emails) {
        this.emails = emails;
    }

    public Object getPushNotificationToken() {
        return pushNotificationToken;
    }

    public void setPushNotificationToken(Object pushNotificationToken) {
        this.pushNotificationToken = pushNotificationToken;
    }

    public Object getModelname() {
        return modelname;
    }

    public void setModelname(Object modelname) {
        this.modelname = modelname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getAccountOfficer() {
        if (accountOfficer == null) {
            if (person != null && person.getAccountOfficer() != null) {
                return person.getAccountOfficer();
            }
        }

        return accountOfficer;
    }

    public void setAccountOfficer(String accountOfficer) {
        this.accountOfficer = accountOfficer;
    }

    public List<Account> getFloatRequestAccount() {
        return floatRequestAccount;
    }

    public void setFloatRequestAccount(List<Account> floatRequestAccount) {
        this.floatRequestAccount = floatRequestAccount;
    }

    public List<Account> getBridgeAccount() {
        return bridgeAccount;
    }

    public void setBridgeAccount(List<Account> bridgeAccount) {
        this.bridgeAccount = bridgeAccount;
    }

    public String getBridgeAccountNumber() {
        return  bridgeAccount != null && bridgeAccount.size() > 0 && !TextUtils.isEmpty(bridgeAccount.get(0).getAccountNumber()) ? bridgeAccount.get(0).getAccountNumber() : "00000000000";
    }

    public Account getFloatOperatingAccount() {
        for (WalletAccount account : walletAccounts) {
            String name = account.getName();
            if (name.equalsIgnoreCase("floatAccount")) {
                return new Account(account.getAccountNumber(), account.getBalance().toString(), getBridgeAccountNumber());
            }
        }
        return null;
    }

    public Account getMasterWalletAccount() {
        for (WalletAccount account : walletAccounts) {
            String name = account.getName();
            if (name.equalsIgnoreCase("masterWalletAccount")) {
                return new Account(account.getAccountNumber(), account.getBalance().toString(), getBridgeAccountNumber());
            }
        }
        return null;
    }


    public List<Account> getCommissionAccounts() {
        return commissionAccounts;
    }

    public void setCommissionAccounts(List<Account> commissionAccounts) {
        this.commissionAccounts = commissionAccounts;
    }

    public List<WalletAccount> getWalletAccounts() {
        return walletAccounts;
    }

    public void setWalletAccounts(List<WalletAccount> walletAccounts) {
        this.walletAccounts = walletAccounts;
    }

    public BusinessUnit getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(BusinessUnit businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public List<ProfilePhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ProfilePhone> phones) {
        this.phones = phones;
    }

    public String getTransactionThreshold() {
        return transactionThreshold;
    }

    public void setTransactionThreshold(String transactionThreshold) {
        this.transactionThreshold = transactionThreshold;
    }

    public List<OperatorPermission> getAllowedOperations() {
        return allowedOperations;
    }

    public void setAllowedOperations(List<OperatorPermission> allowedOperations) {
        this.allowedOperations = allowedOperations;
    }

    public boolean isOperator() {
        return operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }
}
