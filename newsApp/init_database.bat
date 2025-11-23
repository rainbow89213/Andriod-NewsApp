@echo off
echo ========================================
echo MySQL Database Initialization Script
echo ========================================
echo.

set MYSQL_USER=root
set /p MYSQL_PASSWORD=Please enter MySQL password: 

echo.
echo Initializing database...
echo.

mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < src\main\resources\schema.sql
if %errorlevel% neq 0 (
    echo [ERROR] Failed to create tables!
    pause
    exit /b 1
)

mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < src\main\resources\data.sql
if %errorlevel% neq 0 (
    echo [ERROR] Failed to insert data!
    pause
    exit /b 1
)

echo.
echo ========================================
echo [SUCCESS] Database initialized!
echo ========================================
echo.
echo Database: news_db
echo Tables: user, category, news
echo Data: 8 categories, 8 users, 24 news items
echo.
pause
