package in.kosacki.dragndropwithnougat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import in.kosacki.dragndropwithnougat.listeners.OnItemClickListener;
import in.kosacki.dragndropwithnougat.listeners.OnItemLongClickListener;

public abstract class BaseViewHolder<Type> extends RecyclerView.ViewHolder {

    public BaseViewHolder(ViewGroup viewGroup, int layoutId) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(Type type);

    public void setOnItemClick(final Type type, final OnItemClickListener<Type> listener) {
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(itemView, type);
            }
        });
    }

    public void setOnItemLongClick(final Type type, final OnItemLongClickListener<Type> listener) {
        itemView.setClickable(true);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(itemView, type);
                return false;
            }
        });
    }

}
