package express.field.agent.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Formatter {

    private static Formatter mFormatter;

    private Formatter() {
    }

    public static Formatter getInstance() {
        if (mFormatter == null) {
            synchronized (Formatter.class) {
                mFormatter = new Formatter();
            }
        }

        return mFormatter;
    }

    /**
     * Number and currency functions
     */
    public Number formatNumberByDecimal(BigDecimal decimal) {
        Number number = 0;
        try {
            number = NumberFormat.getNumberInstance().parse(String.valueOf(decimal));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return number;
    }

    public Number formatNumberByString(String input) {
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();

        decimalSymbols.setDecimalSeparator('.');

        DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance();
        df.setDecimalFormatSymbols(decimalSymbols);
        df.setGroupingUsed(true);

        Number number = 0;
        try {
            number = df.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return number;
    }

    public Number formatNumberByString(String input, boolean hasDecSeparator) {
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();

        if (hasDecSeparator) {
            decimalSymbols.setDecimalSeparator('.');
            decimalSymbols.setMonetaryDecimalSeparator(',');
        }

        DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance();
        df.setDecimalFormatSymbols(decimalSymbols);


        if (hasDecSeparator) {
            df.setGroupingUsed(true);
        }

        Number number = 0;
        try {
            number = df.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return number;
    }

    public String clearNumberFormatting(String number) {
        return number.replaceAll("[^0-9.,]+", "");
    }

    public String formatCurrency(Number number, String currencyCode, boolean grouping, boolean formatWithCurrencySymbol) {

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();

        formatter.setGroupingUsed(grouping);

        Currency currency = Currency.getInstance(currencyCode);
        formatter.setCurrency(currency);

        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        if (!formatWithCurrencySymbol) {
            symbols.setCurrencySymbol("");
        }

        formatter.setDecimalFormatSymbols(symbols);

        if (!grouping) {
            formatter.setMinimumFractionDigits(0);
        }

        String result = formatter.format(number);
        return result.trim();
    }

    public String formatCurrencyNumber(Number number, String currencyCode, boolean grouping, boolean formatWithCurrencySymbol, boolean hasDecimalSeparator) {

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();

        Currency currency = Currency.getInstance(currencyCode);
        formatter.setCurrency(currency);

        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        if (hasDecimalSeparator) {
            formatter.setGroupingUsed(true);
            symbols.setDecimalSeparator('.');
            symbols.setMonetaryDecimalSeparator(',');
        }

        if (!formatWithCurrencySymbol) {
            symbols.setCurrencySymbol("");
        }

        formatter.setDecimalFormatSymbols(symbols);

        if (!grouping) {
            formatter.setMinimumFractionDigits(0);
        }

        String result = formatter.format(number);
        return result.trim();
    }

    public String formatCurrency(Number number, String currencyCode, boolean grouping, boolean formatWithCurrencySymbol, boolean currencyAtTheBack) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();

        if (!currencyAtTheBack) {
            formatter.setGroupingUsed(grouping);

            Currency currency = Currency.getInstance(currencyCode);
            formatter.setCurrency(currency);

            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            if (!formatWithCurrencySymbol) {
                symbols.setCurrencySymbol("");
            }

            formatter.setDecimalFormatSymbols(symbols);

            if (!grouping) {
                formatter.setMinimumFractionDigits(0);
            }

        }
        String result = formatter.format(number);
        return result.trim();
    }

    public String formatAmount(BigDecimal amount){
        String sAmount;
        return sAmount =  String.format(Locale.getDefault(),"%,.2f", amount.setScale(2, RoundingMode.DOWN));

    }




    /**
     * Date functions
     */
    public String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

    public String formatDateString(String dateObj) throws ParseException {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US);

        return targetFormat.format(sourceFormat.parse(dateObj)) ;
    }

    public String formatTime(Date date) {
        return DateFormat.getTimeInstance().format(date);
    }

    public boolean isToday(long timestamp) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();

        return date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                date.get(Calendar.YEAR) == today.get(Calendar.YEAR);

    }

    public Timestamp getTimeStamp(Date date) {
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);

        return ts;
    }

    public int getOffset() {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        return timeZone.getRawOffset();
    }

    private Date convertDateToGmt(Date date) {
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date(date.getTime() - tz.getRawOffset());

        if (tz.inDaylightTime(ret)) {
            Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

            if (tz.inDaylightTime(dstDate)) {
                ret = dstDate;
            }
        }
        return ret;
    }
}
