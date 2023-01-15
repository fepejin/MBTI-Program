package day24_프로젝트;

import java.util.Scanner;

import data2.Contents;
import data2.FindPw;
import data2.Members;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Members m = new Members();
		FindPw f = new FindPw();
		Contents c = new Contents();
		try {
			while(true) {
				System.out.println("1.로그인 2.회원가입 3.비밀번호 찾기 0.프로그램 종료");
				//nextInt를 하고 엔터(개행)을 하면 다음에 호출되는 scanner에 개행입력이 되기 때문에 Integer로 받음
				int n = Integer.parseInt(sc.nextLine()); 	
				switch(n) {
				case 0: System.out.println("프로그램을 종료합니다."); c.close(); System.exit(0);
				case 1: m.login(); break;
				case 2: m.create(); break;
				case 3: f.findpw(); break;
				}
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
