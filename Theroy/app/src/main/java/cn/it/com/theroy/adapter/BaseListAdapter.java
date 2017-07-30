package cn.it.com.theroy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_BOOK_LABLE = 0;
    public static final int TYPE_BOOK_CHAPTER = 1;
    public static final int TYPE_COUNT = 2;
    protected List<T> data;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_BOOK_LABLE;
        return super.getItemViewType(position);
    }

    public static int getTypeCount() {
        return TYPE_COUNT;
    }
}
