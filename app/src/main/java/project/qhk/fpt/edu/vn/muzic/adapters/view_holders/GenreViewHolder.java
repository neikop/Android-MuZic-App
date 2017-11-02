package project.qhk.fpt.edu.vn.muzic.adapters.view_holders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.models.Genre;

/**
 * Created by WindzLord on 11/29/2016.
 */

public class GenreViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_genre)
    ImageView imageView;

    @BindView(R.id.text_genre)
    TextView textView;

    private Context context;

    public GenreViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Genre genre) {
        try {
            InputStream stream = context.getAssets().open("images/genre_" + genre.getGenreID() + ".png");
            Drawable drawable = Drawable.createFromStream(stream, null);
            imageView.setImageDrawable(drawable);
            stream.close();
        } catch (IOException ex) {

        }
        textView.setText(genre.getName().toUpperCase());
    }
}
