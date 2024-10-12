/*
 * L*L 크기의 체스판, (1,1) 시작
 * 빈칸 0 / 함정 1 / 벽 2
 * 
 *[1번 기사 ~ N번 기사]
 * 기사 처음위치 (r,c) ~ h/w만큼 직사각형
 * 기사 체력 k
 * 
 * 1. 기사 상하좌우 1칸 이동
 * if) 다음칸에 다른 기사 있다면
 * 		=> 연쇄적으로 1칸씩 밀림
 * if) 그 방향 끝에 벽이 있다면
 * 		=> 모든 기사 이동 X
 * if) 체스판에 사라진 기사에게 명령 내리면 => 반응 X
 * 
 * 2. 명령받은 기사가 다른기사를 밀면, 밀린 기사들은 피해 입음
 * 		피해량 == 이동한 곳에서 h*w 직사각형 내 함정수
 * 		피해받으면 -> 대미지 받음
 * 		체력 0 이하라면 -> 사라짐
 * 		밀렸더라도, 함정 없으면 피해 X
 * 
 * Q개의 명령 모두 끝난 후, 생존한 기사들이 총받은 대미지 합 출력
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
	
	static class Knight {
		int r, c, h, w, k, totalDemage;
		boolean isExist, isDemaged;
		
		public Knight(int r, int c, int h, int w, int k) {
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			this.totalDemage = 0;
			this.isExist = true;
			this.isDemaged = false;
		}
	}
	
	static int L, N, Q;
	static int[][] map;
	static int[][] knightMap;
	static Knight[] knight;
	static int dx[] = {-1, 0, 1, 0};	// 위 오 아 왼
	static int dy[] = {0, 1, 0, -1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		
		st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());	// 맵 크기
		N = Integer.parseInt(st.nextToken());	// 기사 수
		Q = Integer.parseInt(st.nextToken());	// 명령 수
		
		// 체스판
		map = new int[L][L];
		for (int i=0; i<L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

//		// map 확인용
//		for (int i=0; i<L; i++) {
//			for (int j=0; j<L; j++) {
//				System.out.print(map[i][j] + " ");
//			}System.out.println();
//		}
		
		// 기사
		knight = new Knight[N];		// 기사 & 기사명령 정보
		knightMap = new int[L][L];
		for (int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken())-1;
			int c = Integer.parseInt(st.nextToken())-1;
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			
			knight[i] = new Knight(r, c, h, w, k);
			
			for (int a=r; a<r+h; a++) {
				for (int b=c; b<c+w; b++) {
					knightMap[a][b] = i+1;	// 기사 번호 저장
				}
			}
		}
		
		// 명령
		for (int q=0; q<Q; q++) {
			// 명령 입력
			st = new StringTokenizer(br.readLine());
			int i = Integer.parseInt(st.nextToken()) - 1;	// (i+1)번 기사에게
			int d = Integer.parseInt(st.nextToken());	// d로 1칸 이동
			
			// 명령 받은 기사 이동
			if (!knight[i].isExist)	continue;	// 사라진 기사는 무반응
			if (!checkMovePossible(i, d))	continue;	// 갈 수 없다면 모두 못 감
			moveKnight(i, d);
			knight[i].isDemaged = false;	// 밀린 기사만 데미지 받음 (본인은 제외)
			
			// 데미지 처리
			getDemage();
		}
		
		// 생존한 기사들이 받은 총 데미지 수 출력
		int answer = 0;
		for (int i=0; i<N; i++) {
			if (knight[i].isExist) {
				answer += knight[i].totalDemage;				
			}
		}
		System.out.println(answer);
	}
	
	static boolean checkMovePossible(int i, int d) {
		for (int r = knight[i].r; r < knight[i].r + knight[i].h; r++) {
			for (int c = knight[i].c; c < knight[i].c + knight[i].w; c++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				// 밖 or 벽 -> 못감
				if (!inRange(nr, nc) || map[nr][nc] == 2) {
					return false;
				}
				
				// 빈곳 or 아군 -> 바로가면됨
				if (knightMap[nr][nc] == 0 || knightMap[nr][nc] == i+1) {
					continue;
				}
				
				// 이동하려는 곳에 다른 기사가 있다면, 밀려남
				// 만약 밀린 기사들중 하나라도 이동못하면 바로 false
				if (!checkMovePossible(knightMap[nr][nc]-1, d)) {
					return false;
				}
			}
		}
		return true;
	}
	
	static void moveKnight(int i, int d) {
		for (int r = knight[i].r; r < knight[i].r + knight[i].h; r++) {
			for (int c = knight[i].c; c < knight[i].c + knight[i].w; c++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				// 빈곳 or 아군 -> 바로가면됨
				if (knightMap[nr][nc] == 0 || knightMap[nr][nc] == i+1) {
					continue;
				}
				
				// 밀기
				moveKnight(knightMap[nr][nc]-1, d);
			}
		}
		
		// 기사 직사각형 이동
		switch(d) {
		case 0:
			moveUp(i, d);
			break;
		case 1:
			moveRight(i, d);
			break;
		case 2:
			moveDown(i, d);
			break;
		case 3:
			moveLeft(i, d);
			break;
		}
		
		knight[i].isDemaged = true;
		
//		// knightMap 확인용
//		System.out.println("미는중...");
//		for (int z=0; z<L; z++) {
//			for (int y=0; y<L; y++) {
//				System.out.print(knightMap[z][y] + " ");
//			}System.out.println();
//		}
	}
	
	static void moveUp(int i, int d) {
		for (int r = knight[i].r; r < knight[i].r + knight[i].h; r++) {
			for (int c = knight[i].c; c < knight[i].c + knight[i].w; c++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				knightMap[nr][nc] = i+1;
				knightMap[r][c] = 0;
			}
		}
		knight[i].r += dx[d];
		knight[i].c += dy[d];
	}
	
	static void moveRight(int i, int d) {
        for (int c = knight[i].c + knight[i].w - 1; c >= knight[i].c; c--) {
            for (int r = knight[i].r; r < knight[i].r + knight[i].h; r++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				knightMap[nr][nc] = i+1;
				knightMap[r][c] = 0;
			}
		}
		knight[i].r += dx[d];
		knight[i].c += dy[d];
	}
	
	static void moveDown(int i, int d) {
		for (int r = knight[i].r + knight[i].h - 1; r >= knight[i].r; r--) {
			for (int c = knight[i].c; c < knight[i].c + knight[i].w; c++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				knightMap[nr][nc] = i+1;
				knightMap[r][c] = 0;
			}
		}
		knight[i].r += dx[d];
		knight[i].c += dy[d];
	}
	
	static void moveLeft(int i, int d) {
        for (int c = knight[i].c; c < knight[i].c + knight[i].w; c++) {
            for (int r = knight[i].r; r < knight[i].r + knight[i].h; r++) {
				int nr = r + dx[d];
				int nc = c + dy[d];
				
				knightMap[nr][nc] = i+1;
				knightMap[r][c] = 0;
			}
		}
		knight[i].r += dx[d];
		knight[i].c += dy[d];
	}
	
	static void getDemage() {
		for (int i=0; i<N; i++) {
			if (!knight[i].isExist)	continue;
			if (!knight[i].isDemaged)	continue;
			
			int demage = calcDamageInRect(i);
			knight[i].k -= demage;
			knight[i].totalDemage += demage;

			// 체스판에서 사라짐
			if (knight[i].k <= 0) {
				knight[i].isExist = false;

				for (int r=knight[i].r; r<knight[i].r + knight[i].h; r++) {
					for (int c=knight[i].c; c<knight[i].c + knight[i].w; c++) {
						knightMap[r][c] = 0;
					}
				}				
			}
			knight[i].isDemaged = false;
		}
	}
	
	static int calcDamageInRect(int i) {
		int demage = 0;
		for (int r=knight[i].r; r<knight[i].r + knight[i].h; r++) {
			for (int c=knight[i].c; c<knight[i].c + knight[i].w; c++) {
				
				if (!inRange(r, c) || map[r][c] == 2)	continue;
				
				if (map[r][c] == 1) {
					demage++;
				}
			}
		}
		
		return demage;
	}
	
	static boolean inRange(int x, int y) {
		return 0<=x && x<L && 0<=y && y<L;
	}
}