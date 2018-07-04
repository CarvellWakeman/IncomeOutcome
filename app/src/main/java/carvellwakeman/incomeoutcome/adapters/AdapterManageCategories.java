package carvellwakeman.incomeoutcome.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.data.CategoryManager;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.models.Category;
import carvellwakeman.incomeoutcome.activities.ActivityManageCategories;
import carvellwakeman.incomeoutcome.viewholders.ViewHolderCategory;
import carvellwakeman.incomeoutcome.viewholders.ViewHolderEntity;


public class AdapterManageCategories extends AdapterManageEntity
{
    public AdapterManageCategories(ActivityManageCategories _parent) { super(_parent); }


    @Override
    public ViewHolderCategory onCreateViewHolder(ViewGroup vg, int viewType)
    {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.row_layout_text_underline, vg, false);
        return new ViewHolderCategory(parent, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderEntity holder, int position) {
        Category cat = CategoryManager.getInstance().GetCategories().get(position);
        if (cat != null) {
            holder.Entity = cat;

            // Fields
            holder.title.setText(cat.GetTitle());
            holder.icon.setImageDrawable(Helper.getDrawable(R.drawable.ic_view_list_white_24dp));
            holder.icon.setColorFilter(cat.GetColor());
        }
    }

    @Override public int getItemCount()
    {
        return CategoryManager.getInstance().GetCategoriesCount();
    }

}
