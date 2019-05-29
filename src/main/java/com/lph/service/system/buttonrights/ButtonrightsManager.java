package com.lph.service.system.buttonrights;

import java.util.List;

import com.lph.util.PageData;

/**
 * 说明：按钮权限 接口
 *
 * @author lvpenghui
 * @since 2019-4-17 14:30:18
 */
public interface ButtonrightsManager {

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void save(PageData pd) throws Exception;

    /**
     * 通过(角色ID和按钮ID)获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void delete(PageData pd) throws Exception;

    /**
     * 列表(全部)
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAll(PageData pd) throws Exception;

    /**
     * 列表(全部)左连接按钮表,查出安全权限标识
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllBrAndQxname(PageData pd) throws Exception;

}

