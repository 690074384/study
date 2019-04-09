package com.lph.controller.app.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lph.controller.base.BaseController;
import com.lph.dao.redis.RedisDao;
import com.lph.util.AppUtil;
import com.lph.util.PageData;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * RedisDemo
 *
 * @author lvpenghui
 * @since 2019-4-8 16:59:39
 */
@Controller
@RequestMapping(value = "/appRedisDemo")
public class RedisDemoController extends BaseController {

    @Resource(name = "redisDaoImpl")
    private RedisDao redisDaoImpl;

    /**
     * 请讲接口 http://127.0.0.1:8080/项目名称/appRedisDemo/redisDemo.do
     * demo展示的在redis存储读取数据的方式，本系统暂时用不到redis，此redis接口可根据实际业务需求选择使用
     * 具体redis的应用场景->百度下即可
     */
    @RequestMapping(value = "/redisDemo")
    @ResponseBody
    public Object redis() {

        Map<String, Object> map = Maps.newHashMap();
        String result = "";

        //删除
        redisDaoImpl.delete("lph0");
        redisDaoImpl.delete("lph");
        redisDaoImpl.delete("lph1");
        redisDaoImpl.delete("lph2");

        //存储字符串
        System.out.println(redisDaoImpl.addString("lph0", "opopopo"));
        //获取字符串
        System.out.println("获取字符串:" + redisDaoImpl.get("lph0"));

        result += "获取字符串:" + redisDaoImpl.get("lph0") + ",";

        Map<String, String> jmap = Maps.newHashMap();
        jmap.put("name", "lphadmin");
        jmap.put("age", "28");
        jmap.put("qq", "690074384");
        //存储Map
        System.out.println(redisDaoImpl.addMap("lph", jmap));
        //获取Map
        System.out.println("获取Map:" + redisDaoImpl.getMap("lph"));

        result += "获取Map:" + redisDaoImpl.getMap("lph") + ",";

        List<String> list = Lists.newArrayList();
        list.add("ssss");
        list.add("bbbb");
        list.add("cccc");
        //存储List
        redisDaoImpl.addList("lph1", list);
        //获取List
        System.out.println("获取List:" + redisDaoImpl.getList("lph1"));

        result += "获取List:" + redisDaoImpl.getList("lph1") + ",";

        Set<String> set = Sets.newHashSet();
        set.add("wwww");
        set.add("eeee");
        set.add("rrrr");
        //存储Set
        redisDaoImpl.addSet("lph2", set);
        //获取Set
        System.out.println("获取Set:" + redisDaoImpl.getSet("lph2"));

        result += "获取Set:" + redisDaoImpl.getSet("lph2") + ",";

        map.put("result", result);

        return AppUtil.returnObject(new PageData(), map);
    }

}
