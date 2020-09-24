package com.rhino.viewpager.factory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.viewpager.base.BaseHolder;
import com.rhino.viewpager.base.BaseHolderFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author LuoLin
 * @since Create on 2016/11/21.
 **/
public class SimpleHolderFactory extends BaseHolderFactory {

    public static SimpleHolderFactory create() {
        return new SimpleHolderFactory();
    }

    @SuppressWarnings("all")
    @Override
    public BaseHolder buildHolder(ViewGroup parent, int layoutResId, String holderClassName) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        BaseHolder holder = null;
        try {
            Class clazz = Class.forName(holderClassName);
            holder = (BaseHolder) clazz.getConstructor(View.class).newInstance(view);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return holder;
    }
}
