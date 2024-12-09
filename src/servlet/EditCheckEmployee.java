package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
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
import model.entity.Section;

/**
 * Servlet implementation class EmployeeEdit
 * @author Yoshiyuki Tonami
 * データベースに接続して従業員コードに対応する従業員情報を送る。
 */
@WebServlet("/EditCheckEmployee")
public class EditCheckEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントからのリクエストを含む HttpServletRequest オブジェクト。
	 * @param response クライアントへのレスポンスを含む HttpServletResponse オブジェクト。
	 * @throws ServletException GET リクエスト処理中に発生する ServletException。
	 * @throws IOException GET リクエスト処理中に発生する IO エラー。
	 * 
	 * 従業員コードに対応する従業員情報を取得してセッションにセットする。
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    // デバッグログ：メソッド開始
	    DebugLogger.log("doGet メソッド開始");

	    // セッション取得とログインチェック
	    HttpSession session = request.getSession();
	    if (session.getAttribute("loginUserId") == null) {
	        response.sendRedirect("login.jsp");
	        return; // 処理中断
	    }

	    // リクエストの文字エンコーディングをUTF-8に設定
	    request.setCharacterEncoding("UTF-8");

	    // セッションから選択された従業員コードの配列を取得
	    String[] selectedEmployeeCodes = (String[]) session.getAttribute("selectedEmployeeCodes");

	    // デバッグ用：選択された従業員コードの数を表示
	    DebugLogger.log("選択した数（編集）-----" + getArrayLength(selectedEmployeeCodes));

	    // DAOインスタンスを取得
	    EmployeeDAO empdao = EmployeeDAO.getInstance();
	    List<Employee> employees = null;
	    List<Section> sections = new LinkedList<>();

	    if (selectedEmployeeCodes != null) {
	        try {
	            // データベース接続とステートメント作成
	            empdao.dbConnect();
	            empdao.createSt();

	            // 選択された従業員情報とセクション情報を取得
	            employees = empdao.selectEmployee(selectedEmployeeCodes);
	            sections = empdao.getSection();

	        } catch (SQLException e) {
	            // 例外発生時のエラーログ出力
	            e.printStackTrace();
	        } finally {
	            // データベース接続を解放
	            empdao.dbDiscon();
	        }

	        // セッションに従業員情報とセクション情報を保存
	        session.setAttribute("employee", employees);
	        session.setAttribute("sections", sections);
	        
	        DebugLogger.log("取得したカラム数-----" + getEmployeeListSize(employees));
	        Integer count = getEmployeeListSize(employees);
	        session.setAttribute("COUNT", count);
	        
	        
	        // データ取得結果に応じた画面遷移
	        if (getEmployeeListSize(employees) != 0) {
	        	DebugLogger.log("正常に到達　フラグ");
	        	response.sendRedirect("edit_employee.jsp");
	        } else {
	            DebugLogger.log("取得したカラム数が0もしくはnullです");
	            response.sendRedirect("menu.jsp");
	        }

	    } else {
	        // 従業員コードが選択されていない場合、全従業員表示画面へ遷移
	    	DebugLogger.log("このファイルからshow_all_employee.jspに偏移しました");
	        response.sendRedirect("show_all_employee.jsp");
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

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
	
	/**
	 * リストの要素数を取得するメソッド。
	 * @param employees 従業員情報のリスト
	 * @return リストの要素数。リストがnullの場合は0を返す。
	 */
	public int getEmployeeListSize(List<Employee> employees) {
	    if (employees == null) {
	        return 0;  // リストがnullの場合は0を返す
	    }
	    return employees.size();  // リストの要素数を返す
	}
}
