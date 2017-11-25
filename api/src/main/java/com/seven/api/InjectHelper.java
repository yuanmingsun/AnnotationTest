package com.seven.api;

import android.app.Activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by sunyuanming on 17-11-25.
 */

public class InjectHelper {
    public static void Inject(Activity activity)
    {
        String classFullName=activity.getClass().getName()+"$$ViewInjector";
        try {
            Class proxy=Class.forName(classFullName);
            Constructor constructor=proxy.getConstructor(activity.getClass());
            constructor.newInstance(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
