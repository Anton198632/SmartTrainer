package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.MovieActivity;
import com.builderlinebr.smarttrainer.R;

import java.io.File;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolded> {

    List<String> videosItems;
    Context context;

    public MovieAdapter(List<String> videosItems, Context context) {
        this.videosItems = videosItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolded onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieAdapter.ViewHolded(LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolded holder, final int position) {


        final File imgFile = new File(videosItems.get(position));
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageVideoPreView.setImageBitmap(bitmap);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MovieActivity) context).btnStart.setVisibility(View.GONE);

                String fileName = imgFile.getName();
                String videoPath = context.getExternalFilesDir("").getPath() + "/videos/" + fileName.substring(1, fileName.lastIndexOf(".")+1) + "mp4";

                Uri video = Uri.parse(videoPath);
                ((MovieActivity) context).videoView.setVisibility(View.VISIBLE);
                ((MovieActivity) context).videoView.setVideoURI(video);
                ((MovieActivity) context).videoView.start();
            }
        });



//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videosItems.get(position),
//                MediaStore.Images.Thumbnails.MINI_KIND);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
//        holder.videoView.setBackground(bitmapDrawable);


//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//                videoView.start();
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return videosItems.size();
    }

    public class ViewHolded extends RecyclerView.ViewHolder {

        public ImageView imageVideoPreView;

        public ViewHolded(@NonNull View itemView) {
            super(itemView);

            imageVideoPreView = itemView.findViewById(R.id.imageVideoPreView);

        }
    }


}
