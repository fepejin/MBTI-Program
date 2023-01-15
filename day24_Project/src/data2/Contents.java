package data2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Contents {
	Connection con=null;
	Scanner sc = new Scanner(System.in);
	Studyroom s = new Studyroom();
	public Contents() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url="jdbc:oracle:thin:@localhost:1521:xe";
			con=DriverManager.getConnection(url,"C##scott","tiger");
		}catch(ClassNotFoundException ce) {
			System.out.println("드라이버 로딩 실패"+ce.getMessage());
		}catch(SQLException se) {
			System.out.println("DB접속 실패"+se.getMessage());
		}
	}
	//친구매칭
	public void matching(String nick) throws SQLException {
		PreparedStatement pstmt1=null;	//매칭mbti
		PreparedStatement pstmt2=null;	//매칭mbti의 데이터
		ResultSet rs1=null;
		ResultSet rs2=null;
		int cnt=0;
		//inner join이용해서 잘맞는 mbti 조회
		String sql1="select a.matchmbti from mems m inner join madmin a "+
					"on m.mbti=a.mbti where m.nick=?";
		//매칭된 mbti의 닉,성별 조회
		String sql2="select nick,gender from mems where mbti=?";
		try {
			//매칭
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1, nick);
			rs1=pstmt1.executeQuery();
			if(rs1.next()) {
				String matchmbti=rs1.getString("matchmbti"); //매칭되는 mbti
				//매치mbti를 가진 닉,성별 조회
				pstmt2=con.prepareStatement(sql2);
				pstmt2.setString(1, matchmbti);
				rs2=pstmt2.executeQuery();
				while(rs2.next()) {
					String nick2 = rs2.getString("nick"); //닉
					String gender = rs2.getString("gender"); //성별
					System.out.println(nick2+"("+gender+")");
					cnt++;	//데이터가 존재할 때마다 cnt 증가
				}
				System.out.println(cnt+"명이 매칭되었습니다."); 
			}
		}finally {
			try {
				if(rs2!=null) rs2.close();
				if(pstmt2!=null) pstmt2.close();
				if(rs1!=null) rs1.close();
				if(pstmt1!=null) pstmt1.close();	
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//영화추천
	public void recommendmv(String nick) throws SQLException {
		PreparedStatement pstmt1=null;	//mbti에 해당되는 영화 추천
		ResultSet rs1=null;
		//sql 조인 사용하여 nick을 조건으로 mbti와 영화제목 조회
		String sql1="select m.mbti, mv.title "
				+ "from mems m, movie mv "
				+ "where m.mbti=mv.mbti and m.nick=?";
		try {
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1, nick);
			rs1=pstmt1.executeQuery();
			System.out.println("<<"+nick+"님께 추천드리는 영화>>");
			while(rs1.next()) {
				String title=rs1.getString("title");
				System.out.print(title+" ||");//"||"으로 구분하고 조회되는 영화제목 나열
			}
			System.out.println(" ");
		}finally {
			try {
				if(rs1!=null) rs1.close();
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//스터디룸 가입한 방이 존재/존재X
	public void studyroom(String nick) throws SQLException {
		PreparedStatement pstmt1=null;
		ResultSet rs1=null;
		//inner join으로 방번호, 방이름 조회
		String sql1="select m.rnum,s.rname from mems m inner join studyroom s "+
					"on m.rnum=s.rnum where m.nick=?";
		try {
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1, nick);
			rs1=pstmt1.executeQuery();
			while(true) {
				if(rs1.next()) {//스터디룸이 존재하면 실행될 코드
					int rnum=rs1.getInt("rnum");
					String rname=rs1.getString("rname");
					System.out.println("현재 소속된 스터디룸(번호): "+rname+"("+rnum+")");
					System.out.println("스터디룸 메뉴를 선택하세요");
					System.out.println("1.입장 2.탈퇴 3.다른컨텐츠 이용하기");
					int n =sc.nextInt();
					if(n==1) { 
						System.out.println("방에 입장하셨습니다.(퇴장:0)");
						int exit=sc.nextInt();
						if(exit==0) return;
					}else if(n==2) {
						s.delete(nick,rnum); //탈퇴: studyroom의 delete 메소드 실행
					}else if(n==3) {
						return;
					}else {
						System.out.println("번호를 잘못 입력하였습니다.");
						return;
					}
				}else { //스터디룸이 존재하지 않으면 실행될 코드
					System.out.println("현재 소속된 스터디룸이 없습니다.");
					System.out.println("스터디룸 메뉴를 선택하세요");
					System.out.println("1.개설 2.가입 3.검색 4.리스트 5.다른컨텐츠 이용하기");
					int n =sc.nextInt();
					switch(n) {
					case 1: s.createroom(nick); return;	//개설: studyroom의 createroom메소드 실행
					case 2: s.addroom(nick); return;	//가입: studyroom의 addroom메소드 실행
					case 3: s.serchroom(); break;		//검색: studyroom의 serchroom메소드 실행
					case 4: s.listroom(); break;		//리스트: studyroom의 listroom메소드 실행
					case 5: return;	//메소드 빠져나가기
					}
				}
			}
		}finally {
			try {
				if(rs1!=null) rs1.close();
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//연결끊기. 필요 없을 것 같지만 일단 써둠
	public void close() {
		try {
			if(con!=null) con.close();
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}
	}
}
