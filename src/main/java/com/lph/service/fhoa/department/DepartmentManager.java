package com.lph.service.fhoa.department;

import java.util.List;

import com.lph.entity.Page;
import com.lph.entity.system.Department;
import com.lph.util.PageData;

/**
 * 说明： 组织机构接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 10:58:11
 */
public interface DepartmentManager {

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
     * 列表
     *
     * @param page PageData对象List
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
     * 通过编码获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByBianma(PageData pd) throws Exception;

    /**
     * 通过ID获取其子级列表
     *
     * @param parentId 父Id
     * @return List<Department>
     * @throws Exception 可能抛出的异常
     */
    List<Department> listSubDepartmentByParentId(String parentId) throws Exception;

    /**
     * 获取所有数据并填充每条数据的子级列表(递归处理)
     *
     * @param parentId 父Id
     * @return 部门列表
     * @throws Exception 可能抛出的异常
     */
    List<Department> listAllDepartment(String parentId) throws Exception;

    /**
     * 获取所有数据并填充每条数据的子级列表(递归处理)下拉ztree用
     *
     * @param parentId          父Id
     * @param zdepartmentPdList 子部门列表
     * @return List<PageData>
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllDepartmentToSelect(String parentId, List<PageData> zdepartmentPdList) throws Exception;

    /**
     * 获取某个部门所有下级部门ID(返回拼接字符串 in的形式)
     *
     * @param departmentId 部门id
     * @return 部门id
     * @throws Exception 可能抛出的异常
     */
    String getdepartmentIds(String departmentId) throws Exception;

}

