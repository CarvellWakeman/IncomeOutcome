package carvellwakeman.incomeoutcome;


import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DatabaseBackgroundHelper extends AsyncTask<Object, String, String> {

    DatabaseHelper databaseHelper;

    public DatabaseBackgroundHelper(){
        databaseHelper = ProfileManager.getInstance().GetDatabaseHelper();
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Object... params) {
        try {

            int action = (int) params[0];

            switch (action) {
                case 0: //Import database
                    databaseHelper.importDatabase((File) params[1], (boolean) params[2]);
                    break;
                case 1: //Export database
                    databaseHelper.exportDatabase((String) params[1]);
                    break;
                case 2: //Insert setting category
                    databaseHelper.insertSetting((Category) params[1], (boolean) params[2]);
                    break;
                case 3: //Insert setting person
                    databaseHelper.insertSetting((String) params[1], (boolean) params[2]);
                    break;
                case 4: //Insert setting profile
                    databaseHelper.insertSetting((Profile) params[1], (boolean) params[2]);
                    break;
                case 5: //Insert transaction
                    databaseHelper.insert((Profile) params[1], (Transaction) params[2], (boolean) params[3]);
                    break;
                case 6: //Insert timeperiod (Called from Insert Transaction)
                    break;
                case 7: //Query timeperiod (Called from Insert Transaction)
                    break;
                case 8: //Load settings
                    databaseHelper.loadSettings();
                    break;
                case 9: //Load transactions
                    databaseHelper.loadTransactions();
                    break;
                case 10: //Remove setting category
                    databaseHelper.removeCategorySetting((String) params[1]);
                    break;
                case 11: //Remove setting person
                    databaseHelper.removePersonSetting((String) params[1]);
                    break;
                case 12: //Remove setting profile
                    databaseHelper.removeProfileSetting((Profile) params[1]);
                    break;
                case 13: //Remove transaction
                    databaseHelper.remove((Transaction) params[1]);
                    break;
                case 14: //Remove timeperiod
                    break;
                case 15: //Delete database
                    databaseHelper.DeleteDB();
                    break;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... text) {}

    @Override
    protected void onPostExecute(String result) {

    }

}
