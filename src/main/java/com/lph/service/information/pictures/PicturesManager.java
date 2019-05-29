package com.lph.service.information.pictures;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;


/**
 * 图片管理接口
 *
 * @author lvpenghui
 * @since 2019-4-17 14:15:45
 */
public interface PicturesManager {

    /**
     * 列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> list(Page page) throws Exception;

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void save(PageData pd) throws Exception;

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void delete(PageData pd) throws Exception;

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void edit(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 批量删除
     *
     * @param arrayDataIds 删除数据对应的id数组
     * @throws Exception 可能抛出的异常
     */
    void deleteAll(String[] arrayDataIds) throws Exception;

    /**
     * 批量获取
     *
     * @param arrayDataIds 获取数据对应的id数组
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> getAllById(String[] arrayDataIds) throws Exception;

    /**
     * 删除图片
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void delTp(PageData pd) throws Exception;

}

