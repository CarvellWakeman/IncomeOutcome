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

import java.util.ArrayList;

public class AdapterTransferTransaction extends RecyclerView.Adapter<AdapterTransferTransaction.TransferViewHolder>
{
    DialogFragmentTransferTransaction parent;
    ArrayList<Profile> _profiles;

    //Constructor
    public AdapterTransferTransaction(DialogFragmentTransferTransaction _parent)
    {
        parent = _parent;

        _profiles = new ArrayList<>(ProfileManager.GetProfiles());
        _profiles.remove(parent.current);
    }


    //When creating a view holder
    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text, parent, false);

        return new TransferViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final TransferViewHolder holder, int position)
    {
        //Profile
        Profile pr = _profiles.get(position);
        if (pr != null) {
            //Textview
            holder.textView.setText(pr.GetName());
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        return _profiles.size();
    }

    //View Holder class
    public class TransferViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout layout;
        TextView textView;

        public TransferViewHolder(View itemView)
        {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.linearLayout_dialog);
            textView = (TextView) itemView.findViewById(R.id.textView_dialog);

            //Short and long click listeners for the expenses context menu
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            parent.TransferTransaction(_profiles.get(getAdapterPosition()));
        }
    }
}
