package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            System.out.println(code);
            log.info(code);
            //调用阿里云提供的短信服务api完成发送短信
            // SMSUtils.sendMessage();
            //需要将生成的验证码保存到session
          //  session.setAttribute(phone, code);

            //需要将生成的验证码保存到redis中
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("验证码发送成功");

        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
       // Object codesession = session.getAttribute(phone);
        //从redis中获取保存的验证码
        Object codesession=redisTemplate.opsForValue().get(phone);
        //进行验证码的比对（页面提交的和session中的）
        if (codesession != null && codesession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //对比成功，登录
            if (user!=null){
                session.setAttribute("user",user.getId());
                return R.success(user);
            }
            //如果未注册，自动注册
            else {
                User user1=new User();
                user1.setPhone(phone);
                userService.save(user1);

                //如果用户登录成功，删除redis缓存中的验证码
                redisTemplate.delete(phone);
                return R.success(user);
            }
        }

        return R.error("登录失败");
    }

}
