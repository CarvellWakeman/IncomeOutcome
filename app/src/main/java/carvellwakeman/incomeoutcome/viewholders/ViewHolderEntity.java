package carvellwakeman.incomeoutcome.viewholders;

import android.view.View;
import carvellwakeman.incomeoutcome.activities.ActivityManageEntity;
import carvellwakeman.incomeoutcome.interfaces.BaseEntity;


// Abstract entity view holder
public abstract class ViewHolderEntity<T extends BaseEntity> extends ViewHolderTextUnderline implements View.OnClickListener, View.OnLongClickListener {
    ActivityManageEntity mParent;
    public T Entity;

    public ViewHolderEntity(ActivityManageEntity parent, View itemView) {
        super(itemView);
        mParent = parent;
    }

    @Override
    public void onClick(View v) {
        if (Entity != null) {
            // Select
            if (mParent.menuState == ActivityManageEntity.MENU_STATE.SELECT) {
                mParent.SelectEntity(Entity);
            }
            else { // Edit or delete
                itemClick(mParent, Entity);
            }
        }
    }

    @Override
    public boolean onLongClick(View v){
        itemLongClick(mParent, Entity);
        return true;
    }

    public abstract void itemClick(ActivityManageEntity parent, T entity);
    public void itemLongClick(ActivityManageEntity parent, T entity){}
}
