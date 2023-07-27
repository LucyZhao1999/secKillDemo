package com.example.seckill.pojo;

import com.example.seckill.pojo.User;
import lombok.AllArgsConstructor ;
import lombok.Data;
import lombok.NoArgsConstructor ;

/**
 * @author zhoubin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}

