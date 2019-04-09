package com.lph.controller.fhoa.datajur;

import com.google.common.collect.Lists;
import com.lph.controller.base.BaseController;
import com.lph.service.fhoa.datajur.DatajurManager;
import com.lph.service.fhoa.department.DepartmentManager;
import com.lph.util.Constants;
import com.lph.util.Jurisdiction;
import com.lph.util.PageData;
import net.sf.json.JSONArray;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明：组织数据权限表
 *
 * @author lvpenghui
 * @since 2019-4-8 21:45:23
 */
@Controller
@RequestMapping(value = "/datajur")
public class DatajurController extends BaseController {

    @Resource(name = "datajurService")
    private DatajurManager datajurService;
    @Resource(name = "departmentService")
    private DepartmentManager departmentService;

    /**
     * 修改
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改Datajur");

        // 菜单地址(权限用)

        String menuUrl = "datajur/list.do";
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //部门ID集
        pd.put("DEPARTMENT_IDS", departmentService.getDEPARTMENT_IDS(pd.getString("DEPARTMENT_ID")));
        datajurService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
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
        pd = datajurService.findById(pd);
        mv.addObject("DATAJUR_ID", pd.getString("DATAJUR_ID"));
        //读取部门数据(用部门名称)
        pd = departmentService.findById(pd);
        mv.setViewName("fhoa/datajur/datajur_edit");
        mv.addObject("msg", "edit");
        mv.addObject("pd", pd);
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
