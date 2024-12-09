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
 * @author Kaori Masutani
 * データベースから従業員を削除するクラス。
 */
@WebServlet("/RestoreEmployee")
public class RestoreEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントが Servlet へ要求したリクエスト内容を含む HttpServletRequest オブジェクト。
	 * @param response Servlet がクライアントに返すレスポンス内容を含む HttpServletResponse オブジェクト。
	 * @throws ServletException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * @throws IOException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * Servlet に GET リクエストを処理可能にさせるため、(service メソッドを通じて) サーバによって呼び出される。<br>
	 * データベースに接続して対応する従業員を削除する。
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {

	    // デバッグログ：メソッド開始
	    DebugLogger.log("doGet メソッド開始");

	    // セッション取得とログインチェック
	    HttpSession session = request.getSession();
	    if (session.getAttribute("loginUserId") == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }

	    // リクエストの文字エンコーディングを設定
	    request.setCharacterEncoding("utf-8");

	    // セッションから従業員コードを取得
	    String[] selectedEmployeeCodes = (String[]) session.getAttribute("selectedEmployeeCodes");
	    
	    // デバッグログ：要素数確認
	    DebugLogger.log("選択した数（復元）-----" +  getArrayLength(selectedEmployeeCodes));

	    // EmployeeDAOインスタンス生成
	    EmployeeDAO dao = EmployeeDAO.getInstance();

	    // 従業員情報の復元処理
	    int count = 0;
	    try {
	        dao.dbConnect();       // DB接続
	        dao.createSt();        // ステートメント生成
	        count = dao.restoreEmployee(selectedEmployeeCodes);  // 従業員の復元
	        session.setAttribute("COUNT", count);       // 復元件数をセッションに保存
	    } catch (SQLException e) {
	        e.printStackTrace();   // 例外のログ出力
	    } finally {
	        dao.dbDiscon();        // DB切断
	    }

	    // 処理結果による画面遷移
	    if (count == 0) {
	        response.sendRedirect("delete_error_employee.jsp");
	    } else {
	        response.sendRedirect("delete_completion.jsp");
	    }
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントが Servlet へ要求したリクエスト内容を含む HttpServletRequest オブジェクト。
	 * @param response Servlet がクライアントに返すレスポンス内容を含む HttpServletResponse オブジェクト。
	 * @throws ServletException Servlet が POST リクエストを処理している間に入出力エラーが発生した場合。
	 * @throws IOException POST に相当するリクエストが処理できない場合。
	 * Servlet に POST リクエストを処理可能にさせるため、(service メソッド経由で) サーバによって呼び出される。
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
	/**
	 * 配列の要素数を取得するメソッド。
	 * @param array セッションから取得したString配列
	 * @return 配列の要素数。配列がnullの場合は0を返す。
	 */
	public int getArrayLength(String[] array) {
	    if (array == null) {
	        return 0;  // 配列がnullなら0を返す
	    }
	    return array.length;  // 配列の要素数を返す
	}
}


