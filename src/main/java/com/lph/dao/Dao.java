package com.lph.dao;

/**
 * 数据接口
 *
 * @author lvpenghui
 * @since 2019-4-11 20:16:58
 */
public interface Dao {

    /**
     * 保存对象
     *
     * @param str key
     * @param obj value
     * @return 保存结果
     * @throws Exception 可能抛出的异常
     */
    Object save(String str, Object obj) throws Exception;

    /**
     * 修改对象
     *
     * @param str key
     * @param obj value
     * @return 修改对象返回的结果
     * @throws Exception 可能抛出的异常
     */
    Object update(String str, Object obj) throws Exception;

    /**
     * 删除对象
     *
     * @param str key
     * @param obj value
     * @return 删除对象返回的结果
     * @throws Exception 可能抛出的异常
     */
    Object delete(String str, Object obj) throws Exception;

    /**
     * 查找对象
     *
     * @param str key
     * @param obj value
     * @return 查找对象返回的结果
     * @throws Exception 可能抛出的异常
     */
    Object findForObject(String str, Object obj) throws Exception;

    /**
     * 查找对象
     *
     * @param str key
     * @param obj value
     * @return 查找对象返回的结果
     * @throws Exception 可能抛出的异常
     */
    Object findForList(String str, Object obj) throws Exception;

    /**
     * 查找对象封装成Map
     *
     * @param sql   sql
     * @param obj   对象
     * @param key   key
     * @param value value
     * @return 查找对象封装成map
     * @throws Exception 可能抛出的异常
     */
    Object findForMap(String sql, Object obj, String key, String value) throws Exception;

}
