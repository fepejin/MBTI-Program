package data2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Rate {
	Connection con=null;
	public Rate() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url="jdbc:oracle:thin:@localhost:1521:xe";
			con=DriverManager.getConnection(url,"C##scott","tiger");
		}catch(ClassNotFoundException ce) {
			System.out.println(ce.getMessage());
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}
	}
	//mbti별 비율갱신 메소드
	public void per() {
		PreparedStatement pstmt1=null;// mbti별 비율 업데이트
		Statement stmt1=null;	//전체 회원수 조회(조건X,Statement사용)
		Statement stmt2=null;	// mbti당 회원수 조회(조건X,Statement사용)
		ResultSet rs1=null;
		ResultSet rs2=null;
		Per p = new Per();	//퍼센트 클래스

		try {
			//전체회원수
			stmt1=con.createStatement();
			String sql="select count(*) from mems";
			stmt1.executeUpdate(sql);
			rs1=stmt1.executeQuery(sql);
			//mbti별 회원수 카운트
			stmt2=con.createStatement();
			//mbti가 존재하지 않으면 'NULL'로 대체
			String sql1="select nvl(mbti,'NULL'),count(*) from mems group by mbti";	
			rs2=stmt2.executeQuery(sql1);
			//비율 구하기
			//n=전체 회원수. 한번만 출력하면 되니까 if문을 써줌
			if(rs1.next()) {
				int n = rs1.getInt("count(*)");	
				while(rs2.next()) {
					//n2=mbti별 회원수
					int n2=rs2.getInt("count(*)");
					String gmbti=rs2.getString("nvl(mbti,'NULL')");
					//비율구하기 메소드
					double rate=p.setPer(n2,n);
					//비율 업데이트
					String sql2="update madmin set rate=? where mbti=?"; 	
					pstmt1=con.prepareStatement(sql2);
					pstmt1.setDouble(1, rate);
					pstmt1.setString(2, gmbti);	
					pstmt1.executeUpdate();
				}
			}
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}
	}
	//비율 보기. 조건없이 조회만을 하기 때문에 Statement 써줌
	public void listRate() throws SQLException{
		Statement stmt1=null; //비율 높은 순으로 조회기능
		Statement stmt2=null; //회원수 조회기능
		ResultSet rs1=null;
		ResultSet rs2=null;
		String sql1="select mbti,rate from madmin order by rate desc"; //비율 내림차순으로 조회
		String sql2="select count(*) from mems"; //mems 행 개수 카운트
		try {
			stmt1=con.createStatement();
			stmt2=con.createStatement();
			rs1=stmt1.executeQuery(sql1);
			rs2=stmt2.executeQuery(sql2);
			if(rs2.next()) {System.out.println("<<회원"+rs2.getInt("count(*)")+"명 mbti비율>>");}
			while(rs1.next()) {
				String mbti=rs1.getString("mbti");
				Double rate=rs1.getDouble("rate");
				System.out.println(mbti+": "+rate+"%");
			}
		}finally {
			try {
				if(stmt2!=null)stmt2.close();		
				if(rs1!=null)rs1.close();
				if(stmt1!=null)stmt1.close();				
			}catch(SQLException se){
				System.out.println(se.getMessage());
			}
		}
	}
}
