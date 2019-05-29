package com.lph.service.fhoa.department.impl;

import com.google.common.collect.Lists;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.entity.system.Department;
import com.lph.service.fhoa.department.DepartmentManager;
import com.lph.util.PageData;
import com.lph.util.Tools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明： 组织机构
 *
 * @author lvpenghui
 * @since 2019-4-17 11:17:27
 */
@Service("departmentService")
public class DepartmentService implements DepartmentManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("DepartmentMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("DepartmentMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("DepartmentMapper.edit", pd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("DepartmentMapper.datalistPage", page);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("DepartmentMapper.findById", pd);
    }

    @Override
    public PageData findByBianma(PageData pd) {
        return (PageData) dao.findForObject("DepartmentMapper.findByBianma", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Department> listSubDepartmentByParentId(String parentId) {
        return (List<Department>) dao.findForList("DepartmentMapper.listSubDepartmentByParentId", parentId);
    }

    @Override
    public List<Department> listAllDepartment(String parentId) {
        List<Department> departmentList = this.listSubDepartmentByParentId(parentId);
        for (Department depar : departmentList) {
            depar.setTreeurl("department/list.do?DEPARTMENT_ID=" + depar.getDEPARTMENT_ID());
            depar.setSubDepartment(this.listAllDepartment(depar.getDEPARTMENT_ID()));
            depar.setTarget("treeFrame");
            depar.setIcon("static/images/user.gif");
        }
        return departmentList;
    }

    @Override
    public List<PageData> listAllDepartmentToSelect(String parentId, List<PageData> zdepartmentPdList) {
        List<PageData>[] arrayDep = this.listAllbyPd(parentId, zdepartmentPdList);
        List<PageData> departmentPdList = arrayDep[1];
        for (PageData pd : departmentPdList) {
            this.listAllDepartmentToSelect(pd.getString("id"), arrayDep[0]);
        }
        return arrayDep[0];
    }

    @SuppressWarnings("unchecked")
    private List<PageData>[] listAllbyPd(String parentId, List<PageData> zdepartmentPdList) {
        List<Department> departmentList = this.listSubDepartmentByParentId(parentId);
        List<PageData> departmentPdList = Lists.newArrayList();
        for (Department depar : departmentList) {
            PageData pd = new PageData();
            pd.put("id", depar.getDEPARTMENT_ID());
            pd.put("parentId", depar.getPARENT_ID());
            pd.put("name", depar.getNAME());
            pd.put("icon", "static/images/user.gif");
            departmentPdList.add(pd);
            zdepartmentPdList.add(pd);
        }
        List<PageData>[] arrayDep = new List[2];
        arrayDep[0] = zdepartmentPdList;
        arrayDep[1] = departmentPdList;
        return arrayDep;
    }

    @Override
    public String getdepartmentIds(String departmentId) {
        departmentId = Tools.notEmpty(departmentId) ? departmentId : "0";
        List<PageData> zdepartmentPdList = Lists.newArrayList();
        zdepartmentPdList = this.listAllDepartmentToSelect(departmentId, zdepartmentPdList);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (PageData dpd : zdepartmentPdList) {
            sb.append("'");
            sb.append(dpd.getString("id"));
            sb.append("'");
            sb.append(",");
        }
        sb.append("'lph')");
        return sb.toString();
    }

}

