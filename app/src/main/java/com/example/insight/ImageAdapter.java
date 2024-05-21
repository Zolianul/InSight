package com.example.insight;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<UploadToFirebase> mUploadToFirebases;
    private OnItemClickListener mListner;

    public ImageAdapter(Context context, List<UploadToFirebase> uploadToFirebases) {
        mContext = context;
        mUploadToFirebases = uploadToFirebases;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        UploadToFirebase uploadToFirebaseCurrent = mUploadToFirebases.get(position);
        holder.textViewName.setText(uploadToFirebaseCurrent.getName());
        Picasso.with(mContext)
                .load(uploadToFirebaseCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUploadToFirebases.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener {
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListner!=null){
                int position =getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    mListner.onItemClick(position);

                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("This is a permanent deletion of the file");
            //MenuItem doWhatever= menu.add(Menu.NONE,1,1,"Do whatever");
            MenuItem delete=menu.add(Menu.NONE,2,2,"Delete");

            //doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {

            if(mListner!=null){
                int position =getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListner.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListner.onDeleteClick(position);
                            return true;

                    }

                }
            }

            return false;
        }
    }


    public interface OnItemClickListener{
        void onItemClick(int position);

        void onWhatEverClick(int position);
        void onDeleteClick(int position);

        void OnDestroy();
    }

    public void setOnItemClicListener(OnItemClickListener listner){
        mListner= listner;

    }
}

