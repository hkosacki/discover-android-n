package in.kosacki.dragndropwithnougat.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import in.kosacki.dragndropwithnougat.listeners.OnItemClickListener;
import in.kosacki.dragndropwithnougat.listeners.OnItemLongClickListener;


/**
 * Created by hubert on 07/10/16.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(ViewGroup viewGroup, int layoutId){
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(T type);

    protected void setOnItemClick(final T object, final OnItemClickListener listener){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, object);
            }
        });
    }

    protected void setOnItemLongClick(final T object, final OnItemLongClickListener longClickListener){
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickListener.onItemLongClick(itemView, object);
                return true;
            }
        });
    }

}
