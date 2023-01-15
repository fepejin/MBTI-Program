package data2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Members {
	Scanner sc = new Scanner(System.in);
	Rate r = new Rate();
	Contents c = new Contents();
	Connection con = null;

	public Members() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			con = DriverManager.getConnection(url, "C##scott", "tiger");
			System.out.println("DB접속 성공");
		} catch (ClassNotFoundException ce) {
			System.out.println("드라이버 로딩 실패" + ce.getMessage());
		} catch (SQLException se) {
			System.out.println("DB접속 실패" + se.getMessage());
		}
	}

	// mbti 테스트 메소드
	public String mtest(String nick) {
		// 4가지 유형 테스트 가져오기
		Quiz q = new Quiz();

		String e = q.eTest(); // E/I
		String s = q.sTest(); // S/N
		String t = q.tTest(); // T/F
		String j = q.jTest(); // P/J
//			MBTI 조합 문자열
		String mbti = e + s + t + j;
		System.out.println(nick + "님의 mbti는 " + mbti + "입니다.");
		return mbti;
	}

	// 로그인
	public void login() throws SQLException {
		PreparedStatement pstmt1 = null; // ID와 PW입력을 통한 로그인 기능
		PreparedStatement pstmt2 = null; // 로그인 후 mbti 누락시 입력하는 기능
		ResultSet rs = null;
		try {
			String sql = "select * from mems where id=? and pw=?";
			pstmt1 = con.prepareStatement(sql);
			System.out.println("ID: ");
			String id = sc.next();
			System.out.println("비밀번호: ");
			String pw = sc.next();
			pstmt1.setString(1, id);
			pstmt1.setString(2, pw);
			rs = pstmt1.executeQuery();
			// 로그인
			if (rs.next()) {
				String nick = rs.getString("nick");
				String mbti = rs.getString("mbti");
				System.out.println("로그인이 완료되었습니다. 반갑습니다 " + nick + "님!"); // 로그인 완료
				// mbti 유무확인
				while (true) {
					if (mbti != null) { // mbti가 있으면(값이 null이 아니면) 컨텐츠 실행
						System.out.println("회원님의 mbti는 " + mbti + "입니다.");
						break;
					} else { // mbti가 없으면 테스트 메뉴실행
						System.out.println("현재 회원님의 mbti의 정보가 없습니다. 컨텐츠는 mbti가 입력되어야 진행이 가능합니다.");
						while (true) {
							System.out.println("1. 테스트 실행 2. 나중에 하기(프로그램 종료)");
							int n = sc.nextInt();
							if (n == 1) { // 1번 선택시 mbti테스트를 실행함.Member클래스의 mtest(닉네임)메소드 사용
								String nmbti = mtest(nick); // 테스트 완료후 얻어온 MBTI값
								String sql2 = "update mems set mbti=? where nick=?";
								pstmt2 = con.prepareStatement(sql2);
								pstmt2.setString(1, nmbti);
								pstmt2.setString(2, nick);
								pstmt2.executeUpdate(); // 회원의 MBTI업데이트
								r.per(); // 퍼센트 업데이트 Rate클래스의 per메소드 사용
								System.out.println("업데이트 완료.");
								break;
							} else if (n == 2) {
								System.out.println("프로그램을 종료합니다.");
								System.exit(0);
							} else {
								System.out.println("번호를 정확히 입력하세요.");
								continue;
							}
						}
					}
					break; // 유무확인 반복문 빠져나감
				}
				while (true) { // 로그인 후 mbti가 존재할때: 컨텐츠 실행
					System.out.println("컨텐츠를 선택하세요.");
					System.out.println("1.친구매칭 2.영화추천 3.스터디룸 4.현재 mbti별 회원비율보기 0.종료");
					int n = sc.nextInt();
					switch (n) {
					case 1:
						c.matching(nick);
						break;
					case 2:
						c.recommendmv(nick);
						break;
					case 3:
						c.studyroom(nick);
						break;
					case 4:
						r.listRate();
						break;
					case 0:
						System.out.println("프로그램을 종료합니다.");
						c.close();
						System.exit(0);
					}
				}
			} else {
				System.out.println("아이디 또는 비밀번호가 일치하지 않습니다");
			}
		} finally {
			try {
				if (pstmt2 != null)
					pstmt2.close();
				if (rs != null)
					rs.close();
				if (pstmt1 != null)
					pstmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}

	}

	// 회원가입
	public void create() throws SQLException {
		PreparedStatement pstmt1 = null; // 회원정보 입력기능
		PreparedStatement pstmt2 = null; // id중복조회 기능
		PreparedStatement pstmt3 = null; // nick중복조회 기능
		ResultSet rs1 = null; // pstmt2
		ResultSet rs2 = null; // pstmt3
		try {
			// 회원가입(mbti 제외한 정보들 입력)
			String sql1 = ("insert into mems(id,pw,nick,gender,email,regdate) values(?,?,?,?,?,sysdate)");
			pstmt1 = con.prepareStatement(sql1);
			// id중복조회
			String sql2 = ("select * from mems where id=?");
			pstmt2 = con.prepareStatement(sql2);
			// nick 중복조회
			String sql3 = ("select * from mems where nick=?");
			pstmt3 = con.prepareStatement(sql3);
			// id 중복조회, 입력
			while (true) {
				System.out.println("ID입력");
				String id = sc.next();
				pstmt2.setString(1, id);
				rs1 = pstmt2.executeQuery();
				if (rs1.next()) { // 입력한 아이디가 존재하면
					System.out.println(id + "은 존재하는 아이디입니다. 다시 입력하여 주십시오");
				} else {
					pstmt1.setString(1, id);
					break;
				} // 없으면 pstmt1에 첫번째값 입력
			} // pw입력
			System.out.println("비밀번호 입력");
			String pw = sc.next();
			pstmt1.setString(2, pw);
			// nick중복조회, 입력
			while (true) {
				System.out.println("닉네임 입력");
				String nick = sc.next();
				pstmt3.setString(1, nick);
				rs1 = pstmt3.executeQuery();
				if (rs1.next()) { // 입력한 닉네임이 존재하면
					System.out.println("존재하는 닉네임 입니다. 다시 입력하여 주십시오.");
				} else {
					pstmt1.setString(3, nick);
					break;
				} // 없으면 pstmt1에 세번째값 입력
			}
			while (true) {
				System.out.println("성별입력(여성/남성)"); // 여성 또는 남성만 입력하도록 함
				String gender = sc.next();
				// check제약조건을 걸어주었으나 예외가 걸리면 프로그램이 종료되어버리므로 반복문에 조건을 걸어 작성함
				if (gender.equals("여성") || gender.equals("남성")) {
					pstmt1.setString(4, gender);
					break;
				} else {
					System.out.println("여성 또는 남성으로 입력하세요.");
				}
			} // 이메일 입력
			System.out.println("메일 입력");
			String email = sc.next();
			pstmt1.setString(5, email);
			int a = pstmt1.executeUpdate();
			System.out.println(a + "명의 회원가입 완료");
		} finally {
			try {
				if (pstmt3 != null)
					pstmt3.close();
				if (rs2 != null)
					rs2.close();
				if (pstmt2 != null)
					pstmt2.close();
				if (rs1 != null)
					rs1.close();
				if (pstmt1 != null)
					pstmt1.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
}
