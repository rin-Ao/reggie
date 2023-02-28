package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 购物车添加
     * @param shoppingCart
     */
    public void add(ShoppingCart shoppingCart);
}
