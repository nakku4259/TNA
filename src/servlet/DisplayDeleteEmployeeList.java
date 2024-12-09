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
import model.entity.ViewListDeleteDisplay;

/**
 * Servlet implementation class DisplayDeleteEmployeeList
 * データベースに接続して論理削除済従業員情報一覧を取得するクラス。
 * @author Hideaki Yabe
 */
@WebServlet("/DisplayDeleteEmployeeList")
public class DisplayDeleteEmployeeList extends HttpServlet {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	    String lastName = request.getParameter("lastName") != null ? request.getParameter("lastName") : (String) session.getAttribute("lastName");
	    String gender = request.getParameter("gender") != null ? request.getParameter("gender") : (String) session.getAttribute("gender");
	    String section = request.getParameter("section") != null ? request.getParameter("section") : (String) session.getAttribute("section");

	    // フィルター条件をセッションに保存
	    session.setAttribute("lastName", lastName);
	    session.setAttribute("gender", gender);
	    session.setAttribute("section", section);

	    ViewListDAO dao = ViewListDAO.getInstance();

	    try {
	        dao.dbConnect();
	        dao.createSt();

	        // フィルター条件を使用してリストを取得
	        List<ViewListDeleteDisplay> vldlist = dao.getFilteredDeletedEmployees(lastName, gender, section, (page - 1) * recordsPerPage, recordsPerPage);

	        // フィルター後の総件数を取得
	        int totalRecords = dao.getFilteredDeletedEmployeeCount(lastName, gender, section);
	        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

	        // セッションに従業員リストとページ情報を設定
	        session.setAttribute("deletevldlist", vldlist);
	        session.setAttribute("currentPage", page);
	        session.setAttribute("totalPages", totalPages);
	        
	      System.out.println("----------------------------");
	      DebugLogger.log("lastName=" +lastName);
	      DebugLogger.log("gender=" + gender);
	      DebugLogger.log("section=" + section);
	      System.out.println("");
	      DebugLogger.log("currentPage=" + page);
	      DebugLogger.log("totalPages=" + totalPages);
	      DebugLogger.log("vldlist=" + vldlist.size());
	      DebugLogger.log("totalRecords=" + totalRecords);
	        
	        response.sendRedirect("show_deleted_employee.jsp");

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        dao.dbDiscon();
	    }
	}

    /**
     * POST リクエストを処理するメソッド。
     * @param request クライアントのリクエスト内容。
     * @param response サーバからのレスポンス内容。
     * @throws ServletException POST リクエスト処理中にエラーが発生した場合。
     * @throws IOException 入出力エラーが発生した場合。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);  // POST リクエストを GET と同じ処理に委譲
    }
}
