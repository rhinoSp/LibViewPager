package com.rhino.viewpager.base;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * @author LuoLin
 * @since Create on 2016/11/21.
 **/
public abstract class BaseHolderData {

    /**
     * Get the layout resources id
     */
    @LayoutRes
    abstract public int getLayoutRes();

    /**
     * Get the class name of ViewHolder.
     */
    @NonNull
    public abstract String getHolderClassName();

}
