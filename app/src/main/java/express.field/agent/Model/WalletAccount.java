package express.field.agent.Model;
import java.math.BigDecimal;

public class WalletAccount {
    private String name;
    private String accountName;
    private String accountNumber;
    private BigDecimal balance;

    public WalletAccount() {
        this.name = "";
        this.accountName = "";
        this.accountNumber = "";
        this.balance = BigDecimal.ZERO;
    }

    public WalletAccount(String name, String accountName, String accountNumber, BigDecimal balance) {
        this.name = name;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

