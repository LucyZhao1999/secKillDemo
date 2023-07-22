package com.example.seckill.vo;
//手机号码校验规则

import com.example.seckill.utils.ValidatorUtil;
import com.example.seckill.validator.IsMobile;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号码验证规则

 *
 * @author zhoubin
 * @since 1.0.0
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    @Override
    //初始化
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    //判断是否是必填
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required){
            //如果是必填的话
        return ValidatorUtil.isMobile(value);
    }else {
            //非必填的情况
        if (StringUtils.isEmpty(value)){
            return true;
        }else {
            return ValidatorUtil.isMobile(value);
        }
    }
    }
}

