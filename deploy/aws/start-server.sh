#!/bin/bash
# Starts the Eriantys EC2 instance and prints its public IP + SSH command once ready.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
source ./config.sh

echo "Starting instance $INSTANCE_ID..."
aws ec2 start-instances --instance-ids "$INSTANCE_ID" --region "$REGION" > /dev/null

echo "Waiting for it to enter 'running' state..."
aws ec2 wait instance-running --instance-ids "$INSTANCE_ID" --region "$REGION"

PUBLIC_IP=$(aws ec2 describe-instances --instance-ids "$INSTANCE_ID" --region "$REGION" \
  --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

echo "Instance running. Public IP: $PUBLIC_IP"
echo "SSH: ssh -i $SSH_KEY $SSH_USER@$PUBLIC_IP"
echo "Game server port: 12345 (share $PUBLIC_IP:12345 with players)"
