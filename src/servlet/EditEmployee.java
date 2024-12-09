package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Debug.DebugLogger;
import model.dao.EmployeeDAO;
import model.entity.Employee;

/**
 * Servlet implementation class EditEmployee
 * データベースに接続して編集した複数の従業員情報を更新するクラス。
 */
@WebServlet("/EditEmployee")
public class EditEmployee extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loginUserId") == null) {
            response.sendRedirect("login.jsp");
        } else {
            response.sendRedirect("menu.jsp");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     * 
     * 複数の従業員情報をリクエストから取得し、DBに更新する処理を行う。
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	//デバックコード
    	DebugLogger.log("doGetメソッド開始");
    	
        // リクエストの文字エンコーディングをUTF-8に設定
        request.setCharacterEncoding("utf-8");

        // セッションから従業員リストを取得
        HttpSession session = request.getSession();
        List<Employee> employees = (List<Employee>) session.getAttribute("employee");

        // 更新する従業員情報のリスト
        List<Employee> updatedEmployees = new ArrayList<>();

        // DAOのインスタンス取得
        EmployeeDAO empdao = EmployeeDAO.getInstance();

        try {
            empdao.dbConnect();
            empdao.createSt();

            // 各従業員ごとに情報を取得し、更新
            for (Employee employee : employees) {
                String employeeCode = employee.getEmployeeCode();

                // 各従業員の情報をリクエストから取得して更新
                employee.setLastName(request.getParameter("lastName_" + employeeCode));
                DebugLogger.log(request.getParameter("lastName_" + employeeCode));
                
                employee.setFirstName(request.getParameter("firstName_" + employeeCode));
                DebugLogger.log(request.getParameter("firstName_" + employeeCode));
                
                employee.setLastKanaName(request.getParameter("lastKanaName_" + employeeCode));
                DebugLogger.log(request.getParameter("lastKanaName_" + employeeCode));
                
                employee.setFirstKanaName(request.getParameter("firstKanaName_" + employeeCode));
                DebugLogger.log(request.getParameter("firstKanaName_" + employeeCode));
                
                employee.setGender(Integer.parseInt(request.getParameter("gender_" + employeeCode)));
                employee.setBirthDay(request.getParameter("birthDay_" + employeeCode));
                employee.setHireDate(request.getParameter("hireDate_" + employeeCode));
                employee.setSectionCode(request.getParameter("section_code_" + employeeCode));
                employee.setDeletedFlag(Integer.parseInt(request.getParameter("deletedFlag_" + employeeCode)));

                // 更新処理を実行
                empdao.updateEmployee(employee);
                updatedEmployees.add(employee); // 更新した従業員をリストに追加
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            empdao.dbDiscon();
        }

        // セッションに更新済みの従業員情報を保存
        session.setAttribute("employee", updatedEmployees);

        // 編集完了画面にリダイレクト
        response.sendRedirect("edit_completion.jsp");
    }
}
