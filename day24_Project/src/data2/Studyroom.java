	package data2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

//탈퇴, 개설, 검색, 리스트 메소드
public class Studyroom {
	Connection con=null;
	Scanner sc = new Scanner(System.in);
	public Studyroom() {
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
	//개설
	public void createroom(String nick) {
		CallableStatement cstmt1 = null;	//프로시져 사용해서 방 개설
		PreparedStatement pstmt1 = null;	//닉네임으로 스터디룸 정보조회
		PreparedStatement pstmt2 = null;	//mems테이블에 rnum 갱신
		ResultSet rs1=null;
		String sql1="{call createsr(sr_seq.nextval,?,?)}";	//studyroom 테이블에 방개설 프로시저 
		String sql2="select * from studyroom where admin=?";//파라미터 닉네임으로 스터디룸 조회
		String sql3="update mems set rnum=? where nick=?";	//mems 테이블에 rnum 갱신
		try {
			cstmt1=con.prepareCall(sql1);
			System.out.println("개설하려는 방의 이름을 작성하시오.");
			String rname=sc.nextLine();
			cstmt1.setString(1,rname);
			cstmt1.setString(2,nick);
			cstmt1.execute();
			System.out.println("스터디룸이 개설되었습니다.");
			//파라미터 nick으로 스터디룸 번호 조회
			pstmt1=con.prepareStatement(sql2);
			pstmt1.setString(1, nick);
			rs1=pstmt1.executeQuery();
			if(rs1.next()) { //스터디룸 존재하면 방번호 얻어와서 mems테이블에도 갱신
				int rnum=rs1.getInt("rnum");
				pstmt2=con.prepareStatement(sql3);
				pstmt2.setInt(1, rnum);
				pstmt2.setString(2, nick);
				pstmt2.executeUpdate();
				System.out.println("방에 관리자로 가입되었습니다.");
				//mems테이블 갱신까지 되면 commit.
				//Connection 객체 생성할때 commit(false)로 지정해주었다.
				con.commit(); 
			}
		}catch(SQLException se) {
			System.out.println("방을 개설할 수 없습니다.");
			try {
				con.rollback();	//롤백 -- 오류나면 롤백
			}catch(SQLException s) {
				System.out.println(s.getMessage());
			}
		}finally {
			try {
				if(pstmt2 != null) pstmt2.close();
				if(rs1 != null) rs1.close();				
				if(pstmt1 != null) pstmt1.close();
				if(cstmt1 != null) cstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//가입
	public void addroom(String nick) {
		PreparedStatement pstmt1 = null;	//방번호 조회
		PreparedStatement pstmt2=null; //파라미터 nick으로 조회하여 rnum갱신
		ResultSet rs1=null;
		String sql1="select * from studyroom where rnum=?";	//방번호 조회
		String sql2="update mems set rnum=? where nick=?";	//가입
		try {
			pstmt1=con.prepareStatement(sql1);
			while(true){
				System.out.println("가입하려는 방 번호를 입력하시오");
				int rnum=sc.nextInt();
				pstmt1.setInt(1,rnum);
				rs1=pstmt1.executeQuery();
				//방 번호가 존재하면 가입
				if(rs1.next()){
					String rname=rs1.getString("rname");
					pstmt2=con.prepareStatement(sql2);
					pstmt2.setInt(1,rnum);
					pstmt2.setString(2,nick);
					pstmt2.executeUpdate();
					System.out.println(rname+"에 가입하였습니다.");
					con.commit(); //mems테이블 갱신까지 하면 commit
					break;
				//존재하지 않으면 메뉴로 돌아감
				}else{ System.out.println(rnum+"번 방이 존재하지 않습니다."); break;}
			}
		}catch(SQLException se) {
			System.out.println("방에 가입할 수 없습니다.");
			try {
				con.rollback();	//롤백 
			}catch(SQLException s) {
				System.out.println(s.getMessage());
			}
		}finally {
			try {
				if(pstmt2!=null) pstmt2.close();
				if(rs1!=null) rs1.close();				
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//검색
	public void serchroom() {
		PreparedStatement pstmt1=null; //특정 키워드를 입력하면 조회해주는 기능
		ResultSet rs1=null;
		boolean find=false; //조건걸어주기 위한 boolean
		String sql1="select * from studyroom where rname like ?"; //like연산자 사용
		System.out.println("검색하실 키워드를 입력하시오.");
		String key=sc.nextLine();
		try {
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1,"%"+key+"%");//key가 포함된 문자열 조회
			rs1=pstmt1.executeQuery();
			System.out.println("<<\'"+key+"\'키워드가 들어간 스터디룸>>");
			while(rs1.next()) { //key포함된 방이름이 존재할때
				int rnum=rs1.getInt("rnum");
				String rname=rs1.getString("rname");
				String admin=rs1.getString("admin");
				System.out.println("방번호: "+rnum+", 방이름: "+rname+", 운영자: "+admin);
				find=true; //boolean값을 true 
			}//!find가 true. 즉,find가 false이면 검색결과가 없는 것임
			if(!find) {System.out.println("검색결과가 없습니다.");}
		}catch(SQLException se){
			System.out.println("검색결과가 없습니다.");
		}finally {
			try {
				if(rs1!=null) rs1.close();				
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
	//리스트
	public void listroom() throws SQLException {
		Statement stmt1=null; //조건없이 조회만 하니까 Statement사용
		ResultSet rs1=null;
		try {
			stmt1=con.createStatement();
			String sql1="select * from studyroom"; //studyroom에 있는 모든 데이터 불러옴
			rs1=stmt1.executeQuery(sql1);
			System.out.println("<<스터디룸 리스트>>");
			while(rs1.next()) { 
				int rnum=rs1.getInt("rnum");
				String rname=rs1.getString("rname");
				String admin=rs1.getString("admin");
				System.out.println("방번호: "+rnum+", 방이름: "+rname+", 운영자: "+admin);
			}
			System.out.println("===============리스트 불러오기 완료===============");
		}finally {
			try {
				if(rs1!=null) rs1.close();				
				if(stmt1!=null) stmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
		
	}
	//탈퇴
	public void delete(String nick,int rnum) throws SQLException {
		PreparedStatement pstmt1=null;	//방번호를 조건으로 스터디룸 조회기능
		PreparedStatement pstmt2=null;	//해당 방번호=>null로 업댓(탈퇴)
		PreparedStatement pstmt3=null;	//해당 방번호 전체데이터 삭제(방폭파)
		PreparedStatement pstmt4=null;	//닉네임을 조건으로 방번호=>null로 업댓
		ResultSet rs1=null;
		//해당 스터디룸 정보 얻어오고, 관리자 닉 추출
		String sql1="select * from studyroom where rnum=?";
		//방관리자인경우 mems의 rnum, studyroom데이터 둘다 삭제(sql2,3)
		//관리자가 아닌 경우 mems의 rnum만 삭제(sql2)
		String sql2="update mems set rnum=null where rnum=?";
		String sql3="delete from studyroom where rnum=?";
		String sql4="update mems set rnum=null where nick=?";
		try {
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setInt(1, rnum);
			rs1=pstmt1.executeQuery();
			if(rs1.next()) {
				//파라미터의 nick과 admin이 같으면 방 폭파
				if(nick.equals(rs1.getString("admin"))) {
					System.out.println("해당 방의 관리자입니다. 스터디룸을 삭제합니다.");
					//해당 방 멤버들의 rnum을 null로 바꾼 뒤
					pstmt2=con.prepareStatement(sql2);
					pstmt2.setInt(1, rnum);
					pstmt2.executeUpdate();
					//스터디룸 delete
					pstmt3=con.prepareStatement(sql3);
					pstmt3.setInt(1, rnum);
					pstmt3.executeUpdate();
					System.out.println("해당 방을 삭제했습니다.");
				//같지 않으면 해당 회원만 삭제
				}else {
					pstmt4=con.prepareStatement(sql4);
					pstmt4.setInt(1, rnum);
					pstmt4.executeUpdate();
					System.out.println("해당 스터디룸을 탈퇴했습니다.");
				}
				con.commit(); //모든 테이블이 갱신되면 commit
			}
		}catch(SQLException se) {
			System.out.println("삭제할 수 없습니다.");
			con.rollback();
		}finally {
			try {
				if(pstmt4!=null) pstmt4.close();
				if(pstmt3!=null) pstmt3.close();
				if(pstmt2!=null) pstmt2.close();
				if(rs1!=null) rs1.close();
				if(pstmt1!=null) pstmt1.close();
			}catch(SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
}