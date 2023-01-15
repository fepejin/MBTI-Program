package data2;

//테스트한 회원 대비 mbti 분포 구하기
class Per implements Comparable<Per>{
	private String mbti;
	private double per;
	
	public Per() {}
	
	public Per(String mbti,double per) {
		this.mbti=mbti;
		this.per=per;
	}
	public String getMbti() {
		return mbti;
	}
	public double setPer(int cnt,int size) {
		//Math.round(): 소수점 첫째자리에서 반올림
		//소수점 두자리만 출력 => 결과값에 100을 곱해서 반올림을 해준 뒤 다시 100.0으로 나눔
		double per = (double)cnt/size*100;
		double per2 = Math.round(per*100)/100.0;
		return per2;
	}
	public double getPer() {
		return per;
	}
	@Override
	public String toString() {
		return getMbti()+"의 비율: "+getPer()+"%";
	}
	@Override
	public int compareTo(Per o) {
		if(per>o.per) {
			return -1;
		}else if(per<o.per) {
			return 1;
		}else {
			return 0;
		}
	}
}
