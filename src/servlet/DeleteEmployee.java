package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Debug.DebugLogger;
import model.dao.EmployeeDAO;

/**
 * Servlet implementation class EmployeeDelete
 * データベースから従業員を削除するサーブレットクラス
 * @author Kaori Masutani
 */
@WebServlet("/DeleteEmployee")
public class DeleteEmployee extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * GETリクエストを処理し、選択された従業員を削除する。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        DebugLogger.log("doGet メソッド開始");
        HttpSession session = request.getSession();

        // セッションからログイン情報を確認
        if (session.getAttribute("loginUserId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // リクエストの文字エンコーディングを設定
            request.setCharacterEncoding("utf-8");

            // リクエストから選択された従業員コードを取得
            String[] selectedEmployeeCodes = (String[]) session.getAttribute("selectedEmployeeCodes");

            // デバッグログ：従業員コードが取得できない場合
            if (selectedEmployeeCodes == null) {
                DebugLogger.log("従業員コードが null です");
            }

            // データベース処理：従業員の削除
            int count = deleteEmployeesFromDB(selectedEmployeeCodes, session);

            // 処理結果に応じた画面遷移
            if (count == 0) {
                response.sendRedirect("delete_error_employee.jsp");
            } else {
                response.sendRedirect("delete_completion.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");  // エラー画面へ遷移
        }
    }

    /**
     * POSTリクエストをGETメソッドに移譲する。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        DebugLogger.log("doPost メソッド開始 - doGetへ変異します");
        doGet(request, response);
    }

    /**
     * データベースから従業員を削除する処理を行う。
     * @param employeeCodes 削除対象の従業員コード配列
     * @param session 現在のセッションオブジェクト
     * @return 削除された件数
     * @throws SQLException SQL例外が発生した場合
     */
    private int deleteEmployeesFromDB(String[] employeeCodes, HttpSession session) 
            throws SQLException {
        
        int count = 0;
        EmployeeDAO dao = EmployeeDAO.getInstance();

        try {
            dao.dbConnect();
            dao.createSt();
            count = dao.deleteEmployee(employeeCodes);
            session.setAttribute("COUNT", count);  // 削除件数をセッションに保存
        } finally {
            dao.dbDiscon();  // データベース接続を切断
        }

        return count;
    }
}
