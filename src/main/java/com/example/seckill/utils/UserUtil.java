package com.example.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.RespBean;

import java.io.ByteArrayOutputStream ;
import java.io.File;
import java.io.InputStream ;
import java.io.OutputStream ;
import java.io.RandomAccessFile ;
import java.net.HttpURLConnection ;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager ;
import java.sql.PreparedStatement ;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        //生成用户

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUserId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickName("user" + i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassWord(MD5Util.inputPassToDbPass("123456", user.getSalt())); users.add(user);
        }
        System.out.println("create user");
        //插入数据库
        Connection conn = getConn();
        String sql = "insert into t_user(login_count, nickName, register_date, salt, pass_word, user_id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickName());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime())); pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassWord());
            pstmt.setLong(6, user.getUserId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");
        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin" ;
        File file = new File("/Users/apple/IdeaProjects/seckill-demo/config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection(); co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getUserId() + "&password=" + MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream .read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream .close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getUserId());

            String row = user.getUserId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getUserId());
        }
        raf.close();

        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill" ;
        String username = "root";
        String password = "1234aaaa";
        String driver = "com.mysql.cj.jdbc.Driver" ;
        Class.forName(driver);
        return DriverManager .getConnection(url, username, password); }
    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}

