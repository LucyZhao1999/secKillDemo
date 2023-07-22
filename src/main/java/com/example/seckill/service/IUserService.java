package com.example.seckill.service;
//登陆功能
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.example.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2023-06-27
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p >
 *
 * @author Jojo
 * @since 2023-06-26
 */
public interface IUserService extends IService<User> {

    RespBean login(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo);

    //根据cookie获取用户
    User getByUserTicket(String userTicket,HttpServletRequest request,HttpServletResponse response);

    /**
     * 更新密码
     * @param userTicket
     * @param id
     * @param password
     * @return */
    RespBean updatePassword(String userTicket,Long id,String password);

}
