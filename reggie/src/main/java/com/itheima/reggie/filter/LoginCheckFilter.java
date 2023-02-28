package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1,获取本次请求的uri
        String uri = request.getRequestURI();
        log.info("本次拦截uri: {}", request.getRequestURI());
        //定义不需要处理的请求路径
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/backend/",
                "/front/**",
                "/common/**",
                "/user/sendMsg",        //移动端发送短信
                "/user/login",          //移动端登录

        };

        //2，判断本次请求是否要处理
        boolean check = check(uris, uri);

        long id = Thread.currentThread().getId();
        log.info("线程id：{}", id);

        //3，如果不需要处理，直接放行
        if (check) {
            log.info("不需要处理");
            filterChain.doFilter(request, response);
            return;
        }
        //4-1，判断登录状态，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("已登录");
            Long employeeid = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeid);

            filterChain.doFilter(request, response);
            return;
        }

        //4-2，判断登录状态，则直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("已登录");
            Long userid = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);

            filterChain.doFilter(request, response);
            return;
        }


        //5，如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("未登录");
        return;
    }

    /**
     * 路径匹配，检查本次请求是否放行
     *
     * @param uris
     * @param requesturi
     * @return
     */
    public boolean check(String[] uris, String requesturi) {
        for (String s : uris) {
            boolean match1 = MATCHER.match(s, requesturi);
            if (match1) {
                return true;
            }
        }
        return false;
    }
}
