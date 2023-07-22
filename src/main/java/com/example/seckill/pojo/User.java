package com.example.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhoubin
 * @since 2023-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * user id, phone number
     */
    private Long userId;

    @TableField("nickName")
    private String nickName;

    /**
     * md5(md5(pass+ salt)+salt)
     */
    private String passWord;

    private String salt;

    /**
     * profile picture
     */
    private String head;

    /**
     *  register date
     */
    private Date registerDate;

    /**
     *  last time to login
     */
    private Date lastLogin;

    /**
     * the total number of login
     */
    private Integer loginCount;


}
