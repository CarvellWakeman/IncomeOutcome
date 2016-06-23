package carvellwakeman.incomeoutcome;

import org.joda.time.LocalDate;

public class BlacklistDate implements java.io.Serializable
{
    public LocalDate date;
    public Boolean edited;
    public BlacklistDate(LocalDate _date, Boolean _edited) {
        this.date = _date;
        this.edited = _edited;
    }
}