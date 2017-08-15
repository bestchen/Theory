package cn.it.com.theroy.mamager;

import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainThreadExecutor {

    private static Handler handler;

    private static final Map<Object, List<Runnable>> runnablesMap = Collections.synchronizedMap(new WeakHashMap<Object, List<Runnable>>());

    public static void post(Object tag, Runnable runnable) {
        if (tag == null) {
            throw new IllegalArgumentException("tag is null");
        }
        if (runnable == null) {
            throw new IllegalArgumentException("runnable is null");
        }
        List<Runnable> runnables = runnablesMap.get(tag);
        if (runnables == null) {
            runnables = new CopyOnWriteArrayList<>();
            runnablesMap.put(tag, runnables);
        }
        runnables.add(runnable);
        getHandler().post(runnable);
    }

    public static void postDelayed(Object tag, Runnable runnable, long delayMill) {
        if (tag == null) {
            throw new IllegalArgumentException("tag is null");
        }
        if (runnable == null) {
            throw new IllegalArgumentException("runnable is null");
        }
        if (delayMill <= 0) {
            throw new IllegalArgumentException("delayMill <= 0");
        }

        List<Runnable> runnables = runnablesMap.get(tag);
        if (runnables == null) {
            runnables = new CopyOnWriteArrayList<>();
            runnablesMap.put(tag, runnables);
        }
        if (!runnables.contains(runnable)) {
            runnables.add(runnable);
        }
        getHandler().postDelayed(runnable, delayMill);
    }

    public static void cancelSpecificRunnable(Object tag, Runnable runnable) {
        if (tag == null) {
            throw new IllegalArgumentException("tag is null");
        }
        if (runnable == null) {
            throw new IllegalArgumentException("runnable is null");
        }

        List<Runnable> runnables = runnablesMap.get(tag);
        if (runnables != null) {
            if (runnables.contains(runnable)) {
                getHandler().removeCallbacks(runnable);
                try {
                    runnables.remove(runnable);
                } catch (UnsupportedOperationException e) {
                }
            }

            if (runnables.isEmpty()) {
                runnablesMap.remove(tag);
            }
        }
    }

    public static void cancelAllRunnables(Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag is null");
        }
        List<Runnable> runnables = runnablesMap.get(tag);
        if (runnables != null) {
            for (Runnable runnable : runnables) {
                getHandler().removeCallbacks(runnable);
            }
        }

        runnablesMap.remove(tag);

    }

    private static Handler getHandler() {
        if (handler == null) {
            synchronized (MainThreadExecutor.class) {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

}
