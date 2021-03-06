package carvellwakeman.incomeoutcome.models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChangelogChange {
    //Constants
    public static String VERSION_INDICATOR = "ver ";

    public static String REGEX_VERSION = "[0-9]\\.[0-9]\\.[0-9]";
    public static String REGEX_VERSION2 = "[0-9]\\.[0-9]";
    public static String REGEX_DATE = "\\d{4}-\\d{2}-\\d{2}";

    public static String REGEX_ADD = "\\*\\[ADD\\]";
    public static String REGEX_SUB = "\\*\\[SUB\\]";
    public static String REGEX_SUB2 = "\\*\\[REMOVE\\]";
    public static String REGEX_CHANGE = "\\*\\[CHANGE\\]";
    public static String REGEX_FIX = "\\*\\[.*[FIX].*\\]";
    public static String REGEX_NOTE = "\\*-";

    //Data
    public String version = "Unknown";
    public String versionChannel = "Unknown";
    public String date = "Unknown";

    public List<String> notes = new ArrayList<>();
    public List<String> fixes = new ArrayList<>();
    public List<String> changes = new ArrayList<>();
    public List<String> additions = new ArrayList<>();
    public List<String> subtractions = new ArrayList<>();

    //Helpers
    Pattern p;
    Matcher m;

    public ChangelogChange(){}

    //FORM:
    //Ver #.#.# CHANNEL DATE
    public void ParseTitleString(String line){
        try {
            //Remove version header
            line = line.replace("ver ", "Ver ");
            line = line.replace("Ver ", "");

            //Version
            p = Pattern.compile(REGEX_VERSION);
            m = p.matcher(line);
            if (m.find()) {
                version = m.group();
            }
            else {
                p = Pattern.compile(REGEX_VERSION2);
                m = p.matcher(line);
                if (m.find()) { version = m.group(); }
            }

            //Date
            p = Pattern.compile(REGEX_DATE);
            m = p.matcher(line);
            if (m.find()) { date = m.group(); }

            //Extract version and date to get channel
            versionChannel = line.replace(version, "").replace(date, "").trim();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void ParseEntry(String line){
        try {
            //Add
            p = Pattern.compile(REGEX_ADD, Pattern.CASE_INSENSITIVE);
            m = p.matcher(line);
            if (m.find()) {
                additions.add(line.replace(m.group(), "").trim());
                return;
            }
            //Sub
            p = Pattern.compile(REGEX_SUB, Pattern.CASE_INSENSITIVE);
            m = p.matcher(line);
            if (m.find()) {
                subtractions.add(line.replace(m.group(), "").trim());
                return;
            }
            p = Pattern.compile(REGEX_SUB2, Pattern.CASE_INSENSITIVE);
            m = p.matcher(line);
            if (m.find()) {
                subtractions.add(line.replace(m.group(), "").trim());
                return;
            }
            //ChangelogChange
            p = Pattern.compile(REGEX_CHANGE, Pattern.CASE_INSENSITIVE);
            m = p.matcher(line);
            if (m.find()) {
                changes.add(line.replace(m.group(), "").trim());
                return;
            }
            //Fix
            p = Pattern.compile(REGEX_FIX, Pattern.CASE_INSENSITIVE);
            m = p.matcher(line);
            if (m.find()) {
                fixes.add(line.replace(m.group(), "").trim());
                return;
            }
            //Note
            p = Pattern.compile(REGEX_NOTE);
            m = p.matcher(line);
            if (m.find()) {
                notes.add(line.replace(m.group(), "").trim());
                return;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
