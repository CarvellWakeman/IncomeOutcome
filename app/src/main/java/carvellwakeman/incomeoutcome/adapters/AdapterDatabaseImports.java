package carvellwakeman.incomeoutcome.adapters;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.data.DatabaseManager;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentManageBPC;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.interfaces.ParentCallBack;
import carvellwakeman.incomeoutcome.activities.ActivityDatabaseImport;
import carvellwakeman.incomeoutcome.viewholders.ViewHolderTextUnderline;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdapterDatabaseImports extends RecyclerView.Adapter<AdapterDatabaseImports.FileViewHolder>
{
    private ActivityDatabaseImport parent;
    private ArrayList<File> database_import_files;

    public AdapterDatabaseImports(ActivityDatabaseImport _parent)
    {
        parent = _parent;

        getFiles();
    }

    private void getFiles(){
        database_import_files = DatabaseManager.getInstance(parent).getImportableDatabases();

        if (database_import_files != null && database_import_files.size() > 0) {
            Collections.sort(database_import_files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) { return (int) Math.signum((f2.lastModified()) - (f1.lastModified())); }
            });
        }
    }

    //When creating a view holder
    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);
        return new FileViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final FileViewHolder holder, int position)
    {
        getFiles();

        if (database_import_files != null && position < database_import_files.size()) {
            File file = database_import_files.get(position);
            if (file != null && file.exists()) {
                int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
                int currentVersion = DatabaseManager.getInstance(parent).getVersion();

                holder.title.setText(file.getName());
                holder.subTitle.setText( new LocalDate(file.lastModified()).toString(parent.getString(R.string.date_format)) );
                holder.subTitle.setVisibility(View.VISIBLE);
                holder.subTitle2.setText(parent.getString(R.string.format_dbversion, String.valueOf(version)));
                holder.subTitle2.setVisibility(View.VISIBLE);

                holder.icon.setImageDrawable(Helper.getDrawable(R.drawable.ic_file_white_24dp));


                if (version == currentVersion){ holder.subTitle2.setTextColor(Helper.getColor(R.color.darkgreen)); }
                else { holder.subTitle2.setTextColor(Color.RED); }
            }
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        if (database_import_files != null) { return database_import_files.size(); }
        else { return -1; }
    }


    //View Holder class
    public class FileViewHolder extends ViewHolderTextUnderline implements View.OnClickListener
    {
        FileViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            //getFiles();

            if (getAdapterPosition() < getItemCount()) {
                File file = database_import_files.get(getAdapterPosition());
                if (file != null && file.exists()) {
                    int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();

                    Helper.OpenDialogFragment(parent, DialogFragmentManageBPC.newInstance(parent, file.getName(), parent.getString(R.string.format_dbversion, String.valueOf(version)), file.getAbsolutePath(),
                            null, parent.getString(R.string.action_import), null,
                            null, new ParentCallBack() {
                        @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DBImport(data, dialogFragment); }
                    }, new ParentCallBack() {
                        @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DBDelete(data, dialogFragment); }
                    }), true); //TODO: Handle mIsLargeDisplay
                }
            }

        }
    }
}
