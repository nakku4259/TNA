package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.entity.Employee;
import model.entity.Section;

/**
 * @author Yusuke Tanabe
 * 従業員データベースと繋ぐDAOクラス。
 */
public class EmployeeDAO {
	private static EmployeeDAO instance = new EmployeeDAO(); // 唯一のインスタンス
	private Connection con; // データベース接続
	private Statement st; // SQL文実行用

	private EmployeeDAO() {
	}

	public static EmployeeDAO getInstance() {
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
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertEmployee(String lastName, String firstName, String lastKanaName, String firstKanaName,
			int gender, String birthDay, String sectionCode, String hireDate, String password) throws SQLException {
		con.setAutoCommit(false); // オートコミットを無効化

		String sql = "SELECT MAX(employee_code) FROM m_employee;";
		ResultSet rs = st.executeQuery(sql);
		int code = 0;
		String employeeCode;

		if (rs.next()) {
			code = Integer.parseInt(rs.getString("MAX(employee_code)").substring(1)) + 1;
		}

		employeeCode = "E" + String.format("%03d", code);

		boolean flag = false;
		String sql2 = "INSERT INTO m_employee (employee_code, last_name, first_name, last_kana_name, "
		        + "first_kana_name, gender, birth_day, section_code, hire_date, password) "
		        + "VALUES ('" + employeeCode + "', '" + lastName + "', '" + firstName + "', '"
		        + lastKanaName + "', '" + firstKanaName + "', " + gender + ", '"
		        + birthDay + "', '" + sectionCode + "', '" + hireDate + "', '" + password + "');";


		int result = st.executeUpdate(sql2);

		if (result > 0) {
			flag = true;
			con.commit(); // 成功した場合コミット
		}

		return flag;
	}

	public Employee updateEmployee(Employee employee) throws SQLException {
		con.setAutoCommit(false);

		String sql = "UPDATE m_employee SET last_name = '" + employee.getLastName()
				+ "', first_name = '" + employee.getFirstName()
				+ "', last_kana_name = '" + employee.getLastKanaName()
				+ "', first_kana_name = '" + employee.getFirstKanaName()
				+ "', gender = '" + employee.getGender()
				+ "', birth_day = '" + employee.getBirthDay()
				+ "', section_code = '" + employee.getSectionCode()
				+ "', hire_date = '" + employee.getHireDate()
				+ "', is_deleted = '" + employee.getDeletedFlag()
				+ "' WHERE employee_code = '" + employee.getEmployeeCode() + "';";

		int count = st.executeUpdate(sql);

		if (count > 0) {
			con.commit(); // 成功した場合コミット
		}

		return employee;
	}

	public Employee selectEmployee(String employeeCode) throws SQLException {
	    String sql = "SELECT * FROM m_employee WHERE employee_code = ? AND is_deleted = 0";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, employeeCode);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Employee employee = new Employee();
	                employee.setEmployeeCode(rs.getString("employee_code"));
	                employee.setLastName(rs.getString("last_name"));
	                employee.setFirstName(rs.getString("first_name"));
	                employee.setLastKanaName(rs.getString("last_kana_name"));
	                employee.setFirstKanaName(rs.getString("first_kana_name"));
	                employee.setGender(rs.getInt("gender"));
	                employee.setBirthDay(rs.getString("birth_day"));
	                employee.setSectionCode(rs.getString("section_code"));
	                employee.setHireDate(rs.getString("hire_date"));
	                employee.setDeletedFlag(rs.getInt("is_deleted"));
	                return employee;
	            }
	        }
	    }
	    return null;
	}
	
	public List<Employee> selectEmployee(String[] employeeCodes) throws SQLException {
	    // SQLのIN句を生成
	    String placeholders = String.join(",", java.util.Collections.nCopies(employeeCodes.length, "?"));
	    String sql = "SELECT * FROM m_employee WHERE employee_code IN (" + placeholders + ") AND is_deleted = 0";

	    List<Employee> employeeList = new ArrayList<>();

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        // 各従業員コードをIN句のプレースホルダにセット
	        for (int i = 0; i < employeeCodes.length; i++) {
	            ps.setString(i + 1, employeeCodes[i]);
	        }

	        // クエリを実行して結果を取得
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Employee employee = new Employee();
	                employee.setEmployeeCode(rs.getString("employee_code"));
	                employee.setLastName(rs.getString("last_name"));
	                employee.setFirstName(rs.getString("first_name"));
	                employee.setLastKanaName(rs.getString("last_kana_name"));
	                employee.setFirstKanaName(rs.getString("first_kana_name"));
	                employee.setGender(rs.getInt("gender"));
	                employee.setBirthDay(rs.getString("birth_day"));
	                employee.setSectionCode(rs.getString("section_code"));
	                employee.setHireDate(rs.getString("hire_date"));
	                employee.setDeletedFlag(rs.getInt("is_deleted"));

	                // リストに追加
	                employeeList.add(employee);
	            }
	        }
	    }
	    return employeeList;
	}

	
	public Employee selectDletedEmployee(String employeeCode) throws SQLException {
	    String sql = "SELECT * FROM m_employee WHERE employee_code = ? AND is_deleted = 1";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, employeeCode);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Employee employee = new Employee();
	                employee.setEmployeeCode(rs.getString("employee_code"));
	                employee.setLastName(rs.getString("last_name"));
	                employee.setFirstName(rs.getString("first_name"));
	                employee.setLastKanaName(rs.getString("last_kana_name"));
	                employee.setFirstKanaName(rs.getString("first_kana_name"));
	                employee.setGender(rs.getInt("gender"));
	                employee.setBirthDay(rs.getString("birth_day"));
	                employee.setSectionCode(rs.getString("section_code"));
	                employee.setHireDate(rs.getString("hire_date"));
	                employee.setDeletedFlag(rs.getInt("is_deleted"));
	                return employee;
	            }
	        }
	    }
	    return null;
	}


	public int deleteEmployee(String employeeCode) throws SQLException {
	    con.setAutoCommit(false);  // オートコミットを無効化

	    // 論理削除のSQL：is_deletedを1に設定
	    String sql = "UPDATE m_employee SET is_deleted = 1 WHERE employee_code = ?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, employeeCode);
	        int count = ps.executeUpdate();  // 更新件数を取得

	        if (count > 0) {
	            con.commit();  // 成功した場合コミット
	        }
	        return count;
	    } catch (SQLException e) {
	        con.rollback();  // エラー時はロールバック
	        throw e;
	    }
	}
	
	public int deleteEmployee(String[] employeeCodes) throws SQLException {
	    con.setAutoCommit(false);  // オートコミットを無効化
	    String sql = "UPDATE m_employee SET is_deleted = 1 WHERE employee_code = ?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        int totalUpdated = 0;  // 更新件数の合計を管理

	        // 配列の要素ごとにSQLクエリを実行
	        for (String employeeCode : employeeCodes) {
	            ps.setString(1, employeeCode);
	            totalUpdated += ps.executeUpdate();  // 更新件数を加算
	        }

	        con.commit();  // 全て成功した場合コミット
	        return totalUpdated;
	    } catch (SQLException e) {
	        con.rollback();  // エラー時はロールバック
	        throw e;
	    }
	}

	

	public int restoreEmployee(String employeeCode) throws SQLException {
	    con.setAutoCommit(false);  // オートコミットを無効化

	    // 論理削除のSQL：is_deletedを1に設定
	    String sql = "UPDATE m_employee SET is_deleted = 0 WHERE employee_code = ?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, employeeCode);
	        int count = ps.executeUpdate();  // 更新件数を取得

	        if (count > 0) {
	            con.commit();  // 成功した場合コミット
	        }
	        return count;
	    } catch (SQLException e) {
	        con.rollback();  // エラー時はロールバック
	        throw e;
	    }
	}
	
	public int restoreEmployee(String[] employeeCodes) throws SQLException {
	    con.setAutoCommit(false);  // オートコミットを無効化
	    String sql = "UPDATE m_employee SET is_deleted = 0 WHERE employee_code = ?";

	    int totalCount = 0;  // 復元した件数の合計

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        // 配列の各コードを順に処理
	        for (String employeeCode : employeeCodes) {
	            ps.setString(1, employeeCode);  // パラメータをセット
	            totalCount += ps.executeUpdate();  // 更新件数を合計
	        }

	        // 1件以上成功した場合、コミット
	        if (totalCount > 0) {
	            con.commit();
	        }
	        return totalCount;

	    } catch (SQLException e) {
	        con.rollback();  // エラー時はロールバック
	        throw e;

	    }
	}



	public List<Section> getSection() throws SQLException {
		String sql = "SELECT * FROM m_section ORDER BY section_code;";
		List<Section> sections = new LinkedList<>();
		ResultSet rs = st.executeQuery(sql);

		while (rs.next()) {
			Section se = new Section();
			se.setSectionCode(rs.getString("section_code"));
			se.setSectionName(rs.getString("section_name"));
			sections.add(se);
		}

		return sections;
	}
}
