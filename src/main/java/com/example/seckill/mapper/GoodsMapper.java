package com.example.seckill.mapper;
//获取商品列表

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckill.pojo.Goods;
import com.example.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p> *
 * @author zhoubin
 * @since 1.0.0
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();


    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}

