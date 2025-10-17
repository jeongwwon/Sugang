provider "aws" {
  region = "ap-northeast-2"
}

# ------------------------
# VPC & Networking
# ------------------------
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  tags = { Name = "portfolio-vpc" }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
}

resource "aws_subnet" "public_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.3.0/24"
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true
}

resource "aws_subnet" "public_2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.4.0/24"
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = true
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
}

resource "aws_route_table_association" "public_1" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_2" {
  subnet_id      = aws_subnet.public_2.id
  route_table_id = aws_route_table.public.id
}

# ------------------------
# Security Group
# ------------------------
resource "aws_security_group" "ec2" {
  name   = "portfolio-ec2-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ------------------------
# IAM Role
# ------------------------
resource "aws_iam_role" "ec2_role" {
  name = "portfolio-ec2-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{ Action = "sts:AssumeRole", Effect = "Allow", Principal = { Service = "ec2.amazonaws.com" } }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "portfolio-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

# ------------------------
# Launch Template
# ------------------------
resource "aws_launch_template" "app" {
  name_prefix   = "portfolio-app-"
  image_id      = "ami-0e9bfdb247cc8de84"
  instance_type = "t3.medium"

  iam_instance_profile {
    name = aws_iam_instance_profile.ec2_profile.name
  }

  vpc_security_group_ids = [aws_security_group.ec2.id]

  user_data = base64encode(<<-EOF
              #!/bin/bash
              sudo apt-get update -y
              sudo apt-get install -y python3
              nohup python3 -m http.server 8080 --bind 0.0.0.0 &
              EOF
  )
}

# ------------------------
# Auto Scaling Group
# ------------------------
resource "aws_autoscaling_group" "app" {
  desired_capacity    = 10
  min_size            = 10
  max_size            = 10
  vpc_zone_identifier = [aws_subnet.public_1.id, aws_subnet.public_2.id]

  launch_template {
    id      = aws_launch_template.app.id
    version = "$Latest"
  }
}

# ------------------------
# Application Load Balancer
# ------------------------
resource "aws_lb" "app" {
  name               = "portfolio-alb"
  load_balancer_type = "application"
  subnets            = [aws_subnet.public_1.id, aws_subnet.public_2.id]
  security_groups    = [aws_security_group.ec2.id]
}

resource "aws_lb_target_group" "app" {
  name     = "portfolio-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id
  health_check {
    path                = "/"
    protocol            = "HTTP"
    matcher             = "200-399"
    interval            = 30
    timeout             = 10
    healthy_threshold   = 2
    unhealthy_threshold = 5
  }
}

resource "aws_lb_listener" "app" {
  load_balancer_arn = aws_lb.app.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}

resource "aws_autoscaling_attachment" "asg_attachment" {
  autoscaling_group_name = aws_autoscaling_group.app.name
  lb_target_group_arn    = aws_lb_target_group.app.arn
}

# ------------------------
# Auto Scaling Policy (ALB 요청 기반)
# ------------------------
resource "aws_autoscaling_policy" "scale_on_request" {
  name                   = "scale-on-request"
  policy_type            = "TargetTrackingScaling"
  autoscaling_group_name = aws_autoscaling_group.app.name

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ALBRequestCountPerTarget"
      resource_label         = "app/portfolio-alb/fb358c376070279e/targetgroup/portfolio-tg/8b57cad8882f733d"
    }
    target_value       = 100
    disable_scale_in   = false
  }
}


# ------------------------
# CloudWatch Dashboard
# ------------------------
resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "portfolio-traffic-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        "type": "metric",
        "x": 0,
        "y": 0,
        "width": 24,
        "height": 6,
        "properties": {
          "view": "timeSeries",
          "stacked": false,
          "metrics": [
            # ALB 전체 요청 수 (모든 요청)
            ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", aws_lb.app.arn_suffix],
            # ALB에서 발생한 에러 응답 (4XX/5XX)
            ["AWS/ApplicationELB", "HTTPCode_ELB_4XX_Count", "LoadBalancer", aws_lb.app.arn_suffix],
            ["AWS/ApplicationELB", "HTTPCode_ELB_5XX_Count", "LoadBalancer", aws_lb.app.arn_suffix],
            # Target 인스턴스 응답 상태 코드
            ["AWS/ApplicationELB", "HTTPCode_Target_2XX_Count", "LoadBalancer", aws_lb.app.arn_suffix],
            ["AWS/ApplicationELB", "HTTPCode_Target_4XX_Count", "LoadBalancer", aws_lb.app.arn_suffix],
            ["AWS/ApplicationELB", "HTTPCode_Target_5XX_Count", "LoadBalancer", aws_lb.app.arn_suffix]
          ],
          "region": "ap-northeast-2",
          "title": "ALB 요청 처리 현황 (전체/성공/실패)",
          "period": 60,
          "stat": "Sum"
        }
      }
    ]
  })
}


# ------------------------
# Outputs
# ------------------------
output "alb_dns_name" {
  value = aws_lb.app.dns_name
}
