package com.example.seckill.service;

import com.baomidou.mybatisplus .extension.service.IService;
import com.example.seckill.pojo.SeckillOrder ;
import com.example.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return */
    Long getResult(User user, Long goodsId);
}

