<%@page import="model.entity.Employee"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	if (session.getAttribute("loginUserId") == null) {
		response.sendRedirect("login.jsp");
	} else {
		
		if (true) {
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>編集完了</title>
<link rel="stylesheet" href="common/css/style.css">
</head>
<body>
	<div class="header">
		<span class="big_title">S</span>tart <span class="big_title">U</span>p
		<span class="big_title">E</span>ducation
	</div>

	<div class="menu">
		<div class="main_frame">
			<p>
				編集完了しました
			</p>
		</div>
	</div>

	<div class="main_admin">
		<p>引き続き入力を行う場合はメニュー画面まで戻ってください。</p>
		<div class="a_logout_button">
			<a href="menu.jsp">
				<button class="display_button">メニュー画面に戻る</button>
			</a>
		</div>
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
	} else {
			response.sendRedirect("menu.jsp");

		}
	}
%>