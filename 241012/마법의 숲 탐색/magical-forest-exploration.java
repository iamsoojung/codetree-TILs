/*
 * R행 C열 마법의 숲
 * 정령들은 위(북쪽)를 통해서만 숲 들어옴
 * K명의 정령 - 각자 골렘타고 숲 탐색
 * 		골렘 => 십자 모양 구조 (중앙 포함 5칸, 중앙 제외 4칸 중 1칸은 출구)
 * 정령들은 어느 방향에서든 골렘 탑승 가능 but 내릴땐 정해진 출구만 가능
 * 
 * i번째로 숲 탐색하는 골렘 - 가장 북쪽부터 시작해 중앙이 c(i)열 위치로 내려오기 시작함
 * 						초기 골렘의 출구는 d(i) 방향
 * 
 * 1) (남)아래로 한칸씩
 * 2) (서)왼쪽으로 한칸씩 (출구가 반시계방향으로 이동)
 * 3) (동)오른쪽으로 한칸씩 (출구가 시계방향으로 이동)
 * 4) (가장 아래(남)에 도달해서 더이상 이동 불가 시,) 정령은 골렘 내 상하좌우 인접한 칸으로 이동 가능
 * 		(단, 현재 위치하고 잇는 골렘 출구가 다른 골렘과 인접하다면, 다른 골렘으로 이동 가능)
 * => 갈 수 있는 칸 중 가장 아래로 이동하고 종료.
 * if) 최대한 아래이지만, 일부가 밖이라면, 초기화하고 다음골렘부터 새로 시작
 * 		(단, 이경우 정렬이 도달하는 최종 위치는 답 포함 X))
 * 		(행의 총합은 누적되긴 함)
 * 각 정령들이 최종적으로 위치한 행의 총합 구하기****
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static int R, C, K;
	static int[][] map;
	static int answer = 0;
	static int[] dx = {-1, 0, 1, 0};	// 북 동 남 서 (시계방향)
	static int[] dy = {0, 1, 0, -1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		
		st = new StringTokenizer(br.readLine());
		R = Integer.parseInt(st.nextToken());	// 숲 크기 R
		C = Integer.parseInt(st.nextToken());	// 숲 크기 C
		K = Integer.parseInt(st.nextToken());	// 정령 수 K
		
		map = new int[R][C];
		for (int i=1; i<K+1; i++) {		// i번째 골렘
			st = new StringTokenizer(br.readLine());
			int c = Integer.parseInt(st.nextToken()) - 1;	// 각 골렘이 출발하는 열 번째
			int d = Integer.parseInt(st.nextToken());	// 골렘의 출구 방향
			
			// 골렘 이동
			int moveResult[] = move(i, c, d);

			// 숲 밖인지 확인 (안 = 정령 이동 / 밖 = 숲 초기화)
			boolean isPossible = moveResult[2] == 1;
			if (isPossible) {	// 잘 이동 가능 : 정령 이동
				answer += bfs(moveResult[0], moveResult[1]);	// (idx+1)번째 행
			} else {	// 맵밖으로 나옴 : 맵 초기화
				map = new int[R][C];
			}
		}
		System.out.println(answer);
	}
	
	static int[] move(int idx, int c, int d) {
		// 골렘 시작 중앙 위치
		int x = -2;		// 0에서 두칸 위부터 시작
		int y = c;
		
		while(true) {
			// 밑
			if (check(x+2, y) && check(x+1, y-1) && check(x+1, y+1)) {
				x += 1;
			// 왼
			} else if (check(x+1, y-1) && check(x+1, y-2) && check(x+2, y-1)
					&& check(x, y-2) && check(x-1, y-1)) {
				x += 1;
				y -= 1;
				d = (d+3) % 4;	// 출구 반시계 방향 이동
			// 오
			} else if (check(x+1, y+1) && check(x+1, y+2) && check(x+2, y+1)
					&& check(x, y+2) && check(x-1, y+1)) {
				x += 1;
				y += 1;
				d = (d+1) % 4; // 출구 시계 방향 이동
			} else {	// 골렘이 벽 부셔버리고 나온 경우
				break;
			}
		}
		
		if (!inRange(x, y) || !inRange(x-1, y) || !inRange(x+1, y) || !inRange(x, y-1) || !inRange(x, y+1)) {
			return new int[] {-1, -1, 0};	// 골렘이 밖으로 가버림 ㅠ (3번째인자 : 못감)
		} else {
			map[x][y] = idx;
			map[x-1][y] = idx;
			map[x+1][y] = idx;
			map[x][y-1] = idx;
			map[x][y+1] = idx;
			
			// 출구 찾기
			int exitX = x + dx[d];
			int exitY = y + dy[d];

			map[exitX][exitY] = -idx;	// 출구번호 음수처리
			return new int[] {x, y, 1};		// {중앙 위치, 감}
		}
	}
	
	static boolean check(int x, int y) {    // 다음칸 갈 수 있는건지
        if (!inRange(x, y)) {    // 맵 밖            
            return x < R && y >= 0 && y < C;    // 바닥, 양옆 뚫을 수 X
        } else {    // 맵 안
            return map[x][y] == 0;    // 비어있어야함
        }
    }
	
	static int bfs(int x, int y) {    // 정령 이동 (4방탐색)
        ArrayList<Integer> result = new ArrayList<>();    // 도달 가능한 행 저장
        
        Queue<int[]> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[R][C];
        
        q.add(new int[] {x, y});
        visited[x][y] = true;
        result.add(x);
        
        while(!q.isEmpty()) {
            int[] cur = q.poll();
            
            for (int d=0; d<4; d++) {
                int nx = cur[0] + dx[d];
                int ny = cur[1] + dy[d];
                
                // 맵 밖 or 이미 방문 or 골렘이랑연결X
                if (!inRange(nx, ny) || visited[nx][ny] || map[nx][ny] == 0) {
                    continue;
                }
                
                // 같은 칸으로 움직이거나 or 지금위치 출구라서 다른 칸으로 이동하거나
                if ((Math.abs(map[cur[0]][cur[1]]) == Math.abs(map[nx][ny]))
                        || (map[cur[0]][cur[1]] < 0 && Math.abs(map[cur[0]][cur[1]]) != Math.abs(map[nx][ny]))) {
                    q.add(new int[] {nx, ny});
                    visited[nx][ny] = true;
                    result.add(nx);
                }
            }
        }
        
        // 정령이 도달 가능한 행 중에 제일 큰 값
        Collections.sort(result, Collections.reverseOrder());
        return result.get(0) + 1;  // 0-based to 1-based    
    }
	
	static boolean inRange(int x, int y) {
		return 0<=x && x<R && 0<=y && y<C;
	}
}