package com.example.seckill.controller;

import com.baomidou.mybatisplus .core.conditions.query.QueryWrapper ;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillMessage;
import com.example.seckill.pojo.SeckillOrder ;
import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.service.IGoodsService ;
import com.example.seckill.service.IOrderService ;
import com.example.seckill.service.ISeckillOrderService ;
import com.example.seckill.utils.JsonUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example .seckill.vo.RespBeanEnum ;
import jakarta.annotation.Resource;
import org.springframework .beans.factory.InitializingBean ;
import org.springframework .beans.factory.annotation.Autowired;
import org.springframework .data.redis.core.RedisTemplate ;
import org.springframework .data.redis.core.ValueOperations ;
import org.springframework .stereotype.Controller;
import org.springframework .util.CollectionUtils ;
import org.springframework .util.StringUtils ;
import org.springframework .web.bind.annotation.RequestMapping ;
import org.springframework .web.bind.annotation.RequestMethod ;
import org.springframework .web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();


    @RequestMapping(value = "/doSeckill" , method = RequestMethod .POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum .SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate .opsForValue();
        //判断是否重复抢购
        String seckillOrderJson = (String) valueOperations .get("order:" + user.getUserId() + ":" + goodsId);
        if (!StringUtils .isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum .REPEATE_ERROR);
        }
        //内存标记 ,减少Redis访问
        if (EmptyStockMap .get(goodsId)) {
            return RespBean.error(RespBeanEnum .EMPTY_STOCK);
        }
        //预减库存
        Long stock = valueOperations .decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap .put(goodsId,true);
            valueOperations .increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum .EMPTY_STOCK);
        }
        // 请求入队，立即返回排队中
        SeckillMessage message = new SeckillMessage(user, goodsId);
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);
    }

    /**

     * 系统初始化，把商品库存数量加载到Redis *
     * @throws Exception */

    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService .findGoodsVo();
        if (CollectionUtils .isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate .opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap .put(goodsVo.getId(), false);
        });
    }

//        GoodsVo goods = goodsService .findGoodsVoByGoodsId(goodsId);
//        //判断库存
//        if (goods.getStockCount() < 1) {
//            return RespBean.error(RespBeanEnum .EMPTY_STOCK);
//        }
//        //判断是否重复抢购
//        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
//        //        user.getId()).eq(
//        //        "goods_id",
//        //        goodsId));
//        SeckillOrder seckillOrder = (SeckillOrder)
//                redisTemplate .opsForValue().get("order:" + user.getUserId() + ":" + goodsId);
//        if (seckillOrder != null) {
//            return RespBean.error(RespBeanEnum .REPEATE_ERROR);
//        }
//        Order order = orderService .seckill(user, goods);
//        //        if (null != order) {
//        //            return RespBean.success(order);
//        //        }
//        return RespBean.success(order);
//    }
// }

    /**
     * 获取秒杀结果 *
     * @param user
     * @param goodsId
     * @return orderId:成功， -1：秒杀失败， 0：排队中 */
    @RequestMapping(value = "/result", method = RequestMethod .GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum .SESSION_ERROR);
        }
        Long orderId = seckillOrderService .getResult(user, goodsId);
        return RespBean.success(orderId);
    }

}



