<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="common/tag.jsp" %>    
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <title>秒杀列表页</title>
    <%@ include file="common/head.jsp" %>
  </head>
  <body>
  
  	<!-- 页面start -->
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading text-center">
				<h2>秒杀列表</h2>
			</div>
			<div class="panel-body">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>名称</th>
							<th>库存</th>
							<th>开始时间</th>
							<th>结束时间</th>
							<th>创建时间</th>
							<th>详情页</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${seckillList }" var="seckill">
							<tr>
								<td>${seckill.name }</td>
								<td>${seckill.number }</td>
								<td>
									<fmt:formatDate value="${seckill.startTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
								<td>
									<fmt:formatDate value="${seckill.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
								<td>
									<fmt:formatDate value="${seckill.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
								<td>
									<a class="btn btn-info" href="/seckill/${seckill.seckillId }/detail" target="_blank">详情</a>
								</td>
							</tr>							
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<!-- 页面end -->
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  </body>
</html>