<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List, model.entity.ViewListDeleteDisplay, Debug.DebugLogger"%>

<%
DebugLogger.log("show_deleted_employee.jsp に到達");
    if (session.getAttribute("loginUserId") == null) {
        response.sendRedirect("login.jsp");
    } else {
        int currentPage = (Integer) session.getAttribute("currentPage");
        int totalPages = (Integer) session.getAttribute("totalPages");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員一覧</title>
<link rel="stylesheet" href="common/css/style.css">
<script type="text/javascript" src="common/JS/func.js"></script>
</head>
<body>

	<div class="header">
		<span class="big_title">S</span>tart <span class="big_title">U</span>p
		<span class="big_title">E</span>ducation
	</div>

	<div class="menu">
		<div class="main_frame">
			<p>従業員（削除済）一覧</p>
		</div>
	</div>
	
	<div class="filter_section">
    <form action="DisplayDeleteEmployeeList" method="get">
        <label for="lastName">苗字:</label> 
        <input type="text" id="lastName" name="lastName"
            value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>">

        <label for="gender">性別:</label> 
        <select id="gender" name="gender">
            <option value="">全て</option>
            <option value="0" <%= "0".equals(request.getParameter("gender")) ? "selected" : "" %>>男性</option>
            <option value="1" <%= "1".equals(request.getParameter("gender")) ? "selected" : "" %>>女性</option>
        </select>
        
        <label for="section">部署:</label> 
        <select id="section" name="section">
            <option value="">全て</option>
            <option value="総務部" <%= "総務部".equals(request.getParameter("section")) ? "selected" : "" %>>総務部</option>
            <option value="開発部" <%= "開発部".equals(request.getParameter("section")) ? "selected" : "" %>>開発部</option>
            <option value="営業部" <%= "営業部".equals(request.getParameter("section")) ? "selected" : "" %>>営業部</option>
        </select>

        <button type="submit" class="filter_button">フィルター</button>
        <!--<button type="submit" name="action" value="clear" class="clear_button">クリア</button>  -->
    </form>
</div>

	<div class="main_wrapper">
		<form name="form" action="CheckEditDelete" method="post"
			onsubmit="return chkShowAll('form')">

			<div class="show_all_table">
				<table class="border_table">
					<tr class="top_table">
						<td></td>
						<td>従業員コード</td>
						<td>氏名</td>
						<td>氏名かな</td>
						<td>性別</td>
						<td>生年月日</td>
						<td>所属部署</td>
						<td>入社日</td>
					</tr>

					<%
                        List<ViewListDeleteDisplay> vldlist = (List<ViewListDeleteDisplay>) 
                            session.getAttribute("deletevldlist");
                        for (ViewListDeleteDisplay vld : vldlist) {
                    %>
					<tr class="main_table">
						<!-- ラジオボタンからチェックボックスに変更 -->
						<td><input type="checkbox" name="employeeCode"
							value="<%=vld.getEmployeeCode()%>"></td>
						<td><%=vld.getEmployeeCode()%></td>
						<td><%=vld.getEmployeeName()%></td>
						<td><%=vld.getEmployeeKanaName()%></td>
						<td><%=vld.getGender()%></td>
						<td><%=vld.getBirthDay()%></td>
						<td><%=vld.getSectionName()%></td>
						<td><%=vld.getHireDate()%></td>
					</tr>
					<% } %>

				</table>
			</div>

			<div class="comment_show_all" id="comment_show_all">
				編集・削除したい従業員にチェックを入れてください</div>

			<div class="employee_button">
				<input type="submit" class="edit_button" name="submit"
					value="従業員(削)を編集する" onclick="setValue('edit_submit')"> <input
					type="submit" class="delete_button" name="submit"
					value="従業員(削)を復元する" onclick="setValue('delete_submit')"> <input
					type="hidden" name="chkBtn">
			</div>
		</form>

		<div class="link_main_button">
			<a href="menu.jsp">
				<button class="display_button">メニュー画面に戻る</button>
			</a>
		</div>
	</div>

	<div class="pagination">
		<% if (currentPage > 1) { %>
		<a href="DisplayDeleteEmployeeList?page=<%= currentPage - 1 %>">前のページ</a>
		<% } %>

		<% for (int i = 1; i <= totalPages; i++) { %>
		<% if (i == currentPage) { %>
		<strong><%= i %></strong>
		<% } else { %>
		<a href="DisplayDeleteEmployeeList?page=<%= i %>"><%= i %></a>
		<% } %>
		<% } %>

		<% if (currentPage < totalPages) { %>
		<a href="DisplayDeleteEmployeeList?page=<%= currentPage + 1 %>">次のページ</a>
		<% } %>
	</div>

	<div class="footer_top">
		<table class="table_format">
			<tr>
				<th>管理者情報</th>
			</tr>
			<tr>
				<td class="cel">会社名</td>
				<td>&nbsp;</td>
				<td>株式会社 Start Up Education</td>
			</tr>
			<tr>
				<td class="cel">Tell</td>
				<td>&nbsp;</td>
				<td>03-3333-3333</td>
			</tr>
			<tr>
				<td class="cel">Email</td>
				<td>&nbsp;</td>
				<td>startup_edu@freemail.com</td>
			</tr>
		</table>
	</div>

	<div class="footer_design">
		<footer>
			<small>© 2019 StartUpEducation.</small>
		</footer>
	</div>

</body>
</html>
<%
    }
%>

