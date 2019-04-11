package com.lph.controller.system.loginimg;

import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.loginimg.LogInImgManager;
import com.lph.util.DelAllFile;
import com.lph.util.Jurisdiction;
import com.lph.util.PageData;
import com.lph.util.PathUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 说明：登录页面背景图片
 *
 * @author lvpenghui
 * @since 2019-4-10 21:38:02
 */
@Controller
@RequestMapping(value = "/loginimg")
public class LogInImgController extends BaseController {

    @Resource(name = "loginimgService")
    private LogInImgManager loginimgService;

    /**
     * 保存
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增LogInImg");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //主键
        pd.put("LOGINIMG_ID", this.get32UUID());
        loginimgService.save(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "删除LogInImg");
        PageData pd = this.getPageData();
        pd = loginimgService.findById(pd);
        loginimgService.delete(pd);
        //删除文件
        DelAllFile.delFolder(PathUtil.getClasspath() + "static/login/images/" + pd.getString("FILEPATH"));
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
        logBefore(logger, Jurisdiction.getUsername() + "修改LogInImg");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        loginimgService.edit(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "列表LogInImg");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        page.setPd(pd);
        //列出LogInImg列表
        List<PageData> varList = loginimgService.list(page);
        mv.setViewName("system/loginimg/loginimg_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去新增页面
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/loginimg/loginimg_edit");
        mv.addObject("msg", "save");
        mv.addObject("pd", pd);
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
        //根据ID读取
        pd = loginimgService.findById(pd);
        mv.setViewName("system/loginimg/loginimg_edit");
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
