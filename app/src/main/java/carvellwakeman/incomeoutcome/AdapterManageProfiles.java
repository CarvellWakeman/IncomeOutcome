package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterManageProfiles extends RecyclerView.Adapter<AdapterManageProfiles.ProfileViewHolder>
{
    ActivityManageProfiles parent;

    //Constructor
    public AdapterManageProfiles(ActivityManageProfiles _parent)
    {
        parent = _parent;
    }


    //When creating a view holder
    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_wdelete, parent, false);

        return new ProfileViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, int position)
    {
        //Profile
        Profile pr = ProfileManager.GetProfileByIndex(position);
        if (pr != null) {
            //Textview
            holder.textView.setText(pr.GetName());
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        return ProfileManager.GetProfileCount();
    }

    //View Holder class
    public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout layout;
        TextView textView;
        ImageView delete;

        public ProfileViewHolder(View itemView)
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
                    final Profile pr = ProfileManager.GetProfileByIndex(getAdapterPosition());
                    if (pr != null) {
                        if (pr.GetIncomeSourcesSize() + pr.GetExpenseSourcesSize() > 0) {
                            ProfileManager.OpenDialogFragment(parent, DialogFragmentTransferTransaction.newInstance(parent, pr), true); //TODO: Handle mIsLargeDisplay
                            //parent.dismiss();
                        }
                        else {
                            new AlertDialog.Builder(parent).setTitle(R.string.confirm_areyousure_deletesingle)
                                    .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ProfileManager.DeleteProfile(pr);
                                            notifyDataSetChanged();
                                        }})
                                    .setNegativeButton(R.string.action_cancel, null).create().show();

                        }
                    }

                }
            });
        }

        @Override
        public void onClick(View v) {
            parent.EditProfile(ProfileManager.GetProfileByIndex(getAdapterPosition()));

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
