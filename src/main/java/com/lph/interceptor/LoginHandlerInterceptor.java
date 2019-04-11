package com.lph.interceptor;

import com.lph.entity.system.User;
import com.lph.util.Const;
import com.lph.util.Jurisdiction;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类名称：登录过滤，权限验证
 *
 * @author lvpenghui
 * @since 2019-4-11 21:04:31
 */
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        if (path.matches(Const.NO_INTERCEPTOR_PATH)) {
            return true;
        } else {
            User user = (User) Jurisdiction.getSession().getAttribute(Const.SESSION_USER);
            if (user != null) {
                path = path.substring(1);
                //访问权限校验
                boolean b = Jurisdiction.hasJurisdiction(path);
                if (!b) {
                    response.sendRedirect(request.getContextPath() + Const.LOGIN);
                }
                return b;
            } else {
                //登陆过滤
                response.sendRedirect(request.getContextPath() + Const.LOGIN);
                return false;
            }
        }
    }

}
