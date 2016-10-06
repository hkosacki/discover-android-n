package in.kosacki.dragndropwithnougat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<Type> extends RecyclerView.ViewHolder {

    public BaseViewHolder(ViewGroup viewGroup, int layoutId) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(Type type);

}
