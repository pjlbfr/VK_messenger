package vkmsgr.com.vkmessenger;



import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zodiakaio on 21.02.2018.
 */

public class FriendsRecycleViewAdapter  extends RecyclerView.Adapter<FriendsRecycleViewAdapter.ViewHolder>{

    private List<Friend> friendsList = new ArrayList<>();
    private Listener listener;

    public static interface Listener{
        public void onClick(int id, String name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public FriendsRecycleViewAdapter(List<Friend> list){
        friendsList = list;
    }

    public void updateList(List<Friend> list){
        friendsList = list;
    }

    @Override
    public FriendsRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        ImageView friendPhoto = (ImageView) cardView.findViewById(R.id.image_friend);
        TextView friendName = (TextView) cardView.findViewById(R.id.name_friend);

        final Friend friend = friendsList.get(position);
        friendName.setText(friend.getFullName());
        Context context = friendPhoto.getContext();
        Glide.with(context).load(friend.getUrlPhoto()).into(friendPhoto);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onClick(friend.getId(), friend.getFullName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList != null ? friendsList.size() : 0;
    }
}

