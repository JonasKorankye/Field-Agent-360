package express.field.agent;

/**
 * Created by jonas korankye on 10/24/2023.
 */
public enum MenuItems {
    BANK_DEPOSIT(0),
    BANK_WITHDRAWAL(1),
    BANK_OPERATION(2),
    AIRTIME_VENDING(3),
    FUNDS(4),
    BILL_PAYMENT(5),
    REVENUE_COLLECTION(6);
    final int position;

    MenuItems(int position) {
        this.position = position;
    }
}
