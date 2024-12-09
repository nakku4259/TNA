package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import Debug.DebugLogger;
import model.entity.ViewListDeleteDisplay;
import model.entity.ViewListDisplay;

public class ViewListDAO {
	private static ViewListDAO instance = new ViewListDAO();
	private Connection con;
	private Statement st;
	private List<ViewListDisplay> list = new LinkedList<ViewListDisplay>();
	private List<ViewListDeleteDisplay> deleteList = new LinkedList<ViewListDeleteDisplay>();

	private ViewListDAO() {
	}

	public static ViewListDAO getInstance() {
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

	public List<ViewListDisplay> showAllList() throws SQLException {
		list.clear();

		String sql = "SELECT e.employee_code, "
				+ "concat(e.last_name, e.first_name) AS employee_name, "
				+ "concat(e.last_kana_name, e.first_kana_name) AS employee_kana_name, "
				+ "e.gender, e.birth_day, "
				+ "s.section_name, e.hire_date "
				+ "FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 0";

		ResultSet rs = st.executeQuery(sql);

		while (rs.next()) {
			ViewListDisplay vld = new ViewListDisplay();
			vld.setEmployeeCode(rs.getString("employee_code"));
			vld.setEmployeeName(rs.getString("employee_name"));
			vld.setEmployeeKanaName(rs.getString("employee_kana_name"));
			vld.setGender(rs.getInt("gender"));
			vld.setBirthDay(rs.getDate("birth_day"));
			vld.setSectionName(rs.getString("section_name"));
			vld.setHireDate(rs.getDate("hire_date"));
			list.add(vld);
		}
		return list;
	}

	public List<ViewListDeleteDisplay> showDeleteList() throws SQLException {
		deleteList.clear();

		String sql = "SELECT e.employee_code, "
				+ "concat(e.last_name, e.first_name) AS employee_name, "
				+ "concat(e.last_kana_name, e.first_kana_name) AS employee_kana_name, "
				+ "e.gender, e.birth_day, "
				+ "s.section_name, e.hire_date "
				+ "FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 1";

		ResultSet rs = st.executeQuery(sql);

		while (rs.next()) {
			ViewListDeleteDisplay vld = new ViewListDeleteDisplay();
			vld.setEmployeeCode(rs.getString("employee_code"));
			vld.setEmployeeName(rs.getString("employee_name"));
			vld.setEmployeeKanaName(rs.getString("employee_kana_name"));
			vld.setGender(rs.getInt("gender"));
			vld.setBirthDay(rs.getDate("birth_day"));
			vld.setSectionName(rs.getString("section_name"));
			vld.setHireDate(rs.getDate("hire_date"));
			deleteList.add(vld);
		}
		return deleteList;
	}

	public List<ViewListDisplay> getFilteredEmployees(String lastName, String gender, String section, int start,
			int total) throws SQLException {
		list.clear();
		String sql = "SELECT e.employee_code, "
				+ "concat(e.last_name, e.first_name) AS employee_name, "
				+ "concat(e.last_kana_name, e.first_kana_name) AS employee_kana_name, "
				+ "e.gender, e.birth_day, "
				+ "s.section_name, e.hire_date "
				+ "FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 0";

		// フィルター条件を動的に追加
		if (lastName != null && !lastName.isEmpty()) {
			sql += " AND e.last_name LIKE ?";
		}
		if (gender != null && !gender.isEmpty()) {
			sql += " AND e.gender = ?";
		}
		if (section != null && !section.isEmpty()) {
			sql += " AND s.section_name LIKE ?";
		}

		sql += " LIMIT ? OFFSET ?";
		System.out.println("-----------------------");
		DebugLogger.log("実行されるSQL:" + sql);

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int index = 1;

			// フィルター条件に基づいてパラメータを設定
			if (lastName != null && !lastName.isEmpty()) {
				ps.setString(index++, "%" + lastName + "%");
			}
			if (gender != null && !gender.isEmpty()) {
				ps.setString(index++, gender);
			}
			if (section != null && !section.isEmpty()) {
				ps.setString(index++, "%" + section + "%");
			}

			// ページネーションのパラメータ設定
			ps.setInt(index++, total);
			ps.setInt(index, start);

			DebugLogger.log("LIMIT (total) パラメータ: " + total);
			DebugLogger.log("OFFSET (start) パラメータ: " + start);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ViewListDisplay vld = new ViewListDisplay();
				vld.setEmployeeCode(rs.getString("employee_code"));
				vld.setEmployeeName(rs.getString("employee_name"));
				vld.setEmployeeKanaName(rs.getString("employee_kana_name"));
				vld.setGender(rs.getInt("gender"));
				vld.setBirthDay(rs.getDate("birth_day"));
				vld.setSectionName(rs.getString("section_name"));
				vld.setHireDate(rs.getDate("hire_date"));
				list.add(vld);
			}
		}
		return list;
	}

	public int getFilteredEmployeeCount(String lastName, String gender, String section) throws SQLException {
		// m_section テーブルを JOIN するように修正
		String query = "SELECT COUNT(*) FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 0";

		// フィルター条件を追加
		if (lastName != null && !lastName.isEmpty()) {
			query += " AND e.last_name LIKE ?";
		}
		if (gender != null && !gender.isEmpty()) {
			query += " AND e.gender = ?";
		}
		if (section != null && !section.isEmpty()) {
			query += " AND s.section_name LIKE ?";
		}

		PreparedStatement pstmt = this.con.prepareStatement(query);
		int index = 1;

		// フィルター条件のパラメータを設定
		if (lastName != null && !lastName.isEmpty()) {
			pstmt.setString(index++, "%" + lastName + "%");
		}
		if (gender != null && !gender.isEmpty()) {
			pstmt.setString(index++, gender);
		}
		if (section != null && !section.isEmpty()) {
			pstmt.setString(index++, "%" + section + "%");
		}

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	public List<ViewListDeleteDisplay> getFilteredDeletedEmployees(String lastName, String gender, String section,
			int start, int total) throws SQLException {
		deleteList.clear();
		String sql = "SELECT e.employee_code, "
				+ "concat(e.last_name, e.first_name) AS employee_name, "
				+ "concat(e.last_kana_name, e.first_kana_name) AS employee_kana_name, "
				+ "e.gender, e.birth_day, "
				+ "s.section_name, e.hire_date "
				+ "FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 1";

		// フィルター条件を動的に追加
		if (lastName != null && !lastName.isEmpty()) {
			sql += " AND e.last_name LIKE ?";
		}
		if (gender != null && !gender.isEmpty()) {
			sql += " AND e.gender = ?";
		}
		if (section != null && !section.isEmpty()) {
			sql += " AND s.section_name LIKE ?";
		}

		sql += " LIMIT ? OFFSET ?";
		System.out.println("-----------------------");
		DebugLogger.log("実行されるSQL:" + sql);

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int index = 1;

			// フィルター条件に基づいてパラメータを設定
			if (lastName != null && !lastName.isEmpty()) {
				ps.setString(index++, "%" + lastName + "%");
			}
			if (gender != null && !gender.isEmpty()) {
				ps.setString(index++, gender);
			}
			if (section != null && !section.isEmpty()) {
				ps.setString(index++, "%" + section + "%");
			}

			// ページネーションのパラメータ設定
			ps.setInt(index++, total);
			ps.setInt(index, start);

			DebugLogger.log("LIMIT (total) パラメータ: " + total);
			DebugLogger.log("OFFSET (start) パラメータ: " + start);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ViewListDeleteDisplay vld = new ViewListDeleteDisplay();
				vld.setEmployeeCode(rs.getString("employee_code"));
				vld.setEmployeeName(rs.getString("employee_name"));
				vld.setEmployeeKanaName(rs.getString("employee_kana_name"));
				vld.setGender(rs.getInt("gender"));
				vld.setBirthDay(rs.getDate("birth_day"));
				vld.setSectionName(rs.getString("section_name"));
				vld.setHireDate(rs.getDate("hire_date"));
				deleteList.add(vld);
			}
		}
		return deleteList;
	}

	public int getFilteredDeletedEmployeeCount(String lastName, String gender, String section) throws SQLException {
		String query = "SELECT COUNT(*) FROM m_employee e "
				+ "LEFT OUTER JOIN m_section s ON e.section_code = s.section_code "
				+ "WHERE e.is_deleted = 1";

		// フィルター条件を追加
		if (lastName != null && !lastName.isEmpty()) {
			query += " AND e.last_name LIKE ?";
		}
		if (gender != null && !gender.isEmpty()) {
			query += " AND e.gender = ?";
		}
		if (section != null && !section.isEmpty()) {
			query += " AND s.section_name LIKE ?";
		}

		PreparedStatement pstmt = this.con.prepareStatement(query);
		int index = 1;
		if (lastName != null && !lastName.isEmpty()) {
			pstmt.setString(index++, "%" + lastName + "%");
		}
		if (gender != null && !gender.isEmpty()) {
			pstmt.setString(index++, gender);
		}
		if (section != null && !section.isEmpty()) {
			pstmt.setString(index++, "%" + section + "%");
		}

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}
}
