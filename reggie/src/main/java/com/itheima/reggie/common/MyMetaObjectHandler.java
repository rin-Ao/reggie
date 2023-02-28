package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /*
    添加操作，自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
      log.info("执行了吗");
      log.info(metaObject.toString());
      metaObject.setValue("createTime", LocalDateTime.now());
      metaObject.setValue("updateTime",LocalDateTime.now());
      metaObject.setValue("createUser",BaseContext.getCurrentId());
      metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    /*
    更新操作，自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("执行了");
        long id = Thread.currentThread().getId();
        log.info("线程id：{}",id);


        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
