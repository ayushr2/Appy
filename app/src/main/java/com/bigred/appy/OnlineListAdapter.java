package main.java.com.bigred.appy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigred.appy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayushranjan on 16/09/17.
 */

public class OnlineListAdapter extends RecyclerView.Adapter<OnlineListAdapter.ViewHolder>  {

    private ArrayList<User> onlineConsultants;

    OnlineListAdapter(ArrayList<User> onlineConsultants) {
        this.onlineConsultants = onlineConsultants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View onlineListItem = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.online_list_item, parent, false);
        return new ViewHolder(onlineListItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = onlineConsultants.get(position);
        holder.consultantName.setText(user.name);
        Picasso.with(holder.consultantProfilePic.getContext()).load(Uri.parse(user.photoUriString))
                .transform(new CircleTransformation()).into(holder.consultantProfilePic);
        holder.consultantScore.setText(String.valueOf(user.score));

        switch (position%5) {
            case 0:
                holder.wholeView.setBackground(holder.wholeView.getContext().getDrawable(R.drawable._5));
                break;
            case 1:
                holder.wholeView.setBackground(holder.wholeView.getContext().getDrawable(R.drawable._1));
                break;
            case 2:
                holder.wholeView.setBackground(holder.wholeView.getContext().getDrawable(R.drawable._2));
                break;
            case 3:
                holder.wholeView.setBackground(holder.wholeView.getContext().getDrawable(R.drawable._3));
                break;
            case 4:
                holder.wholeView.setBackground(holder.wholeView.getContext().getDrawable(R.drawable._4));
                break;
        }

        holder.wholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.wholeView.getContext(), ConsultantInfoActivity.class);
                intent.putExtra(Constants.CONSULTANT_CLEAN_ID, user.emailClean);
                holder.wholeView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return onlineConsultants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView consultantProfilePic;
        TextView consultantName;
        TextView consultantScore;
        View wholeView;

        public ViewHolder(View itemView) {
            super(itemView);
            wholeView = itemView;
            consultantProfilePic = (ImageView) itemView.findViewById(R.id.consultant_profile_pic);
            consultantName = (TextView) itemView.findViewById(R.id.consultant_name_view);
            consultantScore = (TextView) itemView.findViewById(R.id.consultant_score);
        }
    }
}
