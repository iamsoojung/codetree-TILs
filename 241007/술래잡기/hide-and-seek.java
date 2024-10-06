import java.io.*;
import java.util.*;

/**
 * n*n 격자 (n 홀수)
 * m명의 도망자 (상하(2) - 아래쪽시작 / 좌우(1) - 오른쪽시작)
 * h개의 나무 (도망자랑 겹치기 ㄱㄴ)
 * 순서 => 도 - 술 - ... (도망자는 술래와 거리 3이하 필수)
 * 도둑 : 범위밖X, 술래있으면X, 나무O
 * 술래 : 위부터 달팽이 모양, 끝나면 거꾸로, 1칸씩
 * 술래의 시야 : 바라보는 방향으로, 현재 포함 3칸, 나무에 가려짐
 * 점수 : t번째 턴 * 잡힌 수
 * k번 술래잡기 동안의 점수 !!
 */

public class Main {
	
	static int N, M, H, K;
	static boolean[][] map;		// 나무 있으면 T
	static int[][][] thieves;	// 도둑(x, y, d) 배열
	static int policeX = 0, policeY = 0, policeD = 0;	// 술래(x, y, d)
	static boolean[][] isVisited;
	static boolean isReverse = false;	// 회전필요여부
	static int turn = 0, score = 0;

	static int[] dx = {-1, 0, 1, 0};	// 시계방향
	static int[] dy = {0, 1, 0, -1};
	
	public static void main(String args[]) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();
		
		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());	// n*n 맵
		M = Integer.parseInt(st.nextToken());	// 도망자
		H = Integer.parseInt(st.nextToken());	// 나무
		K = Integer.parseInt(st.nextToken());	// k번의 턴
		
		map = new boolean[N][N];
		thieves = new int[N][N][4];
		
		policeX = N/2;	// 술래는 정중앙
		policeY = N/2;
		
		isVisited = new boolean[N][N];
		isVisited[policeX][policeY] = true;
		
		for (int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			int d = Integer.parseInt(st.nextToken());
			thieves[x][y][d] += 1;
		}
		
		for (int i=0; i<H; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			map[x][y] = true;
		}
		// end of input
		
		while (++turn <= K) {	// K턴까지만
			// 도망자 이동
			moveThieves();
			
			// 술래 이동
			movePolice();
			
			// 술래잡기
			catchThieves();
			
		}
		
		System.out.println(score);
	}
	
	static void moveThieves() {
		int[][][] copyMap = new int[N][N][4];
		
		for (int x=0; x<N; x++) {
			for (int y=0; y<N; y++) {
				// 술래와의 거리 3 이하 아니면 그대로 냅둠
				if (Math.abs(x - policeX) + Math.abs(y - policeY) > 3) {
					copyMap[x][y][0] += thieves[x][y][0];
					copyMap[x][y][1] += thieves[x][y][1];
					copyMap[x][y][2] += thieves[x][y][2];
					copyMap[x][y][3] += thieves[x][y][3];
				} else {
					for (int d=0; d<4; d++) {
						if (thieves[x][y][d] == 0)	continue;	// 도망자 없으면 패스

						int nd = d;
						int nx = x + dx[nd];		// 다음 위치
						int ny = y + dy[nd];
						
						if (!inRange(nx, ny)) {		// 다음 위치 벽이면 방향 바꾸고 다시 움직임
							nd = (nd+2) % 4;
							nx = x + dx[nd];
							ny = y + dy[nd];
						}
						
						if (nx == policeX && ny == policeY) {	// 다음 위치 경찰이면 못 움직임
							copyMap[x][y][nd] += thieves[x][y][d];
						} else {
							copyMap[nx][ny][nd] += thieves[x][y][d];
						}
					}
				}
			}
		}
		thieves = copyMap;
	}
	
	static void movePolice() {
		policeX += dx[policeD];
		policeY += dy[policeD];
		isVisited[policeX][policeY] = true;
		
		int nd = isReverse ? policeD : (policeD+1) % 4;		// 유지 : 시계방향 한칸더
		int nx = policeX + dx[nd];
		int ny = policeY + dy[nd];
		
		if (isReverse) {	// 유지 했는데
			if (!inRange(nx, ny) || isVisited[nx][ny]) {	// 범위밖 | 이미방문함
				policeD = (policeD+3) % 4;	// 반시계방향으로 변경
			}
		} else {	// 시계방향 갔는데
			if (inRange(nx, ny) && !isVisited[nx][ny]) {	// 범위내 & 아무도없으면
				policeD = nd;	// 그거 맞음
			}
		}
		
		// 맨위 or 중앙
		if ((policeX == 0 && policeY == 0) || (policeX == N/2 && policeY == N/2)) {
			isVisited = new boolean[N][N];	
			isVisited[policeX][policeY] = true;
			
			isReverse = !isReverse;
			policeD = isReverse ? 2 : 0;	// true 아래 : false 위
		}
	}
	
	static void catchThieves() {
		int cnt = 0;
		for (int i=0; i<=2; i++) {
			int nx = policeX + dx[policeD] * i;
			int ny = policeY + dy[policeD] * i;
			
			if (!inRange(nx, ny))	break;	// 범위밖이면 끝
			if (map[nx][ny])	continue;	// 나무 있으면 넘김
			
			for (int d=0; d<4; d++) {
				cnt += thieves[nx][ny][d];
				thieves[nx][ny][d] = 0;
			}
		}
		
		if (cnt > 0) {
			M -= cnt;
			score += cnt * turn;
		}
	}
	
	static boolean inRange(int x, int y) {
		return 0<=x && x<N && 0<=y && y<N;
	}
}