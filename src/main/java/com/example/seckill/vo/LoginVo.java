package com.example.seckill.vo;
//登陆参数

//import com.example.seckill.validator.IsMobile;
import com.example.seckill.validator.IsMobile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
//import org.hibernate.validator.constraints.Length;
//import javax.validation.constraints.NotNull;

/**
 * 登录入参 *
 * @author zhoubin
 * @since 1.0.0
 */
@Data
//@Data 是 Lombok 提供的一个注解，用于自动生成 Java 类的常用方法（getter、setter、equals、hashCode、toString 等）
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {
    @NotNull //Validation本身的组件
    @IsMobile//自定义组件
    private String mobile;
    @NotNull//Validation本身的组件
    @Length(min = 32)//Validation本身的组件
    private String password;

}

