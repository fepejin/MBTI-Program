package data2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Quiz {
	Scanner sc = new Scanner(System.in);
	Connection con = null;
	private int cnt;
	private boolean find = false;

	public Quiz() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			con = DriverManager.getConnection(url, "C##scott", "tiger");
		} catch (ClassNotFoundException ce) {
			System.out.println(ce.getMessage());
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		}
	}

	// e테스트
	public String eTest() {
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = con.createStatement();
			String sql1 = "select * from etest";
			rs1 = stmt1.executeQuery(sql1);
			cnt = 0;
			while (rs1.next()) {
				int num = rs1.getInt("num");
				String quiz = rs1.getString("quiz");
				// 잘못된 번호를 입력했을때 다시 반복하기 위한 반복문
				while (true) {
					System.out.println(num + ")" + quiz);
					System.out.println("1.맞음 2.다소맞음 3.모르겠다 4.다소아님 5.아님");
					int n = sc.nextInt();
					switch (n) {
					case 1:
						cnt = cnt - 2;
						find = true;
						break;
					case 2:
						cnt = cnt - 1;
						find = true;
						break;
					case 3:
						find = true;
						break;
					case 4:
						cnt = cnt + 1;
						find = true;
						break;
					case 5:
						cnt = cnt + 2;
						find = true;
						break;
					}
					if (!(n <= 5 && n > 0)) {
						System.out.println("없는 번호입니다. 다시 입력하세요");
						continue;
					}
					if (find)
						break;
				}
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (stmt1 != null)
					stmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
		if (cnt < 0) {
			return "E";
		} else {
			return "I";
		}
	}

	// s테스트
	public String sTest() {
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = con.createStatement();
			String sql1 = "select * from stest";
			rs1 = stmt1.executeQuery(sql1);
			cnt = 0;
			while (rs1.next()) {
				int num = rs1.getInt("num");
				String quiz = rs1.getString("quiz");
				// 잘못된 번호를 입력했을때 다시 반복하기 위한 반복문
				while (true) {
					System.out.println(num + ")" + quiz);
					System.out.println("1.맞음 2.다소맞음 3.모르겠다 4.다소아님 5.아님");
					int n = sc.nextInt();
					switch (n) {
					case 1:
						cnt = cnt - 2;
						find = true;
						break;
					case 2:
						cnt = cnt - 1;
						find = true;
						break;
					case 3:
						find = true;
						break;
					case 4:
						cnt = cnt + 1;
						find = true;
						break;
					case 5:
						cnt = cnt + 2;
						find = true;
						break;
					}
					if (!(n <= 5 && n > 0)) {
						System.out.println("없는 번호입니다. 다시 입력하세요");
						continue;
					}
					if (find)
						break;
				}
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (stmt1 != null)
					stmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
		if (cnt <= 0) {
			return "N";
		} else {
			return "S";
		}
	}

	public String tTest() {
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = con.createStatement();
			String sql1 = "select * from ttest";
			rs1 = stmt1.executeQuery(sql1);
			cnt = 0;
			while (rs1.next()) {
				int num = rs1.getInt("num");
				String quiz = rs1.getString("quiz");
				// 잘못된 번호를 입력했을때 다시 반복하기 위한 반복문
				while (true) {
					System.out.println(num + ")" + quiz);
					System.out.println("1.맞음 2.다소맞음 3.모르겠다 4.다소아님 5.아님");
					int n = sc.nextInt();
					switch (n) {
					case 1:
						cnt = cnt - 2;
						find = true;
						break;
					case 2:
						cnt = cnt - 1;
						find = true;
						break;
					case 3:
						find = true;
						break;
					case 4:
						cnt = cnt + 1;
						find = true;
						break;
					case 5:
						cnt = cnt + 2;
						find = true;
						break;
					}
					if (!(n <= 5 && n > 0)) {
						System.out.println("없는 번호입니다. 다시 입력하세요");
						continue;
					}
					if (find)
						break;
				}
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (stmt1 != null)
					stmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
		if (cnt <= 0) {
			return "F";
		} else {
			return "T";
		}
	}

	public String jTest() {
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = con.createStatement();
			String sql1 = "select * from jtest";
			rs1 = stmt1.executeQuery(sql1);
			cnt = 0;
			while (rs1.next()) {
				int num = rs1.getInt("num");
				String quiz = rs1.getString("quiz");
				// 잘못된 번호를 입력했을때 다시 반복하기 위한 반복문
				while (true) {
					System.out.println(num + ")" + quiz);
					System.out.println("1.맞음 2.다소맞음 3.모르겠다 4.다소아님 5.아님");
					int n = sc.nextInt();
					switch (n) {
					case 1:
						cnt = cnt - 2;
						find = true;
						break;
					case 2:
						cnt = cnt - 1;
						find = true;
						break;
					case 3:
						find = true;
						break;
					case 4:
						cnt = cnt + 1;
						find = true;
						break;
					case 5:
						cnt = cnt + 2;
						find = true;
						break;
					}
					if (!(n <= 5 && n > 0)) {
						System.out.println("없는 번호입니다. 다시 입력하세요");
						continue;
					}
					if (find)
						break;
				}
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (stmt1 != null)
					stmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
		if (cnt < 0) {
			return "J";
		} else {
			return "P";
		}
	}
}
