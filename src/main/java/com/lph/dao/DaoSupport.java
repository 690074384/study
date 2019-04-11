package com.lph.dao;

import java.util.List;

import javax.annotation.Resource;

import com.lph.util.Logger;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author lvpenghui
 * @since 2019-4-11 20:22:37
 */
@Repository("daoSupport")
public class DaoSupport implements Dao {

    protected Logger logger = Logger.getLogger(this.getClass());

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Object save(String str, Object obj) throws Exception {
        return sqlSessionTemplate.insert(str, obj);
    }

    /**
     * 批量更新
     *
     * @param str  字符串
     * @param objs 更新对象
     * @return 批量更新结果
     * @throws Exception 可能抛出的异常
     */
    public Object batchSave(String str, List objs) throws Exception {
        return sqlSessionTemplate.insert(str, objs);
    }

    /**
     * 修改对象
     *
     * @param str 字符串
     * @param obj 更新的对象
     * @return 更新结果
     * @throws Exception 可能抛出的异常
     */
    @Override
    public Object update(String str, Object obj) throws Exception {
        return sqlSessionTemplate.update(str, obj);
    }

    public void batchUpdate(String str, List objs) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        //批量执行器
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            if (objs != null) {
                for (Object obj : objs) {
                    sqlSession.update(str, obj);
                }
                sqlSession.flushStatements();
                sqlSession.commit();
                sqlSession.clearCache();
            }
        } finally {
            sqlSession.close();
        }
    }

    public Object batchDelete(String str, List objs) {
        return sqlSessionTemplate.delete(str, objs);
    }

    @Override
    public Object delete(String str, Object obj) throws Exception {
        return sqlSessionTemplate.delete(str, obj);
    }


    @Override
    public Object findForObject(String str, Object obj) {
        return sqlSessionTemplate.selectOne(str, obj);
    }

    @Override
    public Object findForList(String str, Object obj) {
        return sqlSessionTemplate.selectList(str, obj);
    }

    @Override
    public Object findForMap(String str, Object obj, String key, String value) {
        return sqlSessionTemplate.selectMap(str, obj, key);
    }

}


