#!/bin/bash

# Planbook Deployment Script
# Usage: ./deploy.sh [local|prod] [up|down|restart|logs]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default values
ENVIRONMENT="prod"
ACTION="up"
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

# Parse arguments
if [ $# -ge 1 ]; then
    ENVIRONMENT=$1
fi

if [ $# -ge 2 ]; then
    ACTION=$2
fi

# Set compose file and env file based on environment
if [ "$ENVIRONMENT" = "local" ]; then
    COMPOSE_FILE="docker-compose-local.yml"
    ENV_FILE=".env.local"
    print_status "Using LOCAL environment configuration"
else
    COMPOSE_FILE="docker-compose.yml"
    ENV_FILE=".env"
    print_status "Using PRODUCTION environment configuration"
fi

# Check if files exist
if [ ! -f "$COMPOSE_FILE" ]; then
    print_error "Compose file $COMPOSE_FILE not found!"
    exit 1
fi

if [ ! -f "../$ENV_FILE" ]; then
    print_error "Environment file ../$ENV_FILE not found!"
    print_warning "Please copy and configure the appropriate .env file:"
    if [ "$ENVIRONMENT" = "local" ]; then
        echo "  cp ../.env.local ../.env"
    else
        echo "  cp ../.env.example ../.env"
    fi
    exit 1
fi

# Function to show usage
show_usage() {
    echo "Usage: $0 [local|prod] [up|down|restart|logs|ps|pull]"
    echo ""
    echo "Environments:"
    echo "  local  - Local development (infrastructure only)"
    echo "  prod   - Production deployment (all services)"
    echo ""
    echo "Actions:"
    echo "  up      - Start services (default)"
    echo "  down    - Stop services"
    echo "  restart - Restart services"
    echo "  logs    - Show logs"
    echo "  ps      - Show service status"
    echo "  pull    - Pull latest images"
    echo ""
    echo "Examples:"
    echo "  $0 local up     - Start local infrastructure"
    echo "  $0 prod down    - Stop production services"
    echo "  $0 prod logs    - Show production logs"
}

# Function to check Docker
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed!"
        exit 1
    fi

    if ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not available!"
        exit 1
    fi
}

# Function to start services
start_services() {
    print_status "Starting services with $COMPOSE_FILE..."
    
    # Copy env file to current directory for docker-compose
    cp "../$ENV_FILE" ./.env
    
    docker compose -f "$COMPOSE_FILE" up -d --remove-orphans
    
    print_success "Services started successfully!"
    
    if [ "$ENVIRONMENT" = "local" ]; then
        print_warning "Local infrastructure started. You can now run application services from your IDE."
        echo ""
        echo "Available services:"
        echo "  - Kafka: http://localhost:9092"
        echo "  - Kafdrop: http://localhost:9000"
        echo "  - Redis: localhost:6379"
        echo "  - Dozzle: http://localhost:8888"
        echo "  - Portainer: http://localhost:9001"
    else
        echo ""
        echo "Production services started. Available endpoints:"
        echo "  - API Gateway: http://your-domain:8080"
        echo "  - Discovery Server: http://your-domain:8761"
        echo "  - Kafdrop: http://your-domain:9000"
        echo "  - Dozzle: http://your-domain:8888"
        echo "  - Portainer: http://your-domain:9001"
    fi
}

# Function to stop services
stop_services() {
    print_status "Stopping services..."
    docker compose -f "$COMPOSE_FILE" down --remove-orphans
    print_success "Services stopped successfully!"
}

# Function to restart services
restart_services() {
    print_status "Restarting services..."
    stop_services
    sleep 2
    start_services
}

# Function to show logs
show_logs() {
    print_status "Showing logs for all services..."
    docker compose -f "$COMPOSE_FILE" logs -f
}

# Function to show service status
show_status() {
    print_status "Service status:"
    docker compose -f "$COMPOSE_FILE" ps
}

# Function to pull images
pull_images() {
    print_status "Pulling latest images..."
    
    # Copy env file to current directory for docker-compose
    cp "../$ENV_FILE" ./.env
    
    docker compose -f "$COMPOSE_FILE" pull
    print_success "Images pulled successfully!"
}

# Main execution
main() {
    # Check if help is requested
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_usage
        exit 0
    fi

    # Check Docker installation
    check_docker

    # Execute action
    case $ACTION in
        "up")
            start_services
            ;;
        "down")
            stop_services
            ;;
        "restart")
            restart_services
            ;;
        "logs")
            show_logs
            ;;
        "ps")
            show_status
            ;;
        "pull")
            pull_images
            ;;
        *)
            print_error "Unknown action: $ACTION"
            show_usage
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
