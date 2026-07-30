@echo off
setlocal enabledelayedexpansion

echo ========================================
echo TEST COMPLET DE L'API SIGAVT
echo ========================================
echo.

set BASE_URL=http://localhost:8081
set SUCCESS_COUNT=0
set FAIL_COUNT=0

echo === HEALTH CHECKS ===
echo Test: Health Check
curl -s -o nul -w "%%{http_code}" %BASE_URL%/actuator/health
if errorlevel 1 (
    echo [FAILED]
    set /a FAIL_COUNT+=1
) else (
    echo [SUCCESS]
    set /a SUCCESS_COUNT+=1
)
echo.

echo === AUTHENTICATION TESTS ===
echo Test: Login (invalid credentials)
curl -s -o nul -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}" %BASE_URL%/api/auth/login
if errorlevel 1 (
    echo [SUCCESS - Expected failure]
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAILED - Should have failed]
    set /a FAIL_COUNT+=1
)
echo.

echo Test: Login (admin)
curl -s -o nul -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"email\":\"admin@sigavt.cm\",\"password\":\"admin123\"}" %BASE_URL%/api/auth/login
if errorlevel 1 (
    echo [FAILED]
    set /a FAIL_COUNT+=1
) else (
    echo [SUCCESS]
    set /a SUCCESS_COUNT+=1
)
echo.

echo === HTML PAGES TESTS ===
echo Test: Page /
curl -s -o nul -w "%%{http_code}" %BASE_URL%/
if errorlevel 1 (
    echo [FAILED]
    set /a FAIL_COUNT+=1
) else (
    echo [SUCCESS]
    set /a SUCCESS_COUNT+=1
)
echo.

echo Test: Page /login
curl -s -o nul -w "%%{http_code}" %BASE_URL%/login
if errorlevel 1 (
    echo [FAILED]
    set /a FAIL_COUNT+=1
) else (
    echo [SUCCESS]
    set /a SUCCESS_COUNT+=1
)
echo.

echo Test: Page /dashboard
curl -s -o nul -w "%%{http_code}" %BASE_URL%/dashboard
if errorlevel 1 (
    echo [FAILED]
    set /a FAIL_COUNT+=1
) else (
    echo [SUCCESS]
    set /a SUCCESS_COUNT+=1
)
echo.

echo ========================================
echo TEST SUMMARY
echo ========================================
echo.
echo Total tests: 5
echo Success: %SUCCESS_COUNT%
echo Failed: %FAIL_COUNT%
echo.

if %FAIL_COUNT%==0 (
    echo ALL TESTS PASSED!
) else (
    echo SOME TESTS FAILED!
)
echo.

endlocal
