package com.example.seckill.service;
//秒杀

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2023-07-07
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);
}
