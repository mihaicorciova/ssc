:: ---------------------------
:: CONFIG
:: ---------------------------
@echo off
set ECLIPSE_LOCATION="C:\Program Files\Eclipse\eclipse.exe"

IF EXIST %ECLIPSE_LOCATION% (
	:: TODO: remove module hard-coding
	cd ../../client
	%ECLIPSE_LOCATION% -nosplash -application org.eclipse.jdt.core.JavaCodeFormatter -verbose -config .settings/org.eclipse.jdt.core.prefs src/
	echo Formatting done!
) ELSE (
	echo Eclipse was not found in the following location: %ECLIPSE_LOCATION%
)

pause



