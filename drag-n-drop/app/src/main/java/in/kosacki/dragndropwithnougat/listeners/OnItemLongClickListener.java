package in.kosacki.dragndropwithnougat.listeners;

import android.view.View;

/**
 * Created by hubert on 29/09/16.
 */
public interface OnItemLongClickListener<Type> {

    void onItemLongClick(View view, Type object);

}
