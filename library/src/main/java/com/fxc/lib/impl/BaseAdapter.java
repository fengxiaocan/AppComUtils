package com.fxc.lib.impl;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @项目名： MyComUtils
 * @包名： com.fxc.lib.impl
 * @创建者: Noah.冯
 * @时间: 12:05
 * @描述： TODO
 */
public abstract class BaseAdapter<T, V extends ListViewHolder>
        extends android.widget.BaseAdapter
{
    protected List<T> mData;

    public BaseAdapter(List<T> data) {
        mData = data;
    }

    public BaseAdapter() {
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public void removeT(int index) {
        if (mData != null) {
            mData.remove(index);
            notifyDataSetChanged();
        }
    }

    public void removeT(T t) {
        if (mData != null) {
            mData.remove(t);
            notifyDataSetChanged();
        }
    }

    public void update(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void updateT(T t, int position) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.set(position, t);
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addT(T t) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(t);
        notifyDataSetChanged();
    }

    public T getT(int position) {
        if (mData != null && mData.size() > position) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        V holder ;
        if (convertView == null) {
            convertView = View.inflate(onBindContext(), onBindLayoutRes(), null);
            holder = onCreateViewHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            holder = (V) convertView.getTag();
        }
        onBindData(holder, position);
        return convertView;
    }
    /**
     * 绑定布局文件
     */
    public abstract @LayoutRes int onBindLayoutRes();

    /**
     * 绑定上下文
     */
    public abstract Context onBindContext();

    /**
     * 创建ViewHolder
     */
    public abstract V onCreateViewHolder(View rootView, int position);

    /**
     * 绑定数据
     */
    public abstract void onBindData(V holder, int position);
}
