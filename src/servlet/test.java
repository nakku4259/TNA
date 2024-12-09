package servlet;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// アノテーションでURLパターンを指定
@WebServlet("/test")
public class test extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // GETリクエストの処理 (オプション: 必要に応じて処理)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("employeeList.jsp");  // 直接アクセスされた場合、リストページにリダイレクト
    }

    // POSTリクエストの処理
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // JSPから送信された選択された従業員コードを取得
        String[] selectedEmployeeCodes = request.getParameterValues("employeeCode");

	    request.setCharacterEncoding("utf-8");



        if (selectedEmployeeCodes != null) {
            for (String code : selectedEmployeeCodes) {
                // 各従業員コードに対する処理
                System.out.println("選択された従業員コード: " + code);

                // 編集または削除の処理をここで実装
                // 例: データベースの更新処理、復元処理など
            }

            // 処理が完了した後のリダイレクト (例: 従業員一覧ページに戻る)
            
        } else {
            // エラーメッセージをJSPに渡す (選択がなかった場合)
        	System.out.println("空っぽ");
        }
    }
}
