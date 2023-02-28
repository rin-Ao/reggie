package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

    /**
     * 修改地址
     * @param addressBook
     */
   public void updateAddress(AddressBook addressBook);


    /**
     * 删除地址
     * @param ids
     */
   public void deleteAddress(Long ids);
}
