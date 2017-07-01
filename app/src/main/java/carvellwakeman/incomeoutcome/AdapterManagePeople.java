package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdapterManagePeople extends AdapterManageEntity
{

    public AdapterManagePeople(ActivityManagePeople _parent) { super(_parent); }

    @Override
    public ViewHolderPerson onCreateViewHolder(ViewGroup vg, int viewType)
    {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.row_layout_text_underline, vg, false);
        return new ViewHolderPerson(parent, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderEntity holder, int position) {
        Person pr = PersonManager.getInstance().GetPeople().get(position);
        if (pr != null) {
            holder.mEntity = pr;

            // Fields
            holder.title.setText(pr.GetName());
            holder.icon.setImageDrawable(Helper.getDrawable(R.drawable.ic_face_white_24dp));
            holder.secondaryIcon.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount()
    {
        return PersonManager.getInstance().GetPeopleCount();
    }

}
