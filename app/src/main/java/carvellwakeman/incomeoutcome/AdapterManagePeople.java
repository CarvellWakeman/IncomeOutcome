package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        Person pr = PersonManager.getInstance().GetPeople().get(position);
        if (pr != null) {
            //Textview
            holder.title.setText(pr.GetName());
            holder.icon.setImageDrawable(Helper.getDrawable(R.drawable.ic_face_white_24dp));
            holder.secondaryIcon.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount()
    {
        return PersonManager.getInstance().GetPeopleCount();
    }


    public class PersonViewHolder extends ViewHolderTextUnderline implements View.OnClickListener
    {
        public PersonViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            Person person = PersonManager.getInstance().GetPeople().get(getAdapterPosition());
            Helper.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, person.GetName(), "", String.valueOf(person.GetID()),
                    new ParentCallBack() {
                        @Override public void call(String data, DialogFragmentManagePPC dialogFragment) {
                            parent.EditPerson(Integer.valueOf(data), dialogFragment);
                        }
                    },
                    null,
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DeletePerson(Integer.valueOf(data), dialogFragment); } }
            ), true); //TODO: Handle mIsLargeDisplay
            //ProfileManager.OpenDialogFragment(parent, DialogFragmentManageProfile.newInstance(parent, ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition())), true); //TODO: Handle mIsLargeDisplay
            //parent.EditPerson(ProfileManager.GetOtherPersonByIndex(getAdapterPosition()));
        }
    }
}
