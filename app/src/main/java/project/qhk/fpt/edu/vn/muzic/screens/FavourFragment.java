package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.adapters.PlaylistAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.SongAdapter;
import project.qhk.fpt.edu.vn.muzic.adapters.listeners.RecyclerViewListener;
import project.qhk.fpt.edu.vn.muzic.managers.RealmManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavourFragment extends Fragment {

    @BindView(R.id.recycler_view_playlist)
    RecyclerView recyclerViewPlaylist;

    public FavourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favour, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        goContent();
    }

    private void goContent() {
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPlaylist.setAdapter(new PlaylistAdapter());
        recyclerViewPlaylist.getAdapter().notifyDataSetChanged();

        recyclerViewPlaylist.addOnItemTouchListener(new RecyclerViewListener(
                getActivity(), recyclerViewPlaylist, new RecyclerViewListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}
