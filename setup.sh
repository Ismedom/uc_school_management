#!/bin/bash

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_section() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Step 1: Copy .env.local to .env
print_section "Step 1: Setting up Environment Variables"
if [ -f ".env.local" ]; then
    cp .env.local .env
    print_success ".env file created from .env.local"
else
    print_error ".env.local not found"
    exit 1
fi

# Step 2: Run npm install
print_section "Step 2: Installing Node.js Dependencies"
if command -v npm &> /dev/null; then
    npm install
    if [ $? -eq 0 ]; then
        print_success "npm install completed successfully"
    else
        print_error "npm install failed"
        exit 1
    fi
else
    print_warning "npm not found, skipping npm install"
fi

# Step 3: Database Setup
print_section "Step 3: Database Setup"

echo "Do you want to use PostgreSQL on Docker? (y/n)"
read -r answer

if [[ "$answer" =~ ^[Yy]$ ]]; then
    print_success "Using PostgreSQL on Docker"
    # Ensure Docker is running
    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    # Pull and run PostgreSQL via docker-compose
    docker-compose up -d db
    if [ $? -eq 0 ]; then
        print_success "PostgreSQL container started"
    else
        print_error "Failed to start PostgreSQL container"
        exit 1
    fi
else
    echo "Select database type:"
    echo "1) PostgreSQL (local)"
    echo "2) MySQL (local)"
    echo "3) SQLite"
    read -r db_choice

    case $db_choice in
        1)
            print_success "Selected PostgreSQL (local)"
            # Update .env for local PostgreSQL
            cat >> .env << EOF

# Local PostgreSQL Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/school_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
EOF
            ;;
        2)
            print_success "Selected MySQL (local)"
            # Update .env for local MySQL
            cat >> .env << EOF

# Local MySQL Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/school_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
EOF
            ;;
        3)
            print_success "Selected SQLite"
            # Create db directory if not exists
            mkdir -p db
            # Create empty SQLite database file
            touch db/school.db
            print_success "SQLite database file created at db/school.db"
            # Update .env for SQLite
            cat >> .env << EOF

# SQLite Configuration
SPRING_DATASOURCE_URL=jdbc:sqlite:db/school.db
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
EOF
            ;;
        *)
            print_error "Invalid choice"
            exit 1
            ;;
    esac
fi

# Step 4: Run Spring Boot Application
print_section "Step 4: Starting Spring Boot Application"

if [[ "$answer" =~ ^[Yy]$ ]]; then
    # For Docker setup, run the full compose
    docker-compose up
else
    # For local databases, run with Maven
    if command -v mvn &> /dev/null; then
        mvn spring-boot:run
    else
        print_error "Maven not found. Please install Maven."
        exit 1
    fi
fi