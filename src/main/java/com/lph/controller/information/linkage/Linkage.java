package com.lph.controller.information.linkage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.system.Dictionaries;
import com.lph.service.system.dictionaries.DictionariesManager;
import com.lph.util.AppUtil;
import com.lph.util.PageData;
import com.lph.util.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 说明：明细表
 *
 * @author lvpenghui
 * @since 2019-4-9 17:14:55
 */
@Controller
@RequestMapping(value = "/linkage")
public class Linkage extends BaseController {

    @Resource(name = "dictionariesService")
    private DictionariesManager dictionariesService;

    /**
     * 去新增页面
     */
    @RequestMapping(value = "/view")
    public ModelAndView goAdd() {
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("information/linkage/view");
        return mv;
    }

    /**
     * 获取连级数据
     *
     * @return 连级数据
     */
    @RequestMapping(value = "/getLevels")
    @ResponseBody
    public Object getLevels() {
        Map<String, Object> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd;
        try {
            pd = this.getPageData();
            String dictionariesIds = pd.getString("DICTIONARIES_ID");
            dictionariesIds = Tools.isEmpty(dictionariesIds) ? "0" : dictionariesIds;
            //用传过来的ID获取此ID下的子列表数据
            List<Dictionaries> varList = dictionariesService.listSubDictByParentId(dictionariesIds);
            List<PageData> pdList = Lists.newArrayList();
            for (Dictionaries d : varList) {
                PageData pdf = new PageData();
                pdf.put("DICTIONARIES_ID", d.getDICTIONARIES_ID());
                pdf.put("NAME", d.getNAME());
                pdList.add(pdf);
            }
            map.put("list", pdList);
        } catch (Exception e) {
            errInfo = "error";
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

}
