package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class AdapterDatabaseImports extends RecyclerView.Adapter<AdapterDatabaseImports.FileViewHolder>
{
    DialogFragmentDatabaseImport parent;

    View selectedView;
    int selectedViewPosition;

    ArrayList<File> database_import_files;

    //Constructor
    public AdapterDatabaseImports(DialogFragmentDatabaseImport _parent)
    {
        selectedViewPosition = -1;

        parent = _parent;

        database_import_files = ProfileManager.GetImportDatabaseFiles();
    }


    //When creating a view holder
    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text2_wdelete, parent, false);

        return new FileViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final FileViewHolder holder, int position)
    {
        if (database_import_files != null) {
            //File
            File file = database_import_files.get(position);
            if (file != null) {
                int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
                int currentVersion = ProfileManager.GetNewestDatabaseVersion();

                //Textview1
                holder.textView1.setText(file.getName());
                //Textview2
                holder.textView2.setText(parent.getString(R.string.format_dbversion, version));
                if (version == currentVersion){ holder.textView2.setTextColor(Color.GREEN); }
                else { holder.textView2.setTextColor(Color.RED); }
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
    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout layout;
        TextView textView1;
        TextView textView2;
        ImageView delete;

        public FileViewHolder(View itemView)
        {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.linearLayout_dialog);
            textView1 = (TextView) itemView.findViewById(R.id.textView1_dialog);
            textView2 = (TextView) itemView.findViewById(R.id.textView2_dialog);
            delete = (ImageView) itemView.findViewById(R.id.imageView_dialog);

            //Short and long click listeners for the expenses context menu
            layout.setOnClickListener(this);

            //Profile delete button
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parent.getActivity()).setTitle(R.string.confirm_areyousure_deletesingle)
                            .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (database_import_files != null) {
                                database_import_files.get(getAdapterPosition()).delete();
                                database_import_files = ProfileManager.GetImportDatabaseFiles();
                                notifyDataSetChanged();

                                ProfileManager.Print("File Deleted");
                            }
                        }})
                    .setNegativeButton(R.string.action_cancel, null)
                            .create().show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            final File file = database_import_files.get(getAdapterPosition());
            final int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
            final int currentVersion = ProfileManager.GetNewestDatabaseVersion();
            if (version <= currentVersion) { //Version check
                new AlertDialog.Builder(parent.getActivity()).setTitle(R.string.confirm_areyousure_deleteall)
                        .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                parent.ImportDatabase(file);
                                parent.dismiss();
                            }})
                        .setNegativeButton(R.string.confirm_no, null)
                        .create().show();
            }
            else {
                ProfileManager.Print("ERROR: Selected database is newer than this app version supports.");
            }


        }
    }
}
