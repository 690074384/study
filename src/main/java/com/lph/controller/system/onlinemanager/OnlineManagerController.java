package com.lph.controller.system.onlinemanager;

import com.lph.controller.base.BaseController;
import com.lph.util.Constants;
import com.lph.util.Jurisdiction;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类名称：在线管理列表
 *
 * @author lvpenghui
 * @since 2019-4-10 22:08:57
 */
@Controller
@RequestMapping(value = "/onlinemanager")
public class OnlineManagerController extends BaseController {

    /**
     * 列表
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/list")
    public ModelAndView list() {
        logBefore(logger, "列表OnlineManager");
        String menuUrl = "onlinemanager/list.do";
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("system/onlinemanager/onlinemanager_list");
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
