<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="common/tag.jsp" %>     
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <title>秒杀详情页</title>
    <%@ include file="common/head.jsp" %>
  </head>
  <body>

	<div class="container">
		<div class="panel panel-default text-center">
			<div class="panel-heading">
				<h1>${seckill.name }</h1>
			</div>
			<div class="panel panel-body">
				<h2 class="text-danger">
					<!-- 显示time图标 -->
                    <span class="glyphicon glyphicon-time"></span>
                    <!-- 显示倒计时 -->
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>
			</div>
		</div>
	</div>
	<div id="killPhoneModal" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title text-center">
                            <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                        </h3>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-xs-8 col-xs-offset-2">
                                <input type="text" name="killphone" id="killphoneKey" placeholder="填手机号^O^" class="form-control"/>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <span id="killphoneMessage" class="glyphicon"></span>
                        <button type="button" id="killPhoneBtn" class="btn btn-success">
                            <span class="glyphicon glyphicon-phone"></span>
                            Submit
                        </button>
                    </div>
                </div>
            </div>
        </div>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/seckill.js"></script>
    <script type="text/javascript">
    	$(function(){
    		seckill.detail.init({
    			seckillId: '${seckill.seckillId}',
    			startTime: '${seckill.startTime.time}',//毫秒
    			endTime: '${seckill.endTime.time}',//毫秒
    		});
    	})
    </script>
  </body>
</html>