package model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import model.entity.WorkTime;

/**
 * @author Yoshiyuki Tonami
 * 画面表示のために出退勤時刻管理データベースと繋ぐDAOクラス。
 */
public class WorkTimeDAO {

	/**
	 * 唯一のインスタンスを生成する
	 */
	private static WorkTimeDAO instance = new WorkTimeDAO(); //唯一のインスタンスとする

	/**
	 * 特定のデータベースとの接続(セッション)。
	 */
	private Connection con;
	/**
	 * 静的SQL文を実行し、作成された結果を返すために使用されるオブジェクト。
	 */
	private Statement st;

	/**
	 * privateのため新規のインスタンスをつくらせない。
	 */
	private WorkTimeDAO() {
	}

	/**
	 * @return ViewListDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static WorkTimeDAO getInstance() {
		return instance;
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 特定のデータベースとの接続(セッション)を生成する。
	 */
	public void dbConnect() throws SQLException {
		ConnectionManager cm = ConnectionManager.getInstance();
		con = cm.connect();
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 静的SQL文を実行し、作成された結果を返すために使用されるオブジェクトを生成する。
	 */
	public void createSt() throws SQLException {
		st = con.createStatement();
	}

	/**
	 * 特定のデータベースとの接続(セッション)を切断する。
	 */
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

	/**
	 * @param employeeCode - 従業員コード。
	 * @return - 出勤情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 出勤情報が既に存在しているかチェックする。
	 */
	public String selectStartTime(String employeeCode) throws SQLException {
		String sql = "SELECT * FROM t_work_time WHERE employee_code = '" + employeeCode +
				"' AND work_date = '" + LocalDate.now() + "';";
		ResultSet rs = st.executeQuery(sql);
		if(rs.next()) {
			return "disable";
		} else {
			return null;
		}
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return 退勤情報が既に存在していたら文字列"disble"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 退勤情報が既に存在しているかチェックする。
	 */
	public String selectFinishTime(String employeeCode) throws SQLException {
		String sql = "SELECT * FROM t_work_time WHERE employee_code = '" + employeeCode +
				"' AND work_date = '" + LocalDate.now() + "';";
		ResultSet rs = st.executeQuery(sql);
		if(rs.next() && rs.getString("finish_time") != null) {
			return "disable";
		} else {
			return null;
		}
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return - 休憩開始情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 休憩開始情報が既に存在しているかチェックする。
	 */
	public String selectStartBreak(String employeeCode) throws SQLException {
		String sql = "SELECT * FROM t_work_time WHERE employee_code = '" + employeeCode +
				"' AND work_date = '" + LocalDate.now() + "';";
		ResultSet rs = st.executeQuery(sql);
		if(rs.next() && rs.getString("break_start_time") != null) {
			return "disable";
		} else {
			return null;
		}
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return 休憩終了情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 休憩終了情報が既に存在しているかチェックする。
	 */
	public String selectFinishBreak(String employeeCode) throws SQLException {
		String sql = "SELECT * FROM t_work_time WHERE employee_code = '" + employeeCode +
				"' AND work_date = '" + LocalDate.now() + "';";
		ResultSet rs = st.executeQuery(sql);
		if(rs.next() && rs.getString("break_finish_time") != null) {
			return "disable";
		} else {
			return null;
		}
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @param thisMonth 月。
	 * @return 出退勤時刻管理用モデルクラスのリスト。
	 * @throws SQLException。 データベース処理に問題があった場合。
	 * 従業員コードと月から勤務記録を抽出する。
	 */
	/**
	 * @param employeeCode 従業員コード。
	 * @param thisMonth 月。
	 * @return 出退勤時刻管理用モデルクラスのリスト。
	 * @throws SQLException。 データベース処理に問題があった場合。
	 * 従業員コードと月から勤務記録を抽出する。
	 */
	public List<WorkTime> selectWorkTimeThisMonthList(String employeeCode, String thisMonth)
	        throws SQLException {
	    List<WorkTime> workTimeThisMonthList = new LinkedList<>();
	    String sql = "SELECT * FROM t_work_time WHERE employee_code = '" + employeeCode +
	                 "' AND work_date LIKE '" + thisMonth + "%';";
	    ResultSet rs = st.executeQuery(sql);

	    while (rs.next()) {
	        WorkTime workTime = new WorkTime();
	        
	        // 日付を取得
	        workTime.setWorkDate(LocalDate.parse(
	            rs.getString("work_date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

	        // 時刻フォーマットを定義
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

	        // 出勤時刻がある場合の設定
	        if (rs.getString("start_time") != null) {
	            LocalTime startTime = LocalTime.parse(rs.getString("start_time"), dtf);
	            workTime.setStartTime(startTime);
	        }

	        // 退勤時刻がある場合の設定
	        if (rs.getString("finish_time") != null) {
	            LocalTime finishTime = LocalTime.parse(rs.getString("finish_time"), dtf);
	            workTime.setFinishTime(finishTime);
	        }

	        // 休憩開始時刻がある場合の設定
	        if (rs.getString("break_start_time") != null) {
	            LocalTime breakStartTime = LocalTime.parse(rs.getString("break_start_time"), dtf);
	            workTime.setBreakStartTime(breakStartTime);
	        }

	        // 休憩終了時刻がある場合の設定
	        if (rs.getString("break_finish_time") != null) {
	            LocalTime breakFinishTime = LocalTime.parse(rs.getString("break_finish_time"), dtf);
	            workTime.setBreakFinishTime(breakFinishTime);
	        }

	        // 休憩時間の自動計算
	        if (rs.getString("break_start_time") != null && rs.getString("break_finish_time") != null) {
	            workTime.calcBreakTime();
	        }

	        // 勤務時間の自動計算
	        if (rs.getString("start_time") != null && rs.getString("finish_time") != null) {
	            workTime.calcWorkingHours();
	        }

	        // リストに追加
	        workTimeThisMonthList.add(workTime);
	    }

	    return workTimeThisMonthList;
	}



}
