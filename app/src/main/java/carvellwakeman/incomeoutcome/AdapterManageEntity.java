package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class AdapterManageEntity extends RecyclerView.Adapter<ViewHolderEntity> {

    ActivityManageEntity parent;

    public AdapterManageEntity(ActivityManageEntity _parent)
    {
        parent = _parent;
    }


    @Override
    public abstract ViewHolderEntity onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(final ViewHolderEntity holder, int position);

    @Override public abstract int getItemCount();

}
