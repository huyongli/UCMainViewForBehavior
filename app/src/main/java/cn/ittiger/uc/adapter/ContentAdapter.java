package cn.ittiger.uc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ylhu on 17-2-23.
 */
public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mList;
    private Context mContext;

    public ContentAdapter(Context context, List<String> list) {

        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ContentViewHolder viewHolder = (ContentViewHolder) holder;
        viewHolder.mTextView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

    public String getItem(int position) {

        return mList.get(position);
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        public ContentViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
