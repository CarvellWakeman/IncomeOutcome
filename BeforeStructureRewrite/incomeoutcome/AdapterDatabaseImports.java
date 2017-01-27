package carvellwakeman.incomeoutcome;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdapterDatabaseImports extends RecyclerView.Adapter<AdapterDatabaseImports.FileViewHolder>
{
    ActivityDatabaseImport parent;
    ArrayList<File> database_import_files;

    public AdapterDatabaseImports(ActivityDatabaseImport _parent)
    {
        parent = _parent;

        getFiles();
    }

    public void getFiles(){
        database_import_files = ProfileManager.getInstance().GetImportDatabaseFiles(parent);

        Collections.sort(database_import_files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) { return (int)Math.signum( (f2.lastModified()) - (f1.lastModified()) ); }
        });
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
                int currentVersion = ProfileManager.getInstance().GetNewestDatabaseVersion(parent);

                holder.title.setText(file.getName());
                holder.subTitle.setText( new LocalDate(file.lastModified()).toString(ProfileManager.simpleDateFormat) );
                holder.subTitle.setVisibility(View.VISIBLE);
                holder.subTitle2.setText(parent.getString(R.string.format_dbversion, String.valueOf(version)));
                holder.subTitle2.setVisibility(View.VISIBLE);

                holder.icon.setImageDrawable(ProfileManager.getDrawable(R.drawable.ic_file_white_24dp));
                holder.secondaryIcon.setVisibility(View.GONE);



                if (version == currentVersion){ holder.subTitle2.setTextColor(ProfileManager.getColor(R.color.darkgreen)); }
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
        public FileViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            //getFiles();

            if (getAdapterPosition() < getItemCount()) {
                File file = database_import_files.get(getAdapterPosition());
                if (file != null && file.exists()) {
                    int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();

                    ProfileManager.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, file.getName(), parent.getString(R.string.format_dbversion, String.valueOf(version)), file.getAbsolutePath(), null, new ProfileManager.ParentCallback() {
                        @Override
                        public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DBImport(data, dialogFragment); }
                    }, new ProfileManager.ParentCallback() {
                        @Override
                        public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DBDelete(data, dialogFragment); }
                    }), true); //TODO: Handle mIsLargeDisplay
                }
            }

        }
    }
}