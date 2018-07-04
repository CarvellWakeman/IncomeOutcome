package carvellwakeman.incomeoutcome.models;

import org.joda.time.LocalDate;

public class BlacklistDate implements java.io.Serializable
{
    public LocalDate date;
    public Boolean edited;
    public int transactionID;
    public BlacklistDate(int _transactionID, LocalDate _date, Boolean _edited) {
        transactionID = _transactionID;
        date = _date;
        edited = _edited;
    }
}