package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.joda.time.LocalDate;

import java.util.List;

public class AdapterChangelog extends RecyclerView.Adapter<AdapterChangelog.ChangeViewHolder>
{
    Activity _parent;
    List<ChangelogChange> changeList;

    public AdapterChangelog(Activity parent, List<ChangelogChange> changes) {
        _parent = parent;
        changeList = changes;
    }

    @Override
    public ChangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_change, parent, false);
        return new ChangeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChangeViewHolder holder, int position)
    {
        ChangelogChange change = changeList.get(position);

        if (change != null){
            LayoutInflater inflater = _parent.getLayoutInflater();

            //Title info
            holder.version.setText(change.version + " " + change.versionChannel);
            holder.date.setText(LocalDate.parse(change.date).toString(ProfileManager.simpleDateFormat));


            //Notes
            holder.fl_note.removeViews(1,holder.fl_note.getChildCount()-1);
            for (int i = 0; i < change.notes.size(); i++){
                Setting setting = new Setting(inflater, R.drawable.ic_exclamation_white_18dp, change.notes.get(i), null, null);
                setting.getView().setClickable(false);
                setting.SetPadding(0,0,0,0);
                holder.fl_note.addView( setting.getView(), i+1);
            }
            //Additions
            holder.fl_add.removeViews(1,holder.fl_add.getChildCount()-1);
            for (int i = 0; i < change.additions.size(); i++){
                Setting setting = new Setting(inflater, R.drawable.ic_plus_white_18dp, change.additions.get(i), null, null);
                setting.SetPadding(0,0,0,0);
                holder.fl_add.addView(setting.getView(), i+1);
            }
            //Changes
            holder.fl_change.removeViews(1,holder.fl_change.getChildCount()-1);
            for (int i = 0; i < change.changes.size(); i++){
                Setting setting = new Setting(inflater, R.drawable.ic_repeat_white_18dp, change.changes.get(i), null, null);
                setting.SetPadding(0,0,0,0);
                holder.fl_change.addView( setting.getView(), i+1);
            }
            //Subtractions
            holder.fl_remove.removeViews(1,holder.fl_remove.getChildCount()-1);
            for (int i = 0; i < change.subtractions.size(); i++){
                Setting setting = new Setting(inflater, R.drawable.ic_minus_white_18dp, change.subtractions.get(i), null, null);
                setting.SetPadding(0,0,0,0);
                holder.fl_remove.addView( setting.getView(), i+1);
            }
            //Fixes
            holder.fl_fix.removeViews(1,holder.fl_fix.getChildCount()-1);
            for (int i = 0; i < change.fixes.size(); i++){
                Setting setting = new Setting(inflater, R.drawable.ic_bug_white_18dp, change.fixes.get(i), null, null);
                setting.SetPadding(0,0,0,0);
                holder.fl_fix.addView( setting.getView(), i+1);
            }


            //Title coloring
            if (change.version.equals(App.GetVersion(_parent))){
                holder.version.setTextColor(ProfileManager.getColor(R.color.black));
                holder.setVisibility(true);
            }
        }
    }

    @Override public int getItemCount() { return changeList.size(); }


    public class ChangeViewHolder extends ViewHolderTextUnderline implements View.OnClickListener
    {
        TextView version;
        TextView date;

        LinearLayout fl_note;
        LinearLayout fl_add;
        LinearLayout fl_change;
        LinearLayout fl_remove;
        LinearLayout fl_fix;

        boolean moreInfo;

        public ChangeViewHolder(View itemView) {
            super(itemView);

            moreInfo = false;

            version = (TextView) itemView.findViewById(R.id.row_layout_text_title);
            date = (TextView) itemView.findViewById(R.id.row_layout_date);

            fl_note = (LinearLayout) itemView.findViewById(R.id.frameLayout_note);
            fl_add = (LinearLayout) itemView.findViewById(R.id.frameLayout_add);
            fl_change = (LinearLayout) itemView.findViewById(R.id.frameLayout_change);
            fl_remove = (LinearLayout) itemView.findViewById(R.id.frameLayout_remove);
            fl_fix = (LinearLayout) itemView.findViewById(R.id.frameLayout_fix);

            /*
            fl_add.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (fl_add.getChildCount() > 1){
                        if (fl_add.getChildAt(1).getVisibility()==View.VISIBLE){
                            for(int i = 1; i < fl_add.getChildCount(); i++){ fl_add.getChildAt(i).setVisibility(View.GONE); }
                        }
                        else {
                            for(int i = 1; i < fl_add.getChildCount(); i++){ fl_add.getChildAt(i).setVisibility(View.VISIBLE); }
                        }
                    }
                }
            });
            */
        }

        @Override
        public void onClick(View v) {
            toggleMoreInfo();
        }

        //More Info (expand card)
        public void toggleMoreInfo(){
            setVisibility(!moreInfo);
            moreInfo = !moreInfo;
        }

        public void setVisibility(boolean visible){
            if (visible){
                if (fl_note.getChildCount() > 1){ fl_note.setVisibility(View.VISIBLE); }
                if (fl_add.getChildCount() > 1){ fl_add.setVisibility(View.VISIBLE); }
                if (fl_change.getChildCount() > 1){ fl_change.setVisibility(View.VISIBLE); }
                if (fl_remove.getChildCount() > 1){ fl_remove.setVisibility(View.VISIBLE); }
                if (fl_fix.getChildCount() > 1){ fl_fix.setVisibility(View.VISIBLE); }
            }
            else {
                fl_note.setVisibility(View.GONE);
                fl_add.setVisibility(View.GONE);
                fl_change.setVisibility(View.GONE);
                fl_remove.setVisibility(View.GONE);
                fl_fix.setVisibility(View.GONE);
            }
        }
    }
}
