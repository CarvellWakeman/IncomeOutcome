package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterManagePeople extends RecyclerView.Adapter<AdapterManagePeople.PersonViewHolder>
{
    ActivityManagePeople parent;


    //Constructor
    public AdapterManagePeople(ActivityManagePeople _parent)
    {
        parent = _parent;
    }


    //When creating a view holder
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_wdelete, parent, false);

        return new PersonViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position)
    {
        String person = ProfileManager.GetOtherPersonByIndex(position);
        if (person != null) {
            //Textview
            holder.textView.setText(person);
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        return ProfileManager.GetOtherPeopleCount();
    }


    //View Holder class
    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout layout;
        TextView textView;
        ImageView delete;

        public PersonViewHolder(View itemView)
        {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.linearLayout_dialog);
            textView = (TextView) itemView.findViewById(R.id.textView_dialog);
            delete = (ImageView) itemView.findViewById(R.id.imageView_dialog);

            //Short and long click listeners for the expenses context menu
            layout.setOnClickListener(this);

            //Profile delete button
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parent).setTitle(R.string.confirm_areyousure_deletesingle)
                            .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProfileManager.RemoveOtherPerson(ProfileManager.GetOtherPersonByIndex(getAdapterPosition()));
                            notifyDataSetChanged();
                        }})
                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    }).create().show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            parent.EditPerson(ProfileManager.GetOtherPersonByIndex(getAdapterPosition()));

            /*
            //De-select last view if it's not v
            if (selectedView != null && selectedView != v){
                selectedView.setSelected(false);
                selectedViewPosition = -1;
            }

            //Select clicked view
            if (v.isSelected()){
                v.setSelected(false);
                selectedView = null;
                selectedViewPosition = -1;
            }
            else{
                v.setSelected(true);
                selectedView = v;
                selectedViewPosition = getAdapterPosition();
            }

            //Set dialogfragment positive button text
            parent.SetPositiveButtonEnabled(selectedView != null);
            */
        }
    }
}
