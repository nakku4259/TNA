package test;

import model.dao.UserDAO;

/**
 * ConnectionManagerのテスト用クラス。
 */
public class Main {

    public static void main(String[] args) {
//        // ConnectionManagerのインスタンスを取得
//        ConnectionManager manager = ConnectionManager.getInstance();
//
//        // 接続テスト
//        try (Connection conn = manager.connect()) {
//            System.out.println("データベースへの接続に成功しました！");
//        } catch (SQLException e) {
//            System.out.println("データベースへの接続に失敗しました。");
//            e.printStackTrace();
//        }
//        System.out.println("------------------------------");
    	
        try {
            // データベース接続とDAOインスタンスを取得
            UserDAO userDAO = UserDAO.getInstance();
            userDAO.dbConnect();
            userDAO.createSt();

            // 初期ユーザの登録 (ユーザID: admin, パスワード: admin123)
            boolean result = userDAO.insertUser("admin", "pass123");

            if (result) {
                System.out.println("初期ユーザの登録が成功しました。");
            } else {
                System.out.println("ユーザ登録に失敗しました。");
            }

            userDAO.dbDiscon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
