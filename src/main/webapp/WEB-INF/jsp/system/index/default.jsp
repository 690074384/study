<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=basePath%>">

    <!-- jsp文件头和头部 -->
    <%@ include file="../index/top.jsp" %>
    <!-- 百度echarts -->
    <script src="plugins/echarts/echarts.min.js"></script>
</head>
<body class="no-skin">

<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
    <!-- /section:basics/sidebar -->
    <div class="main-content">
        <div class="main-content-inner">
            <div class="page-content">
                <div class="hr hr-18 dotted hr-double"></div>
                <div class="row">
                    <div class="col-xs-12">

                        <div class="alert alert-block alert-success">
                            <button type="button" class="close" data-dismiss="alert">
                                <i class="ace-icon fa fa-times"></i>
                            </button>
                            <i class="ace-icon fa fa-check green"></i>

                            币种：<input type="text" id="coinType"/>
                            低价：<input type="text" id="coinMin"/>
                            高价：<input type="text" id="coinMax"/>
                            <a class="btn btn-mini btn-primary" onclick="trade();">交易</a>
                        </div>
                        <div class="alert alert-block alert-success">
                            币种：<input type="text" id="revokeCoinType"/>
                            <a class="btn btn-mini btn-primary" onclick="revoke();">撤单</a>
                        </div>

                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->
            </div>
            <!-- /.page-content -->
        </div>
    </div>
    <!-- /.main-content -->


    <!-- 返回顶部 -->
    <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
        <i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
    </a>

</div>
<!-- /.main-container -->


<!-- basic scripts -->
<!-- 页面底部js¨ -->
<%@ include file="../index/foot.jsp" %>
<!-- ace scripts -->
<script src="static/ace/js/ace/ace.js"></script>
<!-- inline scripts related to this page -->
<script type="text/javascript">
    $(top.hangge());
</script>
<script type="text/javascript" src="static/ace/js/jquery.js"></script>
<script type="text/javascript">
    function trade() {
        var coinType = $("#coinType").val();
        var coinMin = $("#coinMin").val();
        var coinMax = $("#coinMax").val();
        if (coinMin >= coinMax) {
            alert("fuck");
            return;
        }
        $.ajax({
            type: "POST",
            url: '<%=basePath%>happuser/trade.do',
            data: {
                coinType: coinType,
                coinMin: coinMin,
                coinMax: coinMax,
                tm: new Date().getTime()
            },
            dataType: 'json',
            cache: false,
            success: function (data) {
            }
        });
    }
</script>
<script type="text/javascript">
    function revoke() {
        var coinType = $("#revokeCoinType").val();
        $.ajax({
            type: "POST",
            url: '<%=basePath%>happuser/revoke.do',
            data: {
                coinType: coinType,
                tm: new Date().getTime()
            },
            dataType: 'json',
            cache: false,
            success: function (data) {
            }
        });
    }
</script>
</body>
</html>