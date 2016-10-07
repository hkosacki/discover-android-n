package in.kosacki.dragndropwithnougat.listeners;

import android.view.View;

/**
 * Created by hubert on 29/09/16.
 */
public interface OnItemClickListener<T> {
    void onItemClick(View v, T object);
}
