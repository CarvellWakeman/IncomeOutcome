package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterManageCategories extends RecyclerView.Adapter<AdapterManageCategories.PersonViewHolder>
{
    DialogFragmentManageCategories parent;


    //Constructor
    public AdapterManageCategories(DialogFragmentManageCategories _parent)
    {
        parent = _parent;
    }


    //When creating a view holder
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_category, parent, false);

        return new PersonViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position)
    {
        Category category = ProfileManager.GetCategoryByIndex(position);
        if (category != null) {
            //Textview
            holder.textView.setText(category.GetTitle());
            //Colorbar
            if (category.GetColor() != 0) { holder.colorBar.setBackgroundColor(category.GetColor()); }
            else { holder.colorBar.setBackgroundColor(Color.TRANSPARENT); }
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        return ProfileManager.GetCategoriesCount();
    }


    //View Holder class
    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout colorBar;
        LinearLayout layout;
        TextView textView;
        ImageView delete;

        public PersonViewHolder(View itemView)
        {
            super(itemView);

            colorBar = (LinearLayout) itemView.findViewById(R.id.linearLayout_dialogcat_colorbar);
            layout = (LinearLayout) itemView.findViewById(R.id.linearLayout_dialog);
            textView = (TextView) itemView.findViewById(R.id.textView_dialog);
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
                            ProfileManager.RemoveCategory(ProfileManager.GetCategoryByIndex(getAdapterPosition()));
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
            parent.EditCategory(ProfileManager.GetCategoryByIndex(getAdapterPosition()));
        }
    }
}
