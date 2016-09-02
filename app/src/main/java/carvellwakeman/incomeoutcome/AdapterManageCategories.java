package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AdapterManageCategories extends RecyclerView.Adapter<AdapterManageCategories.CategoryViewHolder>
{
    ActivityManageCategories parent;

    public AdapterManageCategories(ActivityManageCategories _parent)
    {
        parent = _parent;
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position)
    {
        //Category
        Category cr = ProfileManager.GetCategoryByIndex(position);
        if (cr != null) {
            //Textview
            holder.title.setText(cr.GetTitle());
            holder.icon.setImageDrawable(ProfileManager.getDrawable(R.drawable.ic_view_list_white_24dp));
            holder.icon.setColorFilter(cr.GetColor());
            holder.secondaryIcon.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount()
    {
        return ProfileManager.GetCategoriesCount();
    }


    public class CategoryViewHolder extends ViewHolderTextUnderline implements View.OnClickListener
    {
        public CategoryViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            Category cr = ProfileManager.GetCategoryByIndex(getAdapterPosition());
            if (cr != null) {

                ProfileManager.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, cr.GetTitle(), "", cr.GetTitle(),
                        new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditCategory(data, dialogFragment); } },
                        null,
                        new ProfileManager.ParentCallback() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DeleteCategory(data, dialogFragment); } }),
                        true); //TODO: Handle mIsLargeDisplay
            }
        }
    }
}
