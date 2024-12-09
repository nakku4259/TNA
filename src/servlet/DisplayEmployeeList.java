package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Debug.DebugLogger;
import model.dao.ViewListDAO;
import model.entity.ViewListDisplay;

/**
 * Servlet implementation class EmployeeListDisplay
 * @author Hideaki Yabe
 * データベースに接続して従業員情報全件一覧を取得するクラス。
 */
@WebServlet("/DisplayEmployeeList")
public class DisplayEmployeeList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request クライアントが Servlet へ要求したリクエスト内容を含む HttpServletRequest オブジェクト。
	 * @param response Servlet がクライアントに返すレスポンス内容を含む HttpServletResponse オブジェクト。
	 * @throws ServletException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * @throws IOException Servlet が GET リクエストを処理している間に入出力エラーが発生した場合。
	 * Servlet に GET リクエストを処理可能にさせるため、(service メソッドを通じて) サーバによって呼び出される。<br>
	 * データベースに接続して従業員情報一覧を取得してページごとにセッションにセットする。
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginUserId") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		request.setCharacterEncoding("UTF-8");

		// ページ情報
		int page = 1;
		int recordsPerPage = 10;
		if (request.getParameter("page") != null) {
			page = Integer.parseInt(request.getParameter("page"));
		}

		// クリアアクションの処理
		String action = request.getParameter("action");
		if ("clear".equals(action)) {
			// フィルター条件をセッションから削除
			session.removeAttribute("lastName");
			session.removeAttribute("gender");
			session.removeAttribute("section");

			// フィルター条件をクリアして最初のページを表示
			response.sendRedirect("DisplayEmployeeList?page=1");
			return;
		}

		// フィルターパラメータの取得またはセッションからの復元
		String lastName = request.getParameter("lastName");
		if (lastName == null) {
			Object lastNameObj = session.getAttribute("lastName");
			if (lastNameObj instanceof String) {
				lastName = (String) lastNameObj;
			}
		}

		String gender = request.getParameter("gender");
		if (gender == null) {
			Object genderObj = session.getAttribute("gender");
			if (genderObj instanceof String) {
				gender = (String) genderObj;
			}
		}

		String section = request.getParameter("section");
		if (section == null) {
			Object sectionObj = session.getAttribute("section");
			if (sectionObj instanceof String) {
				section = (String) sectionObj;
			}
		}

		// フィルター条件をセッションに保存
		session.setAttribute("lastName", lastName);
		session.setAttribute("gender", gender);
		session.setAttribute("section", section);

		ViewListDAO dao = ViewListDAO.getInstance();

		try {
			dao.dbConnect();
			dao.createSt();

			// フィルター条件を使用してリストを取得
			List<ViewListDisplay> vldlist = dao.getFilteredEmployees(lastName, gender, section,
					(page - 1) * recordsPerPage, recordsPerPage);

			// フィルター後の総件数を取得
			int totalRecords = dao.getFilteredEmployeeCount(lastName, gender, section);
			int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

			// セッションに従業員リストとページ情報を設定
			session.setAttribute("vldlist", vldlist);
			session.setAttribute("currentPage", page);
			session.setAttribute("totalPages", totalPages);

			System.out.println("----------------------------");
			DebugLogger.log("lastName=" + lastName);
			DebugLogger.log("gender=" + gender);
			DebugLogger.log("section=" + section);
			System.out.println("");
			DebugLogger.log("currentPage=" + page);
			DebugLogger.log("totalPages=" + totalPages);
			DebugLogger.log("vldlist=" + vldlist.size());
			DebugLogger.log("totalRecords=" + totalRecords);

			response.sendRedirect("show_all_employee.jsp");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dao.dbDiscon();
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
		doGet(request, response);
	}

}
