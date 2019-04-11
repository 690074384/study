package com.lph.dao;

import java.util.List;

import com.lph.util.Logger;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * 第2数据源
 *
 * @author lvpenghui
 * @since 2019-4-11 20:26:57
 */
@Repository("daoSupport2")
public class DaoSupport2 implements Dao {
    protected Logger logger = Logger.getLogger(this.getClass());
    /**
     * 去掉注释，打开第2数据源   @Resource(name = "sqlSessionTemplate2")
     */
    private SqlSessionTemplate sqlSessionTemplate2;

    @Override
    public Object save(String str, Object obj) throws Exception {
        return sqlSessionTemplate2.insert(str, obj);
    }

    public Object batchSave(String str, List objs) throws Exception {
        return sqlSessionTemplate2.insert(str, objs);
    }

    @Override
    public Object update(String str, Object obj) throws Exception {
        return sqlSessionTemplate2.update(str, obj);
    }

    public void batchUpdate(String str, List objs) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate2.getSqlSessionFactory();
        //批量执行器
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            if (null != objs) {
                for (Object obj : objs) {
                    sqlSession.update(str, obj);
                }
                logger.info("第二数据源关闭");
                sqlSession.flushStatements();
                sqlSession.commit();
                sqlSession.clearCache();
            }
        } finally {
            sqlSession.close();
        }
    }

    public Object batchDelete(String str, List objs) throws Exception {
        return sqlSessionTemplate2.delete(str, objs);
    }

    @Override
    public Object delete(String str, Object obj) throws Exception {
        return sqlSessionTemplate2.delete(str, obj);
    }

    @Override
    public Object findForObject(String str, Object obj) {
        return sqlSessionTemplate2.selectOne(str, obj);
    }

    @Override
    public Object findForList(String str, Object obj) {
        return sqlSessionTemplate2.selectList(str, obj);
    }

    @Override
    public Object findForMap(String str, Object obj, String key, String value) {
        return sqlSessionTemplate2.selectMap(str, obj, key);
    }

}


