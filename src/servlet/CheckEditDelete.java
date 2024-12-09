package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Debug.DebugLogger;

/**
 * Servlet implementation class CheckEditDelete
 * @author Hideaki Yabe
 * 削除処理か編集処理か判断するクラス。
 */
@WebServlet("/CheckEditDelete")
public class CheckEditDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントが Servlet へ要求したリクエスト内容を含む HttpServletRequest オブジェクト。
	 * @param response Servlet がクライアントに返すレスポンス内容を含む HttpServletResponse オブジェクト。
	 * @throws ServletException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * @throws IOException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * Servlet に GET リクエストを処理可能にさせるため、(service メソッドを通じて) サーバによって呼び出される。<br>
	 * 直接アクセスに対して従業員が既にログインしていたらメニュー画面にリダイレクトさせる。
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.getAttribute("loginUserId") == null) {
			response.sendRedirect("login.jsp");
		} else {
			response.sendRedirect("menu.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントが Servlet へ要求したリクエスト内容を含む HttpServletRequest オブジェクト。
	 * @param response Servlet がクライアントに返すレスポンス内容を含む HttpServletResponse オブジェクト。
	 * @throws ServletException Servlet が POST リクエストを処理している間に入出力エラーが発生した場合。
	 * @throws IOException POST に相当するリクエストが処理できない場合。
	 * Servlet に POST リクエストを処理可能にさせるため、(service メソッド経由で) サーバによって呼び出される。<br>
	 * 削除処理か編集処理か判断する。
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
		//デバックコード
		DebugLogger.log("doPost メソッド開始");
		
	    HttpSession session = request.getSession();
	    request.setCharacterEncoding("utf-8");

	    // リクエストから選択された従業員コードを取得してセッションに保存
	    String[] selectedEmployeeCodes = request.getParameterValues("employeeCode");
	    session.setAttribute("selectedEmployeeCodes", selectedEmployeeCodes);

	    // どのボタンが押されたかを判断して分岐処理
	    String action = request.getParameter("submit");

	    switch (action) {
	        case "従業員を編集する":
	            response.sendRedirect("EditCheckEmployee");
	            break;

	        case "従業員を削除する":
	        	//Postメソッドに行く場合があるので明示的にGetメソッドに行くように?action=deleteを追加
	        	response.sendRedirect("DeleteEmployee");
	            break;

	        case "削除済従業員":
	            response.sendRedirect("show_deleted_employee.jsp");
	            break;

	        case "従業員(削)を編集する":
	            // 削除済従業員を編集するページへリダイレクト
	            response.sendRedirect("EditCheckDeletedEmployee");
	            break;

	        case "従業員(削)を復元する":
	            // 従業員の復元処理を行うページへリダイレクト
	            response.sendRedirect("RestoreEmployee");
	            break;

	        default:
	            // その他のリクエストは全従業員一覧にリダイレクト
	            response.sendRedirect("show_all_employee.jsp");
	            break;
	    }
	}


}
