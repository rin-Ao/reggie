package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     *  新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);


    /**
     * 修改套餐
     * @param setmealDto
     */
   public void UpdateWithDish(SetmealDto setmealDto);


    /**
     * 修改套餐页面回显
     * @param id
     */
   public SetmealDto GetWithDish(Long id);


    /**
     * 批量起售/停售
     * @param ids
     */
   public void statu(Long[] ids);


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
   public void DeleteWithDish(List<Long> ids);
}

