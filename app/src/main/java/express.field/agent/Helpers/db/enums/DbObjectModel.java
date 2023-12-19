package express.field.agent.Helpers.db.enums;

public enum DbObjectModel {
    CUSTOMER("example/afc_demo/customer/afc_customer_schema.json"),
    POTENTIAL_CUSTOMER("example/afc_demo/customer/afc_customer_schema.json"),
    CORPORATION,
    CUSTOMER_DOCUMENT,
    ACCOUNT_DOCUMENT,
    LOAN_APPLICATION_DOCUMENT,
    TASK_DOCUMENT,
    ATTACHMENT,
    ACCOUNT(""),
    GROUP(""),
    LOAN_APPLICATION("example/afc_demo/loan_app/afc_loan_application_schema.json"),
    LOAN,
    PROFILE,
    DICTIONARY,
    TERMS_AND_CONDITIONS,
    GROUP_LOAN_REPAYMENT("example/account/transaction/group_loan_repayment_form.json", "example/account/transaction/receipts/balance"),
    LABELS,
    IMAGE,
    IMAGES,
    BIO,
    TASK,
    CHART_REPORTS,
    COMPLETED_TASK,
    ATTENDEE_BIO("example/group/bio_attendee.json"),
    CONFIGURATION,
    GROUP_COLLECTION_SHEET,
    GROUP_MEETING,
    CUSTOMER_VISIT,
    NOTIFICATION_LIST,
    SYNC_HISTORY,
    SETTINGS("settings_screen_items.json"),
    REMOVABLE_MEMBER("example/group/remove_member_items.json"),
    NOT_RECOGNIZED,
    DEPOSIT("example/account/transaction/deposit_form.json", "example/account/transaction/receipts/deposit"),
    WITHDRAWAL(""),
    BALANCE_ENQUIRY("example/account/transaction/balance_form.json", "example/account/transaction/receipts/balance"),
    MINI_STATEMENT("example/account/transaction/mini_statement_form.json", "example/account/transaction/receipts/mini_statement"),
    LOAN_REPAYMENT("example/loan/transaction/loan_repayment_form.json", "example/loan/transaction/receipts/loan_repayment"),
    CONFIGURABLE_PARAMETERS;

    private String uiSchemaName;
    private String extraAssetFileName;


    DbObjectModel() {
    }

    DbObjectModel(String uiSchemaName) {
        this.uiSchemaName = uiSchemaName;
    }

    DbObjectModel(String uiSchemaName, String extraAssetFileName) {
        this.uiSchemaName = uiSchemaName;
        this.extraAssetFileName = extraAssetFileName;
    }


    @Override
    public String toString() {
        return name();
    }

    public boolean is(String modelname) {
        return name().equals(modelname);
    }

    public boolean is(DbObjectModel modelname) {
        return this == modelname;
    }

    public boolean is(Object modelname) {
        if (modelname instanceof String) {
            return is((String) modelname);
        } else {
            return modelname == this;
        }
    }

    public static DbObjectModel valueOf(Object modelname) {
        if (modelname instanceof String) {
            String model = (String) modelname;
            for (DbObjectModel value : values()) {
                if (value.toString().equalsIgnoreCase(model)) {
                    return value;
                }
            }
        } else if (modelname instanceof DbObjectModel) {
            return (DbObjectModel) modelname;
        }


        return NOT_RECOGNIZED;
    }

    public static boolean isValidModel(final String modelName) {
        return valueOf(modelName) != NOT_RECOGNIZED;
    }

    public String getUiSchemaName() {
        return uiSchemaName;
    }

    public void setUiSchemaName(String uiSchemaName) {
        this.uiSchemaName = uiSchemaName;
    }

    public String getExtraAssetFileName() {
        return extraAssetFileName;
    }

    public void setExtraAssetFileName(String extraAssetFileName) {
        this.extraAssetFileName = extraAssetFileName;
    }

    public static String getAsFormatedString(DbObjectModel modelName) {
        if (isValidModel(modelName.toString())) {
            return modelName.toString().replace("_", " ").toLowerCase();
        } else {
            return null;
        }
    }
}
