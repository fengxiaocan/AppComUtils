package com.fxc.lib.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *
 *
 *     time  : 16/11/01
 *     desc  : Handler相关工具类
 * </pre>
 */
public final class HandlerUtils {

    private HandlerUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static class HandlerHolder extends Handler {
        WeakReference<Callback> mListenerWeakReference;

        /**
         * 使用必读：推荐在Activity或者Activity内部持有类中实现该接口，不要使用匿名类，可能会被GC
         *
         * @param listener 收到消息回调接口
         */
        public HandlerHolder(Callback listener) {
            mListenerWeakReference = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListenerWeakReference != null && mListenerWeakReference.get() != null) {
                mListenerWeakReference.get().handleMessage(msg);
            }
        }
    }
}
