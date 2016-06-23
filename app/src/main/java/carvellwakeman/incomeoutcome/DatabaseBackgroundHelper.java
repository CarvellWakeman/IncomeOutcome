package carvellwakeman.incomeoutcome;


import android.os.AsyncTask;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DatabaseBackgroundHelper extends AsyncTask<Object, String, String> {

    @Override
    protected String doInBackground(Object... params) {
        int action = (int)params[0];

        if (action == 0) //import database
        {
            File importFile = (File) params[1];
            File currentDB = (File) params[2];

            try {
                FileUtils.copyFile(importFile, currentDB);
            } catch (IOException ex){
                ex.printStackTrace();
                ProfileManager.Print("Error importing database");
            }

        }
        else if (action == 1){ //Load settings

        }




        return null;
    }


    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... text) {
    }

}
