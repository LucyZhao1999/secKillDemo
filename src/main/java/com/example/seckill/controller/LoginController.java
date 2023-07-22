package com.example.seckill.controller;

import com.example.seckill.service.IUserService;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping; import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 登录 *
 * @author zhoubin
 * @since 1.0.0
 */

@Controller
@RequestMapping("/login")
@Slf4j
//@Slf4j是一种用于在Java类中自动创建日志记录器的注解。通过在类上添加@Slf4j注解，您可以使用log对象来记录日志，而无需手动创建和初始化日志记录器。

public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * 跳转登录页 *
     * @return */
    @RequestMapping("/toLogin")
    public String toLogin() {

        return "login";
    }


    /**
     * 登录功能 *
     * @return */
    @RequestMapping("/doLogin")
    @ResponseBody
    //在Java Web应用程序中，通过 HttpServletRequest 可以获取客户端的请求信息，并根据请求处理相应的逻辑。而通过 HttpServletResponse 可以向客户端发送响应数据，包括设置响应状态码、设置响应头、发送响应内容等
    public RespBean doLogin(HttpServletRequest request, HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        return userService.login(request, response, loginVo);
    }



}
