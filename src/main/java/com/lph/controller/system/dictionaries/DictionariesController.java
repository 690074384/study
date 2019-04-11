package com.lph.controller.system.dictionaries;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.dictionaries.DictionariesManager;
import com.lph.util.AppUtil;
import com.lph.util.Constants;
import com.lph.util.Jurisdiction;
import com.lph.util.PageData;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * 说明：数据字典
 *
 * @author lvpenghui
 * @since 2019-4-9 20:36:48
 */
@Controller
@RequestMapping(value = "/dictionaries")
public class DictionariesController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "dictionaries/list.do";
    @Resource(name = "dictionariesService")
    private DictionariesManager dictionariesService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增Dictionaries");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //主键
        pd.put("DICTIONARIES_ID", this.get32UUID());
        dictionariesService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param dictionariesId 主键
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String dictionariesId) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "删除Dictionaries");
        Map<String, String> map = Maps.newHashMap();
        PageData pd = new PageData();
        pd.put("DICTIONARIES_ID", dictionariesId);
        String errInfo = "success";
        //判断是否有子级，是：不允许删除
        if (dictionariesService.listSubDictByParentId(dictionariesId).size() > 0) {
            errInfo = "false";
        } else {
            //根据ID读取
            pd = dictionariesService.findById(pd);
            if (StringUtils.isNotEmpty(pd.getString(Constants.TBSNAME))) {
                String[] table = pd.getString("TBSNAME").split(Constants.COMMA);
                for (String aTable : table) {
                    pd.put("thisTable", aTable);
                    try {
                        //判断是否被占用，是：不允许删除(去排查表检查字典表中的编码字段)
                        if (Integer.parseInt(dictionariesService.findFromTbs(pd).get("zs").toString()) > 0) {
                            errInfo = "false";
                            break;
                        }
                    } catch (Exception e) {
                        errInfo = "false2";
                        break;
                    }
                }
            }
        }
        if (Constants.SUCCESS.equals(errInfo)) {
            //执行删除
            dictionariesService.delete(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "修改Dictionaries");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        dictionariesService.edit(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "列表Dictionaries");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        String dictionariesId = null == pd.get("DICTIONARIES_ID") ? "" : pd.get("DICTIONARIES_ID").toString();
        if (StringUtils.isNotEmpty(String.valueOf(pd.get(Constants.ID)))) {
            dictionariesId = pd.get("id").toString();
        }
        //上级ID
        pd.put("DICTIONARIES_ID", dictionariesId);
        page.setPd(pd);
        //列出Dictionaries列表
        List<PageData> varList = dictionariesService.list(page);
        //传入上级所有信息
        mv.addObject("pd", dictionariesService.findById(pd));
        //上级ID
        mv.addObject("DICTIONARIES_ID", dictionariesId);
        mv.setViewName("system/dictionaries/dictionaries_list");
        mv.addObject("varList", varList);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 显示列表ztree
     *
     * @param model          Model对象
     * @param dictionariesId 主键
     */
    @RequestMapping(value = "/listAllDict")
    public ModelAndView listAllDict(Model model, String dictionariesId) {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            JSONArray arr = JSONArray.fromObject(dictionariesService.listAllDict("0"));
            String json = arr.toString();
            json = json
                    .replaceAll("DICTIONARIES_ID", "id")
                    .replaceAll("PARENT_ID", "pId")
                    .replaceAll("NAME", "name")
                    .replaceAll("subDict", "nodes")
                    .replaceAll("hasDict", "checked")
                    .replaceAll("treeurl", "url");
            model.addAttribute("zTreeNodes", json);
            mv.addObject("DICTIONARIES_ID", dictionariesId);
            mv.addObject("pd", pd);
            mv.setViewName("system/dictionaries/dictionaries_ztree");
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
        String dictionariesId = null == pd.get("DICTIONARIES_ID") ? "" : pd.get("DICTIONARIES_ID").toString();
        //上级ID
        pd.put("DICTIONARIES_ID", dictionariesId);
        //传入上级所有信息
        mv.addObject("pds", dictionariesService.findById(pd));
        //传入ID，作为子级ID用
        mv.addObject("DICTIONARIES_ID", dictionariesId);
        mv.setViewName("system/dictionaries/dictionaries_edit");
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
        String dictionariesId = pd.getString("DICTIONARIES_ID");
        //根据ID读取
        pd = dictionariesService.findById(pd);
        //放入视图容器
        mv.addObject("pd", pd);
        //用作上级信息
        pd.put("DICTIONARIES_ID", pd.get("PARENT_ID").toString());
        //传入上级所有信息
        mv.addObject("pds", dictionariesService.findById(pd));
        //传入上级ID，作为子ID用
        mv.addObject("DICTIONARIES_ID", pd.get("PARENT_ID").toString());
        //复原本ID
        pd.put("DICTIONARIES_ID", dictionariesId);
        mv.setViewName("system/dictionaries/dictionaries_edit");
        mv.addObject("msg", "edit");
        return mv;
    }

    /**
     * 判断编码是否存在
     *
     * @return 判断编码是否存在
     */
    @RequestMapping(value = "/hasBianma")
    @ResponseBody
    public Object hasBianma() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd;
        try {
            pd = this.getPageData();
            if (dictionariesService.findByBianma(pd) != null) {
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
