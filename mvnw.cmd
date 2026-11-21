@echo off
set "MAVEN_CMD=C:\tools\apache-maven-3.9.6\bin\mvn.cmd"
if exist "%MAVEN_CMD%" (
    "%MAVEN_CMD%" %*
) else (
    mvn %*
)
