package model.dao;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class UserDAO {
    private static UserDAO instance = new UserDAO(); // 唯一のインスタンス
    private Connection con;
    private Statement st;

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        return instance;
    }

    public void dbConnect() throws SQLException {
        ConnectionManager cm = ConnectionManager.getInstance();
        con = cm.connect();
    }

    public void createSt() throws SQLException {
        st = con.createStatement();
    }

    public void dbDiscon() {
        try {
            if (st != null) st.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ソルトを生成するメソッド
    private String generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt); // Base64でエンコード
    }

    // SHA-256 + ソルトでパスワードをハッシュ化するメソッド
    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String saltedPassword = password + salt;
        byte[] hashedBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new BigInteger(1, hashedBytes));
    }

    // ユーザのログイン処理
    public boolean loginUser(String userId, String password) throws SQLException, NoSuchAlgorithmException {
        boolean loginUserChkFlag = false;

        // SQLクエリでユーザIDに対応するパスワードとソルトを取得
        String sql = "SELECT password, salt FROM m_user WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                String hashedInputPassword = hashPassword(password, salt);

                // ハッシュ化されたパスワードと照合
                if (storedPassword.equals(hashedInputPassword)) {
                    loginUserChkFlag = true;
                }
            }
        }
        return loginUserChkFlag;
    }

    // 新しいユーザを登録する処理
    public boolean insertUser(String userId, String password) throws SQLException, NoSuchAlgorithmException {
        con.setAutoCommit(false);
        boolean insertUserChkFlag = false;

        // 既存ユーザのチェック
        String sql = "SELECT * FROM m_user WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) { // ユーザが存在しない場合のみ登録
                String salt = generateSalt(); // ソルトを生成
                String hashedPassword = hashPassword(password, salt); // ソルト付きハッシュを生成

                // 新しいユーザを挿入
                sql = "INSERT INTO m_user (user_id, password, salt) VALUES (?, ?, ?)";
                try (PreparedStatement insertPs = con.prepareStatement(sql)) {
                    insertPs.setString(1, userId);
                    insertPs.setString(2, hashedPassword);
                    insertPs.setString(3, salt);

                    int result = insertPs.executeUpdate();
                    if (result > 0) {
                        insertUserChkFlag = true;
                        con.commit(); // コミット
                    }
                }
            }
        } catch (SQLException e) {
            con.rollback(); // エラー時はロールバック
            throw e;
        }
        return insertUserChkFlag;
    }
}

