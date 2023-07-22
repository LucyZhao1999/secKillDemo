package com.example.seckill.utils;

import org.springframework.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类 *
 * @author zhoubin
 * @since 1.0.0
 */
public class ValidatorUtil {
    //手机号码正则表达式校验
    private static final Pattern mobile_pattern = Pattern.compile("^1([3-9])[0-9]{9}$");

    public static boolean isMobile(String mobile){
        if (!StringUtils.hasLength(mobile)) { // isEmpty 方法已经被弃用
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
