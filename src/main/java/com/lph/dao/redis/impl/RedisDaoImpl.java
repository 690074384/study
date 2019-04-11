package com.lph.dao.redis.impl;

import com.google.common.collect.Maps;
import com.lph.dao.AbstractBaseRedisDao;
import com.lph.dao.redis.RedisDao;
import com.lph.util.Constants;
import com.lph.util.DbFH;
import com.lph.util.PageData;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * redis 实现类
 *
 * @author lvpenghui
 * @since 2019-4-11 17:48:04
 */
@Repository("redisDaoImpl")
public class RedisDaoImpl extends AbstractBaseRedisDao<String, PageData> implements RedisDao {


    /**
     * 新增(存储字符串)
     *
     * @param key   key
     * @param value value
     * @return 增加成功/失败
     */
    @Override
    public boolean addString(final String key, final String value) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] jkey = serializer.serialize(key);
                byte[] jvalue = serializer.serialize(value);
                return connection.setNX(jkey, jvalue);
            }
        });
    }

    /**
     * 新增(拼接字符串)
     *
     * @param key   key
     * @param value value
     * @return 字符串连接结果
     */
    @Override
    public boolean appendString(final String key, final String value) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] jkey = serializer.serialize(key);
                byte[] jvalue = serializer.serialize(value);
                if (connection.exists(jkey)) {
                    connection.append(jkey, jvalue);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 新增(存储Map)
     *
     * @param key key
     * @param map value
     * @return 存储map结果
     */
    @Override
    public String addMap(String key, Map<String, String> map) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }

        String result = jedis.hmset(key, map);
        jedis.close();
        return result;
    }

    /**
     * 获取map
     *
     * @param key key
     * @return 获取到的map值
     */
    @Override
    public Map<String, String> getMap(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        Map<String, String> map = Maps.newHashMap();
        for (String ikey : jedis.hkeys(key)) {
            map.put(ikey, jedis.hmget(key, ikey).get(0));
        }
        jedis.close();
        return map;
    }

    /**
     * 新增(存储List)
     *
     * @param key  key
     * @param list value
     */
    @Override
    public void addList(String key, List<String> list) {
        Jedis jedis = getJedis();
        //开始前，先移除所有的内容
        if (jedis != null) {
            jedis.del(key);
        }
        for (String value : list) {
            if (jedis != null) {
                jedis.rpush(key, value);
            }
        }
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取List
     *
     * @param key key
     * @return list对象
     */
    @Override
    public List<String> getList(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        List<String> list = jedis.lrange(key, 0, -1);
        jedis.close();

        return list;
    }

    /**
     * 新增(存储set)
     *
     * @param key key
     * @param set value
     */
    @Override
    public void addSet(String key, Set<String> set) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return;
        }
        jedis.del(key);
        for (String value : set) {
            jedis.sadd(key, value);
        }
        jedis.close();
    }

    /**
     * 获取Set
     *
     * @param key key
     * @return value
     */
    @Override
    public Set<String> getSet(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        Set<String> set = jedis.smembers(key);
        jedis.close();
        return set;
    }

    /**
     * 删除
     *
     * @param key key
     */
    @Override
    public boolean delete(final String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] jkey = serializer.serialize(key);
                if (connection.exists(jkey)) {
                    connection.del(jkey);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 删除多个
     *
     * @param keys key
     */
    @Override
    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 修改
     *
     * @param key   key
     * @param value value
     */
    @Override
    public boolean eidt(String key, String value) {
        if (delete(key)) {
            addString(key, value);
            return true;
        }
        return false;
    }

    /**
     * 通过key获取值
     *
     * @param keyId key
     */
    @Override
    public String get(final String keyId) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] jkey = serializer.serialize(keyId);
                byte[] jvalue = connection.get(jkey);
                if (jvalue == null) {
                    return null;
                }
                return serializer.deserialize(jvalue);
            }
        });
    }

    /**
     * 获取Jedis
     *
     * @return Jedis
     */
    private Jedis getJedis() {
        Properties pros = getPprVue();
        //是否开启
        String isopen = pros.getProperty("redis.isopen");
        //地址
        String host = pros.getProperty("redis.host");
        //端口
        String port = pros.getProperty("redis.port");
        //密码
        String pass = pros.getProperty("redis.pass");
        if (Constants.YES.equals(isopen)) {
            Jedis jedis = new Jedis(host, Integer.parseInt(port));
            jedis.auth(pass);
            return jedis;
        } else {
            return null;
        }
    }

    /**
     * 读取redis.properties 配置文件
     *
     * @return Properties
     */
    private Properties getPprVue() {
        InputStream inputStream = DbFH.class.getClassLoader().getResourceAsStream("redis.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            //读取配置文件出错
            e.printStackTrace();
        }
        return p;
    }

}
