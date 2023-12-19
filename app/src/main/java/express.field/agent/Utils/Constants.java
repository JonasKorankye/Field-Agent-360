package express.field.agent.Utils;

import java.util.Locale;

import express.field.agent.Helpers.ContextProvider;
import okhttp3.MediaType;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class Constants {
    public static final int FUNDS = 0;
    public static final int BILL_PAYMENT = 1;
    public static final int REVENUE_COLLECTION = 2;
    //preference service
    public static final String PREF_NAME = "FA.PREF";



    public static final int FUNDS_C2C = 0;
    public static final int FUNDS_C2A = 1;
    public static final int FUNDS_A2C = 2;
    public static final int FUNDS_A2A = 3;

    //net
    public static final String METHOD = "method";
    public static final String DOWNLOAD_API_SERVICE = "downloadApiService";
    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
    public final static String JSON_RPC = "2.0";
    public final static String BASE_URL = "https://82b7-154-160-5-88.ngrok-free.app/";

    public static final String APK_FOLDER = "update";
    public static final String IMAGES_FOLDER = "images";
    public static final String EXPIRY_DATE = "expiryDate";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ENCRYPTED_SYM_KEY = "encryptedSymKey";
    public static final String BEARER = "Bearer "; //extra space is not a typing mistake - do not remove
    public static final String GRANT_TYPE = "grant_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String KEYSTORE_ALIAS = "dwMobileAuth";

    public static final Locale DEFAULT_LOCALE = new Locale("en", "GH");
    public static final Locale DEFAULT_LOCALE_ARABIC = new Locale("ar", "EG");
    public static final String LANGUAGE_KEY = "lang_key";

    //res
    public final static String LAYOUT_CONSTANT = "layout";
    public final static String STRING_RESOURCE = "string";
    public final static String COLOR_RESOURCE = "color";
    public final static String DRAWABLE_RESOURCE = "drawable";
    public final static String STYLE_RESOURCE = "style";

    //package
    public final static String PACKAGE_NAME = ContextProvider.getInstance().getContext()
            .getPackageName();
    public static final String ENVIRONMENT = "DEV";
    public static final String LOGIN_RESPONSE_PERSON_ACTOR_ID = "actorId";


    //db
    public final static String MODELNAME = "modelname";
    public final static String DATABASE_ID = "id";
    public final static String PROFILE_DB_ID = "agentProfile";
    public final static String ACTOR_ID = "actorId";
    public final static String CBS_ID = "externalId";
    public final static String CUSTOMER_NATIONAL_ID_NUMBER = "nationalIdNumber";
    public final static String CUSTOMER_ADDRESS = "address";
    public final static String CUSTOMER_SERIAL_NUMBER = "serialNumber";
    public final static String CUSTOMER_CITY = "city";
    public final static String CUSTOMER_DISPLAY_ID = "displayId";
    public final static String SYNC_STATUS = "syncStatus";
    public final static String PROCESS_STATUS = "status";
    public final static String ACCOUNT_STATUS = "accountStatus";
    public final static String SYNC_HISTORY_POSTFIX = "_syncHistory";
    public final static String RECORD_ID = "recordId";
    public final static String PRODUCT_ID = "productId";
    public final static String HISTORY_ID = "history";
    public final static String EXIT_FORM_MODEL_KEY = "exitForm";
    public final static String PRODUCT_NAME = "productName";
    public static final String DICTIONARY_DB_ID = "dictionary";
    public static final String CHART_REPORTS_DB_ID = "chart_reports";
    public static final String IMAGES_KEY = "images";
    public static final String DEFAULT_CUSTOMER_PHOTO_KEY = "images[0]";
    public static final String TERMS_KEY = "terms";
    public final static String SYNC_STATUS_KEY = "syncStatus";
    public final static String CITY_KEY = "city";
    public final static String OPENING_DATE_KEY = "openingDate";
    public static final String TRANSLATIONS_DB_ID = "translations_";
    public static final String SETTINGS_DATABASE_ID = "settings";
    public static final String COUNT_KEY = "count";
    public static final int DEFAULT_FILTER_LIMIT_SIZE = 30000;
    public static final String SYNC_HISTORY_MODEL_KEY = "sync_history_model_";
    public static final String SYNC_HISTORY_LAST_DOWNLOAD_TIMESTAMP_KEY = "lastDownloadTimeStamp";
    public static final String SYNC_HISTORY_ITEMS_DOWNLOADED_KEY = "downloadedItems";
    //    public static final String SYNC_HISTORY_DEFAULT_TIMESTMAP = String.valueOf(DateUtils.toDate("1980-01-01", "yyyy-MM-dd").getTime() / 1000L);
    //keep it in milis for now, until BE is fixed to use unix timestamp which is in seconds
    public static final String SYNC_HISTORY_DEFAULT_TIMESTMAP = String.valueOf(DateUtils.toDate("1980-01-01", "yyyy-MM-dd").getTime());

    //date formats
    public static final String DB_DATE_FORMAT_STRING_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String DB_DATE_FORMAT_STRING_SHORT = "yyyy-MM-dd";
    public static final String ERROR_RESPONSE_MESSAGE = "message";
    public static final byte[] APP_SIGNATURE = new byte[]{56, 85, 80, 108, 89, 78, 97, 116, 121, 55, 67, 115, 53, 76, 74, 106, 117, 122, 81, 54, 56, 99, 71, 75, 68, 109, 65, 61};

//
//    public class UrlConstant {
//        public static final String login = "http://208.64.33.151/platform/signin/";
//        public static final String balance = "http://208.64.33.151/platform/Agent/Balance/";
//        public static final String agentActivities = "http://208.64.33.151/platform/Agent/Transaction/";
//        public static final String telco = "http://208.64.33.151/platform/Telcos/All/";
//        public static final String banks = "http://208.64.33.151/platform/Banks/All/";
//        public static final String revenues = "http://208.64.33.151/platform/Revenue/All/";
//        public static final String bills = "http://208.64.33.151/platform/Bills/All/";
//        public static final String fund_transfer = "http://208.64.33.151/platform/Banks/transaction/";
//        public static final String airtime_vending = "http://208.64.33.151/platform/Telcos/transaction/";
//        public static final String bill_payment = "http://208.64.33.151/platform/Bills/transaction/";
//        public static final String revenue_collection = "http://208.64.33.151/platform/Revenue/transaction/";
//        public static final String bank_deposit = "http://208.64.33.151/platform/Banks/deposit/";
//        public static final String bank_withdrawal = "http://208.64.33.151/platform/Banks/withdrawal/";
//        public static final String acct_opening = "http://208.64.33.151/platform/Banks/accountopening/";
//        public static final String agent_roaming_monitoring = "http://208.64.33.151/platform/Agent/roam/";
//    }



    public class UrlConstant {
        public static final String login = "https://82b7-154-160-5-88.ngrok-free.app/login";
        public static final String balance = "http://208.64.33.151/platform/Agent/Balance/";
        public static final String agentActivities = "http://208.64.33.151/platform/Agent/Transaction/";
        public static final String telco = "http://208.64.33.151/platform/Telcos/All/";
        public static final String banks = "http://208.64.33.151/platform/Banks/All/";
        public static final String revenues = "http://208.64.33.151/platform/Revenue/All/";
        public static final String bills = "http://208.64.33.151/platform/Bills/All/";
        public static final String fund_transfer = "http://208.64.33.151/platform/Banks/transaction/";
        public static final String airtime_vending = "http://208.64.33.151/platform/Telcos/transaction/";
        public static final String bill_payment = "http://208.64.33.151/platform/Bills/transaction/";
        public static final String revenue_collection = "http://208.64.33.151/platform/Revenue/transaction/";
        public static final String bank_deposit = "http://208.64.33.151/platform/Banks/deposit/";
        public static final String bank_withdrawal = "http://208.64.33.151/platform/Banks/withdrawal/";
        public static final String acct_opening = "http://208.64.33.151/platform/Banks/accountopening/";
        public static final String agent_roaming_monitoring = "http://208.64.33.151/platform/Agent/roam/";
    }


}
