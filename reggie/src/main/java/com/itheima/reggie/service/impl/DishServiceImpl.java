package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Transactional
    @Override
    public void SaveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long id = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dishflavor
        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品和口味信息
    @Override
    public DishDto GetWithFlavor(Long id) {
        //查询菜品基本信息，dish表
        Dish byId = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId, dishDto);

        //查询菜品基本信息，dishflavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据   dishflavor表的delete操作
        LambdaQueryWrapper<DishFlavor>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前菜品对应口味数据   dishflavor表的insert操作
        List<DishFlavor>flavors=dishDto.getFlavors();

        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }


    //菜品删除
    @Override
    public void DeleteWithFlavor(Long[] id) {
        //菜品根据id从dish表删除
        for (Long ids : id) {
            this.removeById(ids);
        }


        //菜品根据id从dishflavor表删除
        for (Long ids : id) {

            LambdaQueryWrapper<DishFlavor>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,ids);

            dishFlavorService.remove(queryWrapper);
        }

    }



    @Transactional
//    public void statu(Long id) {
//        //根据id获取菜品
//        Dish dish = this.getById(id);
//
//        if (dish.getStatus()==1){
//            dish.setStatus(0);
//        }
//        else
//            dish.setStatus(1);
//
//
//            dishService.updateById(dish);
//    }



    @Override
    public void status(Long[] ids) {
        List<Dish> dishList=new ArrayList<>();

        for (Long id : ids) {
            Dish dish = this.getById(id);
            if (dish.getStatus()==1){
                dish.setStatus(0);
            }
            else
                dish.setStatus(1);

            dishList.add(dish);
        }
        dishService.updateBatchById(dishList);

    }


}
