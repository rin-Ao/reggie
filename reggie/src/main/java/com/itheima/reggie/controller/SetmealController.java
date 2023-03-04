package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);


        //全局清理缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);


        return R.success("添加套餐成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构建分页构造器
        Page<Setmeal> pageinfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //添加条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行分页查询
        setmealService.page(pageinfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageinfo, dtoPage, "records");
        List<Setmeal> records = pageinfo.getRecords();
        List<SetmealDto> list = records.stream().map((item -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        })).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.GetWithDish(id);

        return R.success(setmealDto);
    }


    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.UpdateWithDish(setmealDto);

        //全局清理缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);

        return R.success("修改套餐成功");
    }


    @PostMapping("/status/{status}")
    public R<String> statu(Long[] ids) {
        setmealService.statu(ids);
        //全局清理缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);

        return R.success("修改状态成功");
    }


    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.DeleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> listsetmeal(Setmeal setmeal) {
        List<Setmeal> list=null;
        //查询redis缓存是否存在
        //构造动态key
        String key="setmeal_"+setmeal.getId()+"_"+setmeal.getStatus();
        //查询缓存
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        //存在直接返回
        if (list!=null){
            return R.success(list);
        }
        //不存在查询数据库并保存到redis缓存

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getId() != null, Setmeal::getId, setmeal.getId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(queryWrapper);

        //保存到redis缓存
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);
        return R.success(list);
    }


}

