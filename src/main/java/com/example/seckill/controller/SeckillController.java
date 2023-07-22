package com.example.seckill.controller;

import com.baomidou.mybatisplus .core.conditions.query.QueryWrapper ;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillOrder ;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IGoodsService ;
import com.example.seckill.service.IOrderService ;
import com.example.seckill.service.ISeckillOrderService ;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example .seckill.vo.RespBeanEnum ;
import org.springframework .beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework .ui.Model;
import org.springframework .web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework .web.bind.annotation.RequestMethod;

/**
 * <p>
 * 前端控制器
 * </p>

 *
 * @author zhoubin
 * @since 1.0.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private IGoodsService goodsService ;
    @Autowired
    private ISeckillOrderService seckillOrderService ;
    @Autowired
    private IOrderService orderService ;
    @Autowired
    private RedisTemplate redisTemplate ;

    @RequestMapping(value = "/doSeckill" , method = RequestMethod .POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum .SESSION_ERROR);
        }
        GoodsVo goods = goodsService .findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum .EMPTY_STOCK);
        }
        //判断是否重复抢购
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
        //        user.getId()).eq(
        //        "goods_id",
        //        goodsId));
        SeckillOrder seckillOrder = (SeckillOrder)
                redisTemplate .opsForValue().get("order:" + user.getUserId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum .REPEATE_ERROR);
        }
        Order order = orderService .seckill(user, goods);
        //        if (null != order) {
        //            return RespBean.success(order);
        //        }
        return RespBean.success(order);
    }
}


