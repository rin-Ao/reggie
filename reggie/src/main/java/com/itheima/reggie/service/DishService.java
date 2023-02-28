package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的我口味数据，需要操作两张表：dish, dishflavor
    public void SaveWithFlavor(DishDto dishDto);

    //根据id查询菜品和口味信息
    public DishDto GetWithFlavor(Long id);

   public void updateWithFlavor(DishDto dishDto);


   public void DeleteWithFlavor(Long[] id);


   //修改销售状态(批量里面已包含)
 //  public void statu(Long id);


   //批量修改销售状态
   public void status(Long[] ids);
}
