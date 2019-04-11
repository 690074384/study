package com.lph.controller.system.createcode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * 类名称： 反向生成
 *
 * @author lvpenghui
 * @since 2019-4-9 20:28:48
 */
@Controller
@RequestMapping(value = "/recreateCode")
public class ReverseCreateCodeController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "recreateCode/list.do";

    /**
     * 列表
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/list")
    public ModelAndView list() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }    //校验权限
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("system/createcode/recreatecode_list");
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 列出所有表
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/listAllTable")
    @ResponseBody
    public Object listAllTable() {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        List<String> tblist = Lists.newArrayList();
        try {
            Object[] arrOb = DbFH.getTables(pd);
            tblist = (List<String>) arrOb[1];
            pd.put("msg", "ok");
        } catch (Exception e) {
            pd.put("msg", "no");
            logger.error("列表失败：", e);
        }
        pdList.add(pd);
        map.put("tblist", tblist);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 去代码生成器页面(进入弹窗)
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goProductCode")
    public ModelAndView goProductCode() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        String fieldType;
        StringBuilder sb = new StringBuilder();
        //读取字段信息
        List<Map<String, String>> columnList = DbFH.getFieldParameterLsit(DbFH.getFHCon(pd), pd.getString("table"));
        for (int i = 0; i < columnList.size(); i++) {
            Map<String, String> fmap = columnList.get(i);
            //字段名称
            sb.append(fmap.get("fieldNanme").toUpperCase());
            sb.append(",lph,");
            //字段类型
            fieldType = fmap.get("fieldType").toLowerCase();
            if (fieldType.contains("int")) {
                sb.append("Integer");
            } else if (fieldType.contains("NUMBER")) {
                if (Integer.parseInt(fmap.get("fieldSccle")) > 0) {
                    sb.append("Double");
                } else {
                    sb.append("Integer");
                }
            } else if (fieldType.contains("double") || fieldType.contains("numeric")) {
                sb.append("Double");
            } else if (fieldType.contains("date")) {
                sb.append("Date");
            } else {
                sb.append("String");
            }
            sb.append(",lph,");
            //备注
            sb.append("备注");
            sb.append(i + 1);
            sb.append(",lph,");
            //是否前台录入
            sb.append("是");
            sb.append(",lph,");
            //默认值
            sb.append("无");
            sb.append(",lph,");
            //长度
            sb.append(fmap.get("fieldLength"));
            sb.append(",lph,");
            //小数点右边的位数
            sb.append(fmap.get("fieldSccle"));
            sb.append("lvpenghui");
        }
        pd.put("FIELDLIST", sb.toString());
        mv.addObject("msg", "edit");
        mv.addObject("pd", pd);
        mv.setViewName("system/createcode/productCode");
        return mv;
    }

}