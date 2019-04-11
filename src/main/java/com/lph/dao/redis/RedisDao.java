package com.lph.dao.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 说明： 第2数据源例子接口
 *
 * @author lvpenghui
 * @since 2019-4-11 20:04:48
 */
public interface RedisDao {

    /**
     * 新增(存储字符串)
     *
     * @param key   key
     * @param value value
     * @return 是否新增成功
     */
    boolean addString(String key, String value);

    /**
     * 拼接字符串
     *
     * @param key   key
     * @param value value
     * @return 拼接字符串
     */
    boolean appendString(String key, String value);

    /**
     * 新增(存储Map)
     *
     * @param key key
     * @param map map
     * @return 增加map结果
     */
    String addMap(String key, Map<String, String> map);

    /**
     * 获取map
     *
     * @param key key
     * @return 获取map
     */
    Map<String, String> getMap(String key);

    /**
     * 新增(存储List)
     *
     * @param key  key
     * @param list value
     */
    void addList(String key, List<String> list);

    /**
     * 获取List
     *
     * @param key key
     * @return 获取到的list
     */
    List<String> getList(String key);

    /**
     * 新增(存储set)
     *
     * @param key key
     * @param set set
     */
    void addSet(String key, Set<String> set);

    /**
     * 获取Set
     *
     * @param key key
     * @return value
     */
    Set<String> getSet(String key);

    /**
     * 删除
     *
     * @param key key
     * @return 删除结果
     */
    boolean delete(String key);

    /**
     * 删除多个
     *
     * @param keys 多个list
     */
    void delete(List<String> keys);

    /**
     * 修改
     *
     * @param key   key
     * @param value value
     * @return 修改结果
     */
    boolean eidt(String key, String value);

    /**
     * 通过ket获取数据
     *
     * @param keyId key
     * @return value
     */
    String get(String keyId);

}
