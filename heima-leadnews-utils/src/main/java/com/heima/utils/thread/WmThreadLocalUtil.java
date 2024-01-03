package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

/**
 * @author ruoling
 * @date 2023/12/27 17:01:41
 * @description
 */
public class WmThreadLocalUtil {
    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setWmUser(WmUser wmUser){
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    public static WmUser getWmUser(){
        return WM_USER_THREAD_LOCAL.get();
    }

    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }
}
