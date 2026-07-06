#!/bin/bash
# EC2 초기 설정 스크립트 (최초 1회만 실행)
# 사용법: bash scripts/ec2-setup.sh <EC2_IP>

EC2_IP=$1
if [ -z "$EC2_IP" ]; then
  echo "사용법: bash scripts/ec2-setup.sh <EC2_IP>"
  exit 1
fi

echo ">>> SSH 키 생성 (이미 있으면 스킵)"
if [ ! -f ~/.ssh/gachi ]; then
  ssh-keygen -t ed25519 -f ~/.ssh/gachi -N ""
  echo "키 생성 완료: ~/.ssh/gachi"
else
  echo "키가 이미 존재합니다: ~/.ssh/gachi"
fi

echo ">>> ~/.ssh/config에 gachi-server 등록"
if ! grep -q "Host gachi-server" ~/.ssh/config 2>/dev/null; then
  cat >> ~/.ssh/config <<EOF

Host gachi-server
  HostName $EC2_IP
  User ubuntu
  IdentityFile ~/.ssh/gachi
EOF
  echo "SSH config 등록 완료"
else
  # IP가 바뀐 경우 업데이트
  sed -i "s/HostName .*/HostName $EC2_IP/" ~/.ssh/config
  echo "SSH config IP 업데이트 완료"
fi

echo ">>> EC2에 Docker, Nginx, Certbot 설치 중..."
ssh -i ~/.ssh/gachi ubuntu@$EC2_IP << 'ENDSSH'
set -e

# Docker
sudo apt-get update -y
sudo apt-get install -y docker.io docker-compose-v2
sudo usermod -aG docker ubuntu
sudo systemctl enable docker
sudo systemctl start docker

# Nginx + Certbot
sudo apt-get install -y nginx certbot python3-certbot-nginx

# 앱 디렉토리
mkdir -p ~/gachi-server/build/libs

echo "EC2 초기 설정 완료"
ENDSSH

echo ""
echo "=== 완료 ==="
echo "이제 다음 작업을 순서대로 진행하세요:"
echo "1. 도메인 DNS A 레코드 → $EC2_IP 등록"
echo "2. EC2에 repo clone:"
echo "   ssh gachi-server 'git clone https://github.com/Gachi-Gayo/Gachi-Server-Main.git ~/gachi-server'"
echo "3. EC2에 .env 파일 생성 (.env.example 참고)"
echo "4. Nginx 설정 복사:"
echo "   scp nginx/gachi.conf gachi-server:/tmp/"
echo "   ssh gachi-server 'sudo cp /tmp/gachi.conf /etc/nginx/sites-available/gachi && sudo ln -sf /etc/nginx/sites-available/gachi /etc/nginx/sites-enabled/ && sudo rm -f /etc/nginx/sites-enabled/default && sudo nginx -t && sudo systemctl reload nginx'"
echo "5. DNS 전파 후 Certbot 실행:"
echo "   ssh gachi-server 'sudo certbot --nginx -d <도메인>'"
echo "6. GitHub Secrets 등록 (EC2_HOST, EC2_SSH_KEY, EC2_ENV)"
echo "   EC2_SSH_KEY: \$(cat ~/.ssh/gachi)"
