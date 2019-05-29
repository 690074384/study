package com.lph.util;

import org.springframework.context.ApplicationContext;

/**
 * 系统静态常量
 *
 * @author lvpenghui
 * @since 2019-4-8 20:02:00
 */
public class Constants {

    public static final String NO_ORDER = "no_order";
    public static final String BAR = "|";
    public static final int HTTP_OK = 200;
    public static final String USERNAME = "USERNAME";
    public static final String FKEY = "FKEY";
    public static final String ERRER = "errer";
    public static final String SUCCESS = "success";
    public static final String YES = "yes";
    public static final String OK = "ok";
    public static final String ID = "id";
    public static final String ZS = "zs";
    public static final String HTTP = "http://";
    public static final String PASSWORD = "PASSWORD";
    public static final String ROLE_ID = "ROLE_ID";
    public static final String TREE = "tree";
    public static final String FATHER_TABLE = "fathertable";
    public static final String SON_TABLE = "sontable";
    public static final String CODE = "code";
    public static final String TBSNAME = "TBSNAME";
    public static final String TYPE = "TYPE";
    public static final String STATUS = "STATUS";
    public static final String ADMIN = "admin";
    public static final String CHANGE_MENU = "changeMenu";
    public static final String HTTP_FORWARD = "x-forwarded-for";
    public static final String USER_ID = "USER_ID";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String SUBSCRIBE = "subscribe";
    public static final String MYSQL = "mysql";
    static final String CALL_BACK = "callback";
    private final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    public static final String EMPTY = "";
    public static final String COMMA = ",";
    public static final String POINT = ".";
    public static final String WELL = "#";

    public static final String ADDD = "add";
    public static final String SAVE = "save";
    public static final String DELE = "del";
    public static final String EDIT = "edit";
    public static final String SRCH = "cha";


    public static final String ADDD_QX = "add_qx";
    public static final String DELE_QX = "del_qx";
    public static final String EDIT_QX = "edit_qx";
    public static final String SRCH_QX = "cha_qx";

    public static final String ZERO_STRING = "0";
    public static final String ONE_STRING = "1";
    public static final String TWO_STRING = "2";

    public static final String WU_QUAN = "无权";

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;


    public static final String GET_APP_USER_BY_NAME = "getAppuserByUsername";
    public static final String REGISTER_SYS_USER = "registerSysUser";

    public static final String ZIP = "zip";

    public static final String SESSION_SECURITY_CODE = "sessionSecCode";
    public static final String SESSION_USER = "sessionUser";
    public static final String SESSION_ROLE_RIGHTS = "sessionRoleRights";
    public static final String sSESSION_ROLE_RIGHTS = "sessionRoleRights";
    public static final String SESSION_menuList = "menuList";
    public static final String SESSION_allmenuList = "allmenuList";
    public static final String SESSION_QX = "QX";
    public static final String SESSION_USERPDS = "userpds";
    public static final String SESSION_USERROL = "USERROL";
    public static final String SESSION_USERNAME = "USERNAME";
    public static final String DEPARTMENT_IDS = "DEPARTMENT_IDS";
    public static final String DEPARTMENT_ID = "DEPARTMENT_ID";
    public static final String TRUE = "T";
    public static final String FALSE = "F";
    public static final String LOGIN = "/login_toLogin.do";
    public static final String SYSNAME = "admin/config/SYSNAME.txt";
    public static final String PAGE = "admin/config/PAGE.txt";
    public static final String EMAIL = "admin/config/EMAIL.txt";
    public static final String SMS1 = "admin/config/SMS1.txt";
    public static final String SMS2 = "admin/config/SMS2.txt";
    public static final String FWATERM = "admin/config/FWATERM.txt";
    public static final String IWATERM = "admin/config/IWATERM.txt";
    public static final String WEIXIN = "admin/config/WEIXIN.txt";
    public static final String WEBSOCKET = "admin/config/WEBSOCKET.txt";
    public static final String LOGINEDIT = "admin/config/LOGIN.txt";
    public static final String FILEPATHIMG = "uploadFiles/uploadImgs/";
    public static final String FILEPATHFILE = "uploadFiles/file/";
    public static final String FILEPATHFILEOA = "uploadFiles/uploadFile/";
    public static final String FILEPATHTWODIMENSIONCODE = "uploadFiles/twoDimensionCode/";
    public static final String NO_INTERCEPTOR_PATH = ".*/((login)|(logout)|(code)|(app)|(weixin)|(static)|(main)|(websocket)).*";
    public static ApplicationContext WEB_APP_CONTEXT = null;

    /**
     * 系统用户注册接口_请求协议参数)
     */
    static final String[] SYSUSER_REGISTERED_PARAM_ARRAY = new String[]{"USERNAME", "PASSWORD", "NAME", "EMAIL", "rcode"};
    static final String[] SYSUSER_REGISTERED_VALUE_ARRAY = new String[]{"用户名", "密码", "姓名", "邮箱", "验证码"};

    /**
     * app根据用户名获取会员信息接口_请求协议中的参数
     */
    static final String[] APP_GETAPPUSER_PARAM_ARRAY = new String[]{"USERNAME"};
    static final String[] APP_GETAPPUSER_VALUE_ARRAY = new String[]{"用户名"};

}
