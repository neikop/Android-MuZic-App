package project.qhk.fpt.edu.vn.muzic.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.view_holders.PlaylistViewHolder;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;

/**
 * Created by WindzLord on 11/2/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.view_holder_playlist, parent, false);
        return new PlaylistViewHolder(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.bind(RealmManager.getInstance().getAllPlaylist().get(position));
    }

    @Override
    public int getItemCount() {
        return RealmManager.getInstance().getAllPlaylist().size();
    }
}
