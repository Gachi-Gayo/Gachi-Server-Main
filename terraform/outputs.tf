output "ec2_ip" {
  value       = aws_eip.gachi.public_ip
  description = "EC2 Elastic IP - DNS A 레코드에 등록할 IP"
}

output "ssh_command" {
  value       = "ssh -i ~/.ssh/gachi ubuntu@${aws_eip.gachi.public_ip}"
  description = "EC2 SSH 접속 명령어"
}
