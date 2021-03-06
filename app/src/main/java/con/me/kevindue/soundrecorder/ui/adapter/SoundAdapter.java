package con.me.kevindue.soundrecorder.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

import con.me.kevindue.soundrecorder.R;
import con.me.kevindue.soundrecorder.entity.bean.SoundBean;
import con.me.kevindue.soundrecorder.presenter.impl.RecorderPresenter;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {
    private Context context;
    private List<SoundBean> list;
    private OnItemClickLister onItemClickLister;
    private OnLongClickLister onLongClickLister;

    public interface OnItemClickLister {
        void onItemClick(SoundBean sondBean, int position);
    }

    public interface OnLongClickLister {
        void onLongClick(String filePath);
    }

    public void setOnItemClickLister(OnItemClickLister onItemClickLister) {
        this.onItemClickLister = onItemClickLister;
    }

    public void setOnLongClickLister(OnLongClickLister onLongClickLister) {
        this.onLongClickLister = onLongClickLister;
    }

    public SoundAdapter(Context context){
        this.context = context;
    }

    public void updateItemsIcon(RecorderPresenter recorderControl, int prePosion, int position, SoundBean soundBean){
        if (list != null){
            for (int i=0;i<list.size();i++){
                if (i == position){
                    if (recorderControl.isPalying() && prePosion == position){
                        list.get(i).iconRes = R.drawable.ic_play_start;
                        recorderControl.pauseAudio();
                    } else {
                        list.get(i).iconRes = R.drawable.ic_play_pause;
                        int finalI = i;
                        recorderControl.playAudio(list.get(i).path, mp -> {
                            list.get(finalI).iconRes = R.drawable.ic_play_start;
                            notifyItemChanged(finalI);
                        });
                    }
                } else {
                    list.get(i).iconRes = R.drawable.ic_play_start;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void fillData(List<SoundBean> list){
        this.list = list;
        Collections.sort(this.list, (o1, o2) -> { //逆序排列
            if (o1.lastModified < o2.lastModified){
                return 1;
            } else {
                return -1;
            }
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(context, R.layout.item_sound_list, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = viewHolder.getLayoutPosition();
        viewHolder.name.setText(list.get(position).name);
        if (list.get(position).iconRes > 0){
            viewHolder.ivAction.setImageResource(list.get(position).iconRes);
        } else {
            viewHolder.ivAction.setImageResource(R.drawable.ic_play_start);
        }
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongClickLister != null){
                    onLongClickLister.onLongClick(list.get(position).path);
                }
                return false;
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickLister != null){
                    onItemClickLister.onItemClick(list.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAction;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAction = (ImageView) itemView.findViewById(R.id.iv_action);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

}
