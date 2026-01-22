@echo off
setlocal enabledelayedexpansion

REM Color codes (using color command for simplicity)
REM We'll use prefixes instead of colors for batch

:print_section
echo ========================================
echo %~1
echo ========================================
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

REM Step 1: Copy .env.local to .env
call :print_section "Step 1: Setting up Environment Variables"
if exist ".env.local" (
    copy .env.local .env >nul
    call :print_success ".env file created from .env.local"
) else (
    call :print_error ".env.local not found"
    exit /b 1
)

REM Step 2: Run npm install
call :print_section "Step 2: Installing Node.js Dependencies"
where npm >nul 2>nul
if %errorlevel% equ 0 (
    npm install
    if %errorlevel% equ 0 (
        call :print_success "npm install completed successfully"
    ) else (
        call :print_error "npm install failed"
        exit /b 1
    )
) else (
    call :print_warning "npm not found, skipping npm install"
)

REM Step 3: Database Setup
call :print_section "Step 3: Database Setup"

set /p answer="Do you want to use PostgreSQL on Docker? (y/n): "

if /i "%answer%"=="y" (
    call :print_success "Using PostgreSQL on Docker"
    REM Check if Docker is running
    docker info >nul 2>nul
    if %errorlevel% neq 0 (
        call :print_error "Docker is not running. Please start Docker and try again."
        exit /b 1
    )
    REM Pull and run PostgreSQL via docker-compose
    docker-compose up -d db
    if %errorlevel% equ 0 (
        call :print_success "PostgreSQL container started"
    ) else (
        call :print_error "Failed to start PostgreSQL container"
        exit /b 1
    )
    set USE_DOCKER=1
) else (
    echo Select database type:
    echo 1) PostgreSQL (local)
    echo 2) MySQL (local)
    echo 3) SQLite
    set /p db_choice="Enter choice (1-3): "

    if "%db_choice%"=="1" (
        call :print_success "Selected PostgreSQL (local)"
        REM Update .env for local PostgreSQL
        echo. >> .env
        echo # Local PostgreSQL Configuration >> .env
        echo SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/school_db >> .env
        echo SPRING_DATASOURCE_USERNAME=postgres >> .env
        echo SPRING_DATASOURCE_PASSWORD=postgres >> .env
    ) else if "%db_choice%"=="2" (
        call :print_success "Selected MySQL (local)"
        REM Update .env for local MySQL
        echo. >> .env
        echo # Local MySQL Configuration >> .env
        echo SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/school_db >> .env
        echo SPRING_DATASOURCE_USERNAME=root >> .env
        echo SPRING_DATASOURCE_PASSWORD=password >> .env
    ) else if "%db_choice%"=="3" (
        call :print_success "Selected SQLite"
        REM Create db directory if not exists
        if not exist "db" mkdir db
        REM Create empty SQLite database file
        type nul > db\school.db
        call :print_success "SQLite database file created at db\school.db"
        REM Update .env for SQLite
        echo. >> .env
        echo # SQLite Configuration >> .env
        echo SPRING_DATASOURCE_URL=jdbc:sqlite:db/school.db >> .env
        echo SPRING_DATASOURCE_USERNAME= >> .env
        echo SPRING_DATASOURCE_PASSWORD= >> .env
    ) else (
        call :print_error "Invalid choice"
        exit /b 1
    )
    set USE_DOCKER=0
)

REM Step 4: Run Spring Boot Application
call :print_section "Step 4: Starting Spring Boot Application"

if "%USE_DOCKER%"=="1" (
    REM For Docker setup, run the full compose
    docker-compose up
) else (
    REM For local databases, run with Maven
    where mvn >nul 2>nul
    if %errorlevel% equ 0 (
        mvn spring-boot:run
    ) else (
        call :print_error "Maven not found. Please install Maven."
        exit /b 1
    )
)