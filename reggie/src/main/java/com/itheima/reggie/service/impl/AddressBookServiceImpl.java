package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>implements AddressBookService {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 修改地址
     * @param addressBook
     */
    @Override
    public void updateAddress(AddressBook addressBook) {
        //获取id
        Long id = addressBook.getId();

        //删除原来
        LambdaQueryWrapper<AddressBook>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id);
        addressBookService.remove(queryWrapper);
        //插入新数据
        addressBookService.save(addressBook);


    }


    /**
     * 删除地址
     * @param ids
     */
    @Override
    public void deleteAddress(Long ids) {
        //根据id获取addressbook对象
        LambdaQueryWrapper<AddressBook>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,ids);

        //删除地址
        addressBookService.remove(queryWrapper);
    }
}
