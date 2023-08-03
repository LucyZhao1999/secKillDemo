package com.example.seckill.service.impl;

import com.baomidou.mybatisplus .core.conditions.query.QueryWrapper ;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus .extension.service.impl.ServiceImpl ;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillGoods ;
import com.example.seckill.pojo.SeckillOrder ;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IGoodsService ;
import com.example.seckill.service.IOrderService ;
import com.example.seckill.service.ISeckillGoodsService ;
import com.example.seckill.service.ISeckillOrderService ;
import com.example.seckill.utils.JsonUtil;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.OrderDetailVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import org.springframework .beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework .stereotype.Service;
import org.springframework .transaction .annotation.Transactional ;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p> *
 * @author zhoubin
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService ;
    @Autowired
    private IGoodsService goodsService ;
    @Autowired
    @Resource
    private OrderMapper orderMapper ;
    @Autowired
    private ISeckillOrderService seckillOrderService ;
    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 秒杀 *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate .opsForValue();
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService .getOne(new
                QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        boolean seckillGoodsResult = seckillGoodsService .update(
                new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count- 1").eq("goods_id", goods.getId()).gt("stock_count" , 0));
        // seckillGoodsService.updateById(seckillGoods);
        if (seckillGoods .getStockCount() < 1) {
            //判断是否还有库存
            valueOperations .set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods .getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper .insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder .setOrderId(order.getId());
        seckillOrder .setUserId(user.getUserId());
        seckillOrder .setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        valueOperations.set("order:" + user.getUserId() + ":" + goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;
    }


    /**
     * 订单详情
     * @param orderId
     * @return */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null == orderId) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoodsVo(goodsVo);
        detail.setOrder(order);
        return detail;
    }

    /**
     * 验证请求地址 *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user==null || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate .opsForValue().get("seckillPath:" + user.getUserId() + ":" + goodsId);
        return path.equals(redisPath);
    }


    /**
     * 生成秒杀地址 *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate .opsForValue().set("seckillPath:" + user.getUserId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验验证码 *
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (StringUtils .isEmpty(captcha) ||null==user||goodsId<0){

        return false;
    }
        String redisCaptcha = (String) redisTemplate .opsForValue().get("captcha:" + user.getUserId() + ":" + goodsId);
        return redisCaptcha .equals(captcha);
    }



}

