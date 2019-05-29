package com.lph.controller.fhoa.staff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.fhoa.datajur.DatajurManager;
import com.lph.service.fhoa.department.DepartmentManager;
import com.lph.service.fhoa.staff.StaffManager;
import com.lph.util.*;
import net.sf.json.JSONArray;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：员工管理
 *
 * @author lvpenghui
 * @since 2019-4-9 15:27:35
 */
@Controller
@RequestMapping(value = "/staff")
public class StaffController extends BaseController {
    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "staff/list.do";
    @Resource(name = "staffService")
    private StaffManager staffService;
    @Resource(name = "departmentService")
    private DepartmentManager departmentService;
    @Resource(name = "datajurService")
    private DatajurManager datajurService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增Staff");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("STAFF_ID", this.get32UUID());
        //绑定账号ID
        pd.put("USER_ID", "");
        //保存员工信息到员工表
        staffService.save(pd);
        //获取某个部门所有下级部门ID
        String departmentIds = departmentService.getdepartmentIds(pd.getString("DEPARTMENT_ID"));
        //主键
        pd.put("DATAJUR_ID", pd.getString("STAFF_ID"));
        //部门ID集
        pd.put("DEPARTMENT_IDS", departmentIds);
        //把此员工默认部门及以下部门ID保存到组织数据权限表
        datajurService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除Staff");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        staffService.delete(pd);
        out.write("success");
        out.close();
    }

    /**
     * 修改
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改Staff");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        staffService.edit(pd);
        //获取某个部门所有下级部门ID
        String departmentIds = departmentService.getdepartmentIds(pd.getString("DEPARTMENT_ID"));
        pd.put("DATAJUR_ID", pd.getString("STAFF_ID"));
        //部门ID集
        pd.put("DEPARTMENT_IDS", departmentIds);
        //把此员工默认部门及以下部门ID保存到组织数据权限表
        datajurService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表(检索条件中的部门，只列出此操作用户最高部门权限以下所有部门的员工)
     *
     * @param page 分页
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表Staff");
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        String departmentIds = pd.getString("DEPARTMENT_ID");
        //只有检索条件穿过值时，才不为null,否则读取缓存
        pd.put("DEPARTMENT_ID", null == departmentIds ? Jurisdiction.getDEPARTMENT_ID() : departmentIds);
        //部门检索条件,列出此部门下级所属部门的员工
        pd.put("item", (null == pd.getString("DEPARTMENT_ID") ? Jurisdiction.getDEPARTMENT_IDS() : departmentService.getdepartmentIds(pd.getString("DEPARTMENT_ID"))));

        /* 比如员工 张三 所有部门权限的部门为 A ， A 的下级有  C , D ,F ，那么当部门检索条件值为A时，只列出A以下部门的员工(自己不能修改自己的信息，只能上级部门修改)，不列出部门为A的员工，当部门检索条件值为C时，可以列出C及C以下员工 */
        if (!(null == departmentIds || departmentIds.equals(Jurisdiction.getDEPARTMENT_ID()))) {
            pd.put("item", pd.getString("item").replaceFirst("\\(", "\\('" + departmentIds + "',"));
        }

        page.setPd(pd);
        //列出Staff列表
        List<PageData> varList = staffService.list(page);
        //列表页面树形下拉框用(保持下拉树里面的数据不变)
        String ztreedepartmentId = pd.getString("ZDEPARTMENT_ID");
        ztreedepartmentId = Tools.notEmpty(ztreedepartmentId) ? ztreedepartmentId : Jurisdiction.getDEPARTMENT_ID();
        pd.put("ZDEPARTMENT_ID", ztreedepartmentId);
        List<PageData> zdepartmentPdList = Lists.newArrayList();
        JSONArray arr = JSONArray.fromObject(departmentService.listAllDepartmentToSelect(ztreedepartmentId, zdepartmentPdList));
        mv.addObject("zTreeNodes", arr.toString());
        PageData dpd = departmentService.findById(pd);
        if (null != dpd) {
            ztreedepartmentId = dpd.getString("NAME");
        }
        mv.addObject("depname", ztreedepartmentId);
        mv.setViewName("fhoa/staff/staff_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去新增页面
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        List<PageData> zdepartmentPdList = Lists.newArrayList();
        JSONArray arr = JSONArray.fromObject(departmentService.listAllDepartmentToSelect(Jurisdiction.getDEPARTMENT_ID(), zdepartmentPdList));
        mv.addObject("zTreeNodes", (null == arr ? "" : arr.toString()));
        mv.addObject("msg", "save");
        mv.addObject("pd", pd);
        mv.setViewName("fhoa/staff/staff_edit");
        return mv;
    }

    /**
     * 去修改页面
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        List<PageData> zdepartmentPdList = Lists.newArrayList();
        JSONArray arr = JSONArray.fromObject(departmentService.listAllDepartmentToSelect(Jurisdiction.getDEPARTMENT_ID(), zdepartmentPdList));
        mv.addObject("zTreeNodes", (null == arr ? "" : arr.toString()));
        //根据ID读取
        pd = staffService.findById(pd);
        mv.setViewName("fhoa/staff/staff_edit");
        mv.addObject("depname", departmentService.findById(pd).getString("NAME"));
        mv.addObject("msg", "edit");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 批量删除
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "批量删除Staff");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (null != dataIds && !"".equals(dataIds)) {
            String[] allDatas = dataIds.split(Constants.COMMA);
            staffService.deleteAll(allDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 绑定用户
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/userBinding")
    @ResponseBody
    public Object userBinding() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "绑定用户");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        staffService.userBinding(pd);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 导出到excel
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "导出Staff到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        PageData pd = this.getPageData();
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("titles", getExcelTitles());
        List<PageData> varOList = staffService.listAll(pd);
        List<PageData> varList = Lists.newArrayList();
        for (PageData aVarOList : varOList) {
            PageData vpd = getPageData(aVarOList);
            varList.add(vpd);
        }
        dataMap.put("varList", varList);
        ObjectExcelView erv = new ObjectExcelView();
        return new ModelAndView(erv, dataMap);
    }

    /**
     * 根据list获取pd对象
     *
     * @param aVarOList list中的对象
     * @return PageData对象
     */
    private PageData getPageData(PageData aVarOList) {
        PageData vpd = new PageData();
        vpd.put("var1", aVarOList.getString("NAME"));
        vpd.put("var2", aVarOList.getString("NAME_EN"));
        vpd.put("var3", aVarOList.getString("BIANMA"));
        vpd.put("var4", aVarOList.getString("DEPARTMENT_ID"));
        vpd.put("var5", aVarOList.getString("FUNCTIONS"));
        vpd.put("var6", aVarOList.getString("TEL"));
        vpd.put("var7", aVarOList.getString("EMAIL"));
        vpd.put("var8", aVarOList.getString("SEX"));
        vpd.put("var9", aVarOList.getString("BIRTHDAY"));
        vpd.put("var10", aVarOList.getString("NATION"));
        vpd.put("var11", aVarOList.getString("JOBTYPE"));
        vpd.put("var12", aVarOList.getString("JOBJOINTIME"));
        vpd.put("var13", aVarOList.getString("FADDRESS"));
        vpd.put("var14", aVarOList.getString("POLITICAL"));
        vpd.put("var15", aVarOList.getString("PJOINTIME"));
        vpd.put("var16", aVarOList.getString("SFID"));
        vpd.put("var17", aVarOList.getString("MARITAL"));
        vpd.put("var18", aVarOList.getString("DJOINTIME"));
        vpd.put("var19", aVarOList.getString("POST"));
        vpd.put("var20", aVarOList.getString("POJOINTIME"));
        vpd.put("var21", aVarOList.getString("EDUCATION"));
        vpd.put("var22", aVarOList.getString("SCHOOL"));
        vpd.put("var23", aVarOList.getString("MAJOR"));
        vpd.put("var24", aVarOList.getString("FTITLE"));
        vpd.put("var25", aVarOList.getString("CERTIFICATE"));
        vpd.put("var26", aVarOList.get("CONTRACTLENGTH").toString());
        vpd.put("var27", aVarOList.getString("CSTARTTIME"));
        vpd.put("var28", aVarOList.getString("CENDTIME"));
        vpd.put("var29", aVarOList.getString("ADDRESS"));
        vpd.put("var30", aVarOList.getString("USER_ID"));
        vpd.put("var31", aVarOList.getString("BZ"));
        return vpd;
    }

    /**
     * 构建excel表头
     *
     * @return 表头list
     */
    private List<String> getExcelTitles() {
        List<String> titles = Lists.newArrayList();
        titles.add("姓名");
        titles.add("英文");
        titles.add("编码");
        titles.add("部门");
        titles.add("职责");
        titles.add("电话");
        titles.add("邮箱");
        titles.add("性别");
        titles.add("出生日期");
        titles.add("民族");
        titles.add("岗位类别");
        titles.add("参加工作时间");
        titles.add("籍贯");
        titles.add("政治面貌");
        titles.add("入团时间");
        titles.add("身份证号");
        titles.add("婚姻状况");
        titles.add("进本单位时间");
        titles.add("现岗位");
        titles.add("上岗时间");
        titles.add("学历");
        titles.add("毕业学校");
        titles.add("专业");
        titles.add("职称");
        titles.add("职业资格证");
        titles.add("劳动合同时长");
        titles.add("签订日期");
        titles.add("终止日期");
        titles.add("现住址");
        titles.add("绑定账号ID");
        titles.add("备注");
        return titles;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
