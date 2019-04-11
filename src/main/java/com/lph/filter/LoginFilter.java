package com.lph.filter;

import com.lph.controller.base.BaseController;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录验证过滤器(废弃  com.lph.interceptor替代)
 *
 * @author lvpenghui
 * @since 2019-4-11 20:43:41
 */
public class LoginFilter extends BaseController implements Filter {

    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig fc) throws ServletException {
        //FileUtil.createDir("d:/FH/topic/");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 调用下一过滤器
        chain.doFilter(req, res);
    }

}
