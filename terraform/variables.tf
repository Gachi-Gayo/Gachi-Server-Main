variable "aws_region" {
  default = "ap-northeast-2"
}

variable "key_name" {
  description = "EC2 key pair name (registered in AWS)"
  default     = "gachi-key"
}

variable "public_key_path" {
  description = "Local path to SSH public key"
  default     = "~/.ssh/gachi.pub"
}

variable "domain" {
  description = "Domain name for HTTPS (e.g. api.example.com)"
}
