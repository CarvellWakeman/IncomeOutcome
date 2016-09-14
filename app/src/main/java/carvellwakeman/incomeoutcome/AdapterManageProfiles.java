package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


public class AdapterManageProfiles extends RecyclerView.Adapter<AdapterManageProfiles.ProfileViewHolder>
{
    ActivityManageProfiles parent;

    public AdapterManageProfiles(ActivityManageProfiles _parent)
    {
        parent = _parent;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, int position)
    {
        //Profile
        Profile pr = ProfileManager.getInstance().GetProfileByIndex(position);
        if (pr != null) {
            //Textview
            holder.title.setText(pr.GetName());
            holder.subTitle.setText(pr.GetDateFormatted());

            if (holder.subTitle.getText() != null) { holder.subTitle.setVisibility(View.VISIBLE); }

            if (ProfileManager.getInstance().GetCurrentProfile() == pr){
                //holder.secondaryIcon.setImageDrawable(ProfileManager.getDrawable(R.drawable.ic_check_white_24dp));
                holder.secondaryIcon.setVisibility(View.VISIBLE);
                //holder.base.setBackgroundColor(ProfileManager.getColor(R.color.blue));
            }
            else {
                holder.secondaryIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override public int getItemCount()
    {
        return ProfileManager.getInstance().GetProfileCount();
    }


    public class ProfileViewHolder extends ViewHolderTextUnderline implements View.OnClickListener, View.OnLongClickListener
    {
        public ProfileViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            Profile pr = ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition());
            if (pr != null) {
                String descriptionString =
                        pr.GetPeriodFormatted() + "\n" +
                        pr.GetDateFormatted() + "\n" +
                                pr.GetTransactionsSize() + " " + ProfileManager.getString(R.string.misc_transactoins);

                ProfileManager.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, pr.GetName(), descriptionString, String.valueOf(pr.GetID()),
                        new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditProfile(data, dialogFragment); } },
                        new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.SelectProfile(data, dialogFragment); } },
                        new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.RemoveProfile(data, dialogFragment); } }),
                        true); //TODO: Handle mIsLargeDisplay
                //parent.EditProfile(ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v){
            ProfileManager.getInstance().SelectProfile(ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition()));
            notifyDataSetChanged();
            return true;
        }
    }
}
