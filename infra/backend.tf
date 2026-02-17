terraform {
  backend "s3" {
    bucket = "fiap-fase-4-oficina"
    key    = "notification-service/terraform.tfstate"
    region = "us-east-1"
  }
}
