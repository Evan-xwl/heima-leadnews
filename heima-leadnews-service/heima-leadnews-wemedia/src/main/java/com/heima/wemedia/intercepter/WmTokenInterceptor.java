package com.heima.wemedia.intercepter;

import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ruoling
 * @date 2023/12/27 16:53:45
 * @description 将gateway存入header中的userId信息存入ThreadLocal中,方便读取
 */
public class WmTokenInterceptor implements HandlerInterceptor {

    /**将userId存入*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if(userId != null){
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.valueOf(userId));
            WmThreadLocalUtil.setWmUser(wmUser);
        }
        return true;
    }

    /**清理线程的数据*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        WmThreadLocalUtil.clear();
    }
}
