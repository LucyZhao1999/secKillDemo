package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.service.IOrderService ;
import com.example.seckill.vo.OrderDetailVo ;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum ;
import jakarta.annotation.Resource;
import org.springframework .beans.factory.annotation.Autowired;
import org.springframework .stereotype.Controller;
import org.springframework .web.bind.annotation.RequestMapping ;
import org.springframework .web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p> *
 * @author zhoubin
 * @since 1.0.0
 */

@RequestMapping("/order")
public class OrderController {

    @Resource
    @Autowired
    private IOrderService orderService;


    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if (null==user){
            return RespBean.error(RespBeanEnum .SESSION_ERROR);
        }
        OrderDetailVo detail = orderService .detail(orderId);
        return RespBean.success(detail);
    }


}

