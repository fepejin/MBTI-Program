package data2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class FindPw{
	Scanner sc = new Scanner(System.in);
	Random r=new Random();	//인증번호생성 객체
	Connection con=null;
	Socket socket;
	ServerSocket server=null;

	public FindPw() {
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
	public void findpw() {
		Members m = new Members();
		PreparedStatement pstmt1=null;
		ResultSet rs1=null;
		String sql1="select * from mems where id=?";
		try {
			server=new ServerSocket(3000);
			System.out.println("서버실행. 메일 접속해주세요");
			socket=server.accept();
			System.out.println("메일접속완료"); //클라이언트에서 메일을 입력하면(로그인) 접속됨
			System.out.println("아이디를 입력하세요.");
			String id=sc.nextLine();
			System.out.println("이메일을 입력하세요.");
			String email=sc.nextLine();
			PrintWriter pw=new PrintWriter(socket.getOutputStream());
			pstmt1=con.prepareStatement(sql1);
			pstmt1.setString(1, id);
			rs1=pstmt1.executeQuery();
			if(rs1.next()) {
				String mail=rs1.getString("email");
				if(mail.equals(email)) {
					//1000~9999까지 네자리 인증번호가 랜덤으로 나오도록 함
					int num=r.nextInt(8999)+1001; 
					pw.println(mail+","+num); //소켓을 통해 클라이언트로 보냄
					pw.flush();
					for(int i=1;i<6;i++) {
						System.out.println("메일로 보낸 인증번호를 입력하십시오.(5회시도/0.종료)");
						int n=sc.nextInt();
						if(n==0) System.exit(0);
						if(num==n) {
							String pwd=rs1.getString("pw");
							System.out.println("회원님의 비밀번호는 "+pwd+"입니다.");
							System.out.println("로그인화면으로 갑니다.");
							m.login();
						}else {
							System.out.println("인증번호가 일치하지 않습니다.");
							System.out.println("인증시도==>"+i+"회시도");
							}
					}System.out.println("시도횟수가 초과하였습니다.프로그램을 종료합니다.");
					System.exit(0);
				}else {
					System.out.println("메일주소가 일치하지 않습니다.");
					return;
				}
			}else {
				System.out.println("존재하지 않는 회원정보입니다.");
				return;
			}
			pw.close();
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}catch(IOException ie) {
			System.out.println(ie.getMessage());
		}finally {
			try {
				socket.close();
				server.close();
			}catch(IOException ie) {
				System.out.println(ie.getMessage());
			}
			
		}
//		System.out.println("메일로 전달된 인증번호를 입력하세요.");
//		int num=sc.nextInt();
	}
}
	
//이메일 입력하면 server 열림 -> client로 랜덤 숫자 보내서 server에서 다시 입력하면 비밀번호 알려줌
//public class FindPw{
//	public static void main(String[] args) {
//		Find f =new Find();
//		f.findpw();
//	}
//}
