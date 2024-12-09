<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // サンプルデータ
    String[][] employees = {
        {"E001", "田中aaa", "aaa", "男性", "1990-01-01", "総務部", "2015-04-01"},
        {"E002", "aaaaa", "bbb", "女性", "1992-05-12", "総務部", "2018-06-15"},
        {"E005", "いいいいい", "aaaa", "男性", "1985-12-23", "開発部", "2010-03-01"},
        {"E006", "苗字6名前6", "ミョウジ6", "女性", "1992-04-15", "総務部", "2015-05-12"},
        {"E007", "苗字7名前7", "ミョウジ7", "男性", "1987-10-10", "総務部", "2009-08-19"}
    };
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員一覧</title>
<link rel="stylesheet" href="common/css/style.css">
</head>
<body>
    <h1>従業員一覧</h1>

    <!-- フィルターセクション -->
    <div align="center" >
        <form method="get" action="employeeList.jsp">
            <label for="department">部署:</label>
            <select name="department" id="department">
                <option value="">すべて</option>
                <option value="総務部">総務部</option>
                <option value="開発部">開発部</option>
            </select>

            <label for="gender">性別:</label>
            <select name="gender" id="gender">
                <option value="">すべて</option>
                <option value="男性">男性</option>
                <option value="女性">女性</option>
            </select>

            <label for="hire_date_start">入社日（開始）:</label>
            <input type="date" name="hire_date_start" id="hire_date_start">

            <label for="hire_date_end">入社日（終了）:</label>
            <input type="date" name="hire_date_end" id="hire_date_end">

            <input type="submit" value="フィルター">
            <input type="reset" value="リセット">
        </form>
    </div>

    <!-- 従業員テーブル -->
    <table border="1">
        <tr>
            <th>従業員コード</th>
            <th>氏名</th>
            <th>氏名かな</th>
            <th>性別</th>
            <th>生年月日</th>
            <th>所属部署</th>
            <th>入社日</th>
        </tr>
        <%
            // フィルター条件の取得
            String department = request.getParameter("department");
            String gender = request.getParameter("gender");
            String hireDateStart = request.getParameter("hire_date_start");
            String hireDateEnd = request.getParameter("hire_date_end");

            // フィルターを適用して従業員を表示
            for (String[] employee : employees) {
                boolean display = true;

                // 部署フィルター
                if (department != null && !department.isEmpty() && !employee[5].equals(department)) {
                    display = false;
                }

                // 性別フィルター
                if (gender != null && !gender.isEmpty() && !employee[3].equals(gender)) {
                    display = false;
                }

                // 入社日フィルター
                if (hireDateStart != null && !hireDateStart.isEmpty() && employee[6].compareTo(hireDateStart) < 0) {
                    display = false;
                }
                if (hireDateEnd != null && !hireDateEnd.isEmpty() && employee[6].compareTo(hireDateEnd) > 0) {
                    display = false;
                }

                if (display) {
                    %>
                    <tr>
                        <td><%= employee[0] %></td>
                        <td><%= employee[1] %></td>
                        <td><%= employee[2] %></td>
                        <td><%= employee[3] %></td>
                        <td><%= employee[4] %></td>
                        <td><%= employee[5] %></td>
                        <td><%= employee[6] %></td>
                    </tr>
                    <%
                }
            }
        %>
    </table>
</body>
</html>