import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    one_time_enrollment: {
      executor: 'per-vu-iterations',
      vus: 30000,       // 동시 3만명
      iterations: 1,    // 각 VU는 1번만 요청
      maxDuration: '10m',
    },
  },
};

export default function () {
  const url = 'http://portfolio-alb-1496820650.ap-northeast-2.elb.amazonaws.com/';

  // timeout 120초 적용
  let res = http.get(url, { timeout: '120s' });

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
