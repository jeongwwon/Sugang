import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    one_time_enrollment: {
      executor: 'per-vu-iterations',
      vus: 1000,         // 10만명 동시 접속
      iterations: 1      // 각 VU는 1번만 실행
    },
  },
};

export default function () {
  const studentId = __VU; // 가상 사용자 번호 (1~100)
  const lectureId = 1;

  // Redis 기반 수강신청 API 호출
  const url = `http://localhost:8080/enrollment/redis/${studentId}/${lectureId}`;
  let res = http.post(url);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
