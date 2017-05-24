package com.fxc.lib.impl;

import android.view.View;

/**
 * @项目名： MyComUtils
 * @包名： com.fxc.lib.impl
 * @创建者: Noah.冯
 * @时间: 12:01
 * @描述： TODO
 */
public abstract class TOnClickListener<T> implements View.OnClickListener {
    protected T t;

    public TOnClickListener(T t) {
        this.t = t;
    }
}
