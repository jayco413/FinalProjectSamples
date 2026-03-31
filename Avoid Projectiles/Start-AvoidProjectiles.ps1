param(
    [switch]$CompileOnly
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$classesRoot = Join-Path $projectRoot "out\classes"
$workspaceRoot = Split-Path -Parent $projectRoot
$javaExe = Join-Path $workspaceRoot "runtime\bin\java.exe"
$javaFxLib = Join-Path $workspaceRoot "lib\javafx-sdk-23.0.2\lib"

if (-not (Test-Path $javaExe)) {
    throw "Bundled runtime not found at $javaExe"
}

if (-not (Test-Path $classesRoot)) {
    throw "Compiled game files not found at $classesRoot"
}

if (-not (Test-Path $javaFxLib)) {
    throw "JavaFX SDK not found at $javaFxLib"
}

if ($CompileOnly) {
    return
}

Push-Location $projectRoot
try {
    & $javaExe `
        --enable-native-access=javafx.graphics,javafx.media `
        --module-path $javaFxLib `
        --add-modules javafx.controls,javafx.fxml,javafx.media `
        -cp $classesRoot `
        edu.mvcc.jcovey.avoidprojectiles.app.AvoidProjectilesApp
}
finally {
    Pop-Location
}
