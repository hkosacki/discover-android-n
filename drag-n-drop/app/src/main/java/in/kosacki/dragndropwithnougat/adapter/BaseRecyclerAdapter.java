package in.kosacki.dragndropwithnougat.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by kryst on 06.10.2016.
 */

public abstract class BaseRecyclerAdapter<Type> extends RecyclerView.Adapter<BaseViewHolder<Type>> {

    protected List<Type> data;

    public BaseRecyclerAdapter(List<Type> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<Type> getData() {
        return data;
    }

    public void setData(List<Type> data) {
        this.data = data;
    }

}
