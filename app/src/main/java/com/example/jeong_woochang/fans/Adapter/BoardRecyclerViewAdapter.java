package com.example.jeong_woochang.fans.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeong_woochang.fans.POJO.RecyclerViewItem;
import com.example.jeong_woochang.fans.R;

import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 12. 15..
 */

public class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecyclerViewItem> mList;
    private SparseIntArray listPosition = new SparseIntArray();
    private BoardRecyclerViewAdapter.OnItemClickListener mItemClickListener;
    private Context mContext;

    public BoardRecyclerViewAdapter(ArrayList<RecyclerViewItem> list) {
        this.mList = list;
    }

    private class CellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private RecyclerView thumbnail;

        private ImageRecyclerViewAdapter adapter;

        private LinearLayoutManager layoutManager;
        private TextView title, num, name, date, view;
        public CellViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            num = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            view = itemView.findViewById(R.id.view);


            thumbnail.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            thumbnail.setLayoutManager(layoutManager);


            adapter = new ImageRecyclerViewAdapter(mContext);
            thumbnail.setAdapter(adapter);


            // this is needed if you are working with CollapsingToolbarLayout, I am adding this here just in case I forget.
            thumbnail.setNestedScrollingEnabled(false);

            //optional
            StartSnapHelper snapHelper = new StartSnapHelper();
            snapHelper.attachToRecyclerView(thumbnail);
        }
        public void setData(ArrayList<String> list) {
            adapter.updateList(list);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition());
                return true;
            }
            return false;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        switch (viewType) {
            default: {
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
                return new CellViewHolder(v1);
            }
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        switch (viewHolder.getItemViewType()) {
            default: {
                CellViewHolder cellViewHolder = (CellViewHolder) viewHolder;

                cellViewHolder.setData(mList.get(position).getThumbnail());

                int lastSeenFirstPosition = listPosition.get(position, 0);
                if (lastSeenFirstPosition >= 0) {
                    cellViewHolder.layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, 0);
                }
                ((CellViewHolder) viewHolder).title.setText(mList.get(position).getTitle());
                ((CellViewHolder) viewHolder).num.setText(mList.get(position).getNum());
                ((CellViewHolder) viewHolder).name.setText(mList.get(position).getName());
                ((CellViewHolder) viewHolder).date.setText(mList.get(position).getDate());
                ((CellViewHolder) viewHolder).view.setText(mList.get(position).getView());
                break;
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        CellViewHolder cellViewHolder = (CellViewHolder) viewHolder;
        int firstVisiblePosition = cellViewHolder.layoutManager.findFirstVisibleItemPosition();
        listPosition.put(position, firstVisiblePosition);

        super.onViewRecycled(viewHolder);
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }


    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }


    public void addData(ArrayList<RecyclerViewItem> item) {
        mList.addAll(item);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    // for both short and long click
    public void SetOnItemClickListener(final BoardRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public String getHref(int pos){
        return mList.get(pos).getHref();
    }
}
