package carvellwakeman.incomeoutcome;

import android.view.View;


// Abstract entity view holder
public abstract class ViewHolderEntity<T extends BaseEntity> extends ViewHolderTextUnderline implements View.OnClickListener, View.OnLongClickListener {
    ActivityManageEntity mParent;
    T mEntity;

    public ViewHolderEntity(ActivityManageEntity parent, View itemView) {
        super(itemView);
        mParent = parent;
    }

    @Override
    public void onClick(View v) {
        if (mEntity != null) {
            // Select
            if (mParent.menuState == ActivityManageEntity.MENU_STATE.SELECT) {
                mParent.SelectEntity(mEntity);
            }
            else { // Edit or delete
                itemClick(mParent, mEntity);
            }
        }
    }

    @Override
    public boolean onLongClick(View v){
        itemLongClick(mParent, mEntity);
        return true;
    }

    public abstract void itemClick(ActivityManageEntity parent, T entity);
    public void itemLongClick(ActivityManageEntity parent, T entity){}
}
