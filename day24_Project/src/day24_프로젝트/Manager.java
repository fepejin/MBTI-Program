package day24_프로젝트;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import data2.Contents;
import data2.Rate;
class Mmenu{
	Connection con=null;
	Scanner sc=new Scanner(System.in);
	public Mmenu() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url="jdbc:oracle:thin:@localhost:1521:xe";
			con=DriverManager.getConnection(url,"C##scott","tiger");
			con.setAutoCommit(false);
		}catch(ClassNotFoundException ce) {
			System.out.println("드라이버 로딩 실패"+ce.getMessage());
		}catch(SQLException se) {
			System.out.println("DB접속 실패"+se.getMessage());
		}
	}
	//영화추가
	public void addmovie() {
		CallableStatement cstmt1=null;	//저장 프로시저 사용
		PreparedStatement pstmt1=null;	//mbti와 영화제목을 조건으로 한 조회
		ResultSet rs1=null;
		String sql1="{call addmovie(mv_seq.nextval,?,?)}"; //번호는 Sequence사용
		String sql2="select * from movie where title=? and mbti=?";//중복추가 발생방지
		try {
			System.out.println("영화제목 입력:");
			String title=sc.nextLine();
			System.out.println("MBTI 입력:");
			String mbti=sc.nextLine();
			pstmt1=con.prepareStatement(sql2);
			pstmt1.setString(1, title);
			pstmt1.setString(2, mbti);
			rs1=pstmt1.executeQuery();
			//이미 존재하는 데이터가 있다면 => 중복입력방지
			if(rs1.next()) {
				System.out.println("이미 존재하는 데이터입니다.");
			}else {	//중복 데이터가 없다면 추가해줌
				cstmt1=con.prepareCall(sql1);				
				cstmt1.setString(1, title);
				cstmt1.setString(2, mbti);
				cstmt1.execute();
				System.out.println("["+title+"]을(를) "+mbti+"에 저장하였습니다.");
			}
			con.commit(); //입력완료되면 commit
		}catch(SQLException se) {
			System.out.println("영화를 저장할 수 없습니다."+se.getMessage());
			try {
				con.rollback();
			}catch(SQLException s) {
				System.out.println(s.getMessage());
			}
		}finally {
			try {				
				if(rs1!=null) rs1.close();
				if(cstmt1!=null) cstmt1.close();
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}	
	}
	//퀴즈 문항보기
	public void allquiz() throws SQLException {
		Statement stmt1=null; //union으로 테이블 합친 뷰 조회
		ResultSet rs1=null;
		String sql1="select * from allquiz";
		try {
			stmt1=con.createStatement();
			rs1=stmt1.executeQuery(sql1);
			while(rs1.next()) {
				int num=rs1.getInt("num");
				String quiz=rs1.getString("quiz");
				System.out.println("["+num+"]"+quiz);
			}
		}finally {
			try {
				if(rs1!=null) rs1.close();
				if(stmt1!=null) stmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}	
	}
}
//비율보기, 영화추가(프로시저 써보자)
public class Manager {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Rate r = new Rate();
		Mmenu m=new Mmenu();
		Contents c = new Contents();
		try {
			while(true) {
				System.out.println("1.현재 mbti별 회원비율보기 2.영화추가 3.퀴즈 문항보기 0.종료");
				//개행문자(엔터키)가 다음 nextLine에 입력되기 때문에 Integer 써줌
				int n = Integer.parseInt(sc.nextLine());
				switch(n) {
				case 0: System.out.println("프로그램을 종료합니다."); c.close(); System.exit(0);
				case 1: r.listRate(); break; //회원 메뉴에 있는 비율보기메소드임
				case 2: m.addmovie(); break; 
				case 3: m.allquiz(); break;
				}
			}
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}
	}
}
