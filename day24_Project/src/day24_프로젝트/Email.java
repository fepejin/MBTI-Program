package day24_프로젝트;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Email {
	public static void main(String[] args) {
		Connection con=null;
		PreparedStatement pstmt1=null;	//서버로부터 받은 메일주소를 조건으로 하고 조회
		ResultSet rs1=null;
		Socket socket = null;
		Scanner sc = new Scanner(System.in);
		String sql1="select * from mems where email=?";
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String url="jdbc:oracle:thin:@localhost:1521:xe";
			con=DriverManager.getConnection(url,"C##scott","tiger");
			System.out.println("메일을 입력하세요");
			String email=sc.next(); //메일주소를 입력하면 서버와 연결
			socket=new Socket("localhost",3000);
			System.out.println("서버에 연결되었습니다.");
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1, email);	//입력한 이메일을 조건으로 조회
			rs1=pstmt1.executeQuery();
			
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String[]s=br.readLine().split(","); //이메일,인증번호를 ","를 기준으로 split해줌
			//로그인한 이메일과 같으면
			if(rs1.next()) {
				if(email.equals(s[0])) {
					System.out.println("인증번호: "+s[1]);
				}else {	//같지 않으면 인증번호 X
					System.out.println("도착한 인증번호가 없습니다.");
					return;
				}
			}else {
				System.out.println("존재하지 않는 메일주소입니다.");
				return;
			}
			br.close();
			is.close();
		}catch(ClassNotFoundException ce) {
			System.out.println("드라이버 로딩 실패"+ce.getMessage());
		}catch(SQLException se) {
			System.out.println("DB접속 실패"+se.getMessage());
		}catch(IOException ie) {
			System.out.println("서버와 연결이 끊어졌습니다.");
		}catch(NullPointerException ne){
			System.out.println("서버와 연결이 끊어졌습니다.");
		}finally {
			try {
				socket.close();
			}catch(IOException ie) {
				System.out.println(ie.getMessage());
			}
		}
	}
}
