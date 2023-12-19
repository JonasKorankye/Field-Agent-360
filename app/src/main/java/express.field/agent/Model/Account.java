package express.field.agent.Model;

public class Account {
    private String accountNumber;
    private String balance;
    private String cbsAccount;

    public Account() {
    }

    public Account(String accountNumber, String balance, String cbsAccount) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.cbsAccount = cbsAccount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCbsAccount() {
        return cbsAccount;
    }

    public void setCbsAccount(String cbsAccount) {
        this.cbsAccount = cbsAccount;
    }
}

