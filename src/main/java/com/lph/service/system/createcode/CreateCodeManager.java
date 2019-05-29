package com.lph.service.system.createcode;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 类名称：代码生成器接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 14:33:32
 */
public interface CreateCodeManager {

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
     * 列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> list(Page page) throws Exception;

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
     * @param arrayDataIds 需要删除的id数组
     * @throws Exception 可能抛出的异常
     */
    void deleteAll(String[] arrayDataIds) throws Exception;

    /**
     * 列表(主表)
     *
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listFa() throws Exception;

}
