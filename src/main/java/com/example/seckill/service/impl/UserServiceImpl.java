package com.example.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IUserService;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2023-06-27
 */
@Service
    public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    //@Autowired是Spring框架中的一个注解，用于自动装配（依赖注入）对象的依赖关系
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public RespBean login(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //参数校验
//         if (StringUtils.isEmpty(mobile) ||StringUtils.isEmpty(password)){
//             System.out.println("empty");
//             return RespBean.error(RespBeanEnum.LOGINVO_ERROR);
//
//         }
//         if ( !ValidatorUtil.isMobile(mobile)){
//             return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//         }
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null==user){
            //System.out.println("null");
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        //校验密码
        if ( !MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassWord())){
            //System.out.println("password");
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        //return RespBean.success();

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket,user);
        //request.getSession().setAttribute(ticket,user);解决了分布式session问题
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }
        /**
         * 根据cookie获取用户
         * @param userTicket
         * @param request
         * @param response
         * @return */
        @Override

        public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {
            if (StringUtils.isEmpty(userTicket)) {
                return null;
            }
            //String userJson = (String) redisTemplate.opsForValue().get("user:" + userTicket);
            //User user = JsonUtil.jsonStr2Object(userJson, User.class);
            //根据cookie获取用户
            User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
            if (user != null) {
                CookieUtil.setCookie(request,response,"userTicket",userTicket);
            }
            return user;
        }

    /**
     * 更新密码 *
     * @param userTicket
     * @param id
     * @param password
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, Long id, String password) { User user = userMapper.selectById(id);
        if (user == null) {
            throw new GlobalException(RespBeanEnum .MOBILE_NOT_EXIST);
        }
        user.setPassWord(MD5Util.inputPassToDbPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result) {
        //删除Redis
            redisTemplate .delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum .PASSWORD_UPDATE_FAIL);
    }

}
