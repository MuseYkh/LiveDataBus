package cn.muse.livedatademo;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * author: wanshi
 * created on: 2019-05-09 14:08
 * description:
 */
public class LiveDataBus {

    private static LiveDataBus mInstance;
    private static Map<String, MyMutableLiveData> mLiveDataMap = new HashMap<>();

    private LiveDataBus() {

    }

    public static LiveDataBus get() {
        if (mInstance == null) {
            synchronized (LiveDataBus.class) {
                if (mInstance == null) {
                    mInstance = new LiveDataBus();
                }
            }
        }
        return mInstance;
    }

    public <T> MyMutableLiveData<T> with(String key, Class<T> type) {
        if (!mLiveDataMap.containsKey(key)) {
            mLiveDataMap.put(key, new MyMutableLiveData());
        }
        return mLiveDataMap.get(key);
    }

    private MyMutableLiveData<Object> with(String key) {
        return with(key, Object.class);
    }

    public <T> void post(String key, T t) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            with(key).setValue(t);
        } else {
            with(key).postValue(t);
        }
    }

    public static class MyMutableLiveData<T> extends MutableLiveData<T> {
        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
            try {
                hook(observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 处理粘性事件
         * @param owner
         * @param observer
         */
        public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
        }

        private void hook(@NonNull Observer<T> observer) throws Exception {
            Class<LiveData> liveDataClass = LiveData.class;
            Field fieldObservers = liveDataClass.getDeclaredField("mObservers");
            fieldObservers.setAccessible(true);
            Object mObservers = fieldObservers.get(this);
            Class<?> classObservers = mObservers.getClass();

            Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
            methodGet.setAccessible(true);
            Object objectWrapperEntry = methodGet.invoke(mObservers, observer);
            Object objectWrapper = ((Map.Entry)objectWrapperEntry).getValue();
            Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();

            Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
            fieldLastVersion.setAccessible(true);
            Field fieldVersion = liveDataClass.getDeclaredField("mVersion");
            fieldVersion.setAccessible(true);
            Object objectVersion = fieldVersion.get(this);
            fieldLastVersion.set(objectWrapper, objectVersion);
        }
    }
}
