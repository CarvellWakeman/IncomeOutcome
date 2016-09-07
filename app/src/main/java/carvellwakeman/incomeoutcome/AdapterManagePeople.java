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

    public AdapterManagePeople(ActivityManagePeople _parent)
    {
        parent = _parent;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);
        return new PersonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position)
    {
        //Person
        String pr = ProfileManager.getInstance().GetOtherPersonByIndex(position);
        if (pr != null) {
            //Textview
            holder.title.setText(pr);
            holder.icon.setImageDrawable(ProfileManager.getDrawable(R.drawable.ic_face_white_24dp));
            holder.secondaryIcon.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount()
    {
        return ProfileManager.getInstance().GetOtherPeopleCount();
    }


    public class PersonViewHolder extends ViewHolderTextUnderline implements View.OnClickListener
    {
        public PersonViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            ProfileManager.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, ProfileManager.getInstance().GetOtherPersonByIndex(getAdapterPosition()), "", ProfileManager.getInstance().GetOtherPersonByIndex(getAdapterPosition()),
                    new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditPerson(data, dialogFragment); } },
                    null,
                    new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DeletePerson(data, dialogFragment); } }
            ), true); //TODO: Handle mIsLargeDisplay
            //ProfileManager.OpenDialogFragment(parent, DialogFragmentManageProfile.newInstance(parent, ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition())), true); //TODO: Handle mIsLargeDisplay
            //parent.EditPerson(ProfileManager.GetOtherPersonByIndex(getAdapterPosition()));
        }
    }
}
