#!/bin/bash
# slave_init.sh (슬레이브 컨테이너 내부에서 실행될 스크립트)

MASTER_HOST="mysql-master"  # 마스터 서비스 이름 (Docker Compose)
MASTER_USER="replica"
MASTER_PASSWORD="replica1234" # 복제 계정 비밀번호
SLAVE_USER="root"
SLAVE_PASSWORD="root1234"

echo "1. 복제 중단 및 초기화"
# 기존 복제 상태를 모두 리셋
mysql -u$SLAVE_USER -p$SLAVE_PASSWORD -e "STOP REPLICA; RESET REPLICA ALL;"

echo "2. 마스터가 준비될 때까지 대기"
# 마스터가 완전히 부팅되어 접속 가능할 때까지 대기
until mysql -h $MASTER_HOST -u $MASTER_USER -p$MASTER_PASSWORD -e "SELECT 1" &>/dev/null; do
  echo "마스터($MASTER_HOST) 대기 중..."
  sleep 5
done

echo "3. 마스터에서 전체 데이터 덤프 및 복구"
# 마스터에서 모든 데이터베이스를 덤프하고, 슬레이브에 파이프로 바로 복원
# --all-databases 옵션을 사용하여 모든 데이터를 가져옵니다.
docker exec $MASTER_HOST /usr/bin/mysqldump --single-transaction --all-databases -u $MASTER_USER -p$MASTER_PASSWORD | \
mysql -u$SLAVE_USER -p$SLAVE_PASSWORD

if [ $? -ne 0 ]; then
    echo "데이터 덤프/복구 실패. 복제 초기화 중단."
    exit 1
fi

echo "4. 복제 시작 위치 설정 및 재시작"
# 마스터의 바이너리 로그 파일명과 위치를 확인 (mysqldump --master-data=2가 출력에 포함)
# 마스터의 복제 계정으로 접속하여 로그 위치를 가져옵니다.
MASTER_LOG_INFO=$(mysql -h $MASTER_HOST -u $MASTER_USER -p$MASTER_PASSWORD -e "SHOW MASTER STATUS\G" | grep 'File\|Position')
MASTER_LOG_FILE=$(echo "$MASTER_LOG_INFO" | grep 'File' | awk '{print $2}')
MASTER_LOG_POS=$(echo "$MASTER_LOG_INFO" | grep 'Position' | awk '{print $2}')

# 복제 설정 (CHANGE MASTER TO)
mysql -u$SLAVE_USER -p$SLAVE_PASSWORD -e "
    CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='$MASTER_HOST',
    SOURCE_USER='$MASTER_USER',
    SOURCE_PASSWORD='$MASTER_PASSWORD',
    SOURCE_LOG_FILE='$MASTER_LOG_FILE',
    SOURCE_LOG_POS=$MASTER_LOG_POS;
    START REPLICA;
"

echo "5. 복제 초기화 완료 및 시작"