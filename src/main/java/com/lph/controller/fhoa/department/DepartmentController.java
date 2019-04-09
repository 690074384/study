package com.lph.controller.fhoa.department;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.fhoa.department.DepartmentManager;
import com.lph.util.AppUtil;
import com.lph.util.Constants;
import com.lph.util.Jurisdiction;
import com.lph.util.PageData;
import net.sf.json.JSONArray;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：组织机构
 *
 * @author lvpenghui
 * @since 2019-4-8 21:48:51
 */
@Controller
@RequestMapping(value = "/department")
public class DepartmentController extends BaseController {
    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "department/list.do";
    @Resource(name = "departmentService")
    private DepartmentManager departmentService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增department");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("DEPARTMENT_ID", this.get32UUID());
        departmentService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param departmentId 部门编号
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String departmentId) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "删除department");
        Map<String, String> map = Maps.newHashMap();
        PageData pd = new PageData();
        pd.put("DEPARTMENT_ID", departmentId);
        String errInfo = "success";
        //判断是否有子级，是：不允许删除
        if (departmentService.listSubDepartmentByParentId(departmentId).size() > 0) {
            errInfo = "false";
        } else {
            departmentService.delete(pd);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 修改
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改department");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        departmentService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page 分页
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表department");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        String departmentId = null == pd.get("DEPARTMENT_ID") ? "" : pd.get("DEPARTMENT_ID").toString();
        if (null != pd.get(Constants.ID) && !"".equals(pd.get(Constants.ID).toString())) {
            departmentId = pd.get("id").toString();
        }
        //上级ID
        pd.put("DEPARTMENT_ID", departmentId);
        page.setPd(pd);
        //列出Dictionaries列表
        List<PageData> varList = departmentService.list(page);
        //传入上级所有信息
        mv.addObject("pd", departmentService.findById(pd));
        //上级ID
        mv.addObject("DEPARTMENT_ID", departmentId);
        mv.setViewName("fhoa/department/department_list");
        mv.addObject("varList", varList);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 显示列表ztree
     *
     * @param model        model对象
     * @param departmentId 部门ID
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/listAllDepartment")
    public ModelAndView listAllDepartment(Model model, String departmentId) {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            JSONArray arr = JSONArray.fromObject(departmentService.listAllDepartment("0"));
            String json = arr.toString();
            json = json
                    .replaceAll("DEPARTMENT_ID", "id")
                    .replaceAll("PARENT_ID", "pId")
                    .replaceAll("NAME", "name")
                    .replaceAll("subDepartment", "nodes")
                    .replaceAll("hasDepartment", "checked")
                    .replaceAll("treeurl", "url");
            model.addAttribute("zTreeNodes", json);
            mv.addObject("DEPARTMENT_ID", departmentId);
            mv.addObject("pd", pd);
            mv.setViewName("fhoa/department/department_ztree");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
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
        String departmentId = null == pd.get("DEPARTMENT_ID") ? "" : pd.get("DEPARTMENT_ID").toString();
        //上级ID
        pd.put("DEPARTMENT_ID", departmentId);
        //传入上级所有信息
        mv.addObject("pds", departmentService.findById(pd));
        //传入ID，作为子级ID用
        mv.addObject("DEPARTMENT_ID", departmentId);
        mv.setViewName("fhoa/department/department_edit");
        mv.addObject("msg", "save");
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
        String departmentId = pd.getString("DEPARTMENT_ID");
        //根据ID读取
        pd = departmentService.findById(pd);
        //放入视图容器
        mv.addObject("pd", pd);
        //用作上级信息
        pd.put("DEPARTMENT_ID", pd.get("PARENT_ID").toString());
        //传入上级所有信息
        mv.addObject("pds", departmentService.findById(pd));
        //传入上级ID，作为子ID用
        mv.addObject("DEPARTMENT_ID", pd.get("PARENT_ID").toString());
        //复原本ID
        pd.put("DEPARTMENT_ID", departmentId);
        mv.setViewName("fhoa/department/department_edit");
        mv.addObject("msg", "edit");
        return mv;
    }

    /**
     * 判断编码是否存在
     *
     * @return 编码对象
     */
    @RequestMapping(value = "/hasBianma")
    @ResponseBody
    public Object hasBianma() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd;
        try {
            pd = this.getPageData();
            if (!CollectionUtils.isEmpty(departmentService.findByBianma(pd))) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
