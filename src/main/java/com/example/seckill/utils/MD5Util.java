package com.example.seckill.utils;
//定义了一个 MD5Util 工具类，提供了一些方法来进行密码的加密转换。它使用了 MD5 加密算法，并结合盐值进行密码的转换操作。主方法用于进行一些简单的测试，验证密码转换的正确性。

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * MD5工具类 *
 * @author zhoubin
 * @since 1.0.0
 */
@ComponentScan("com.example.seckill")
//扫描位于com.example.seckill包及其子包中的组件。当Spring 启动时，它会自动扫描该包及其子包中的类，并将它们注册为Spring的组件
public class MD5Util {

    public static String md5(String src) {

        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));//
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }
}

