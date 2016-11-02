package in.kosacki.dragndropwithnougat.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import in.kosacki.dragndropwithnougat.viewholders.BaseViewHolder;

/**
 * Created by hubert on 07/10/16.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    protected List<T> dataList;

    public BaseRecyclerAdapter(List<T> data) {
        dataList = data;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void setData(List<T> data) {
        dataList = data;
    }

    public List<T> getData() {
        return dataList;
    }

}
