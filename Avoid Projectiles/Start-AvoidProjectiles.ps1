param(
    [switch]$Clean,
    [switch]$CompileOnly
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcRoot = Join-Path $projectRoot "src\main\java"
$resourcesRoot = Join-Path $projectRoot "src\main\resources"
$outRoot = Join-Path $projectRoot "out"
$classesRoot = Join-Path $outRoot "classes"
$workspaceRoot = Split-Path -Parent $projectRoot
$javaFxLib = Join-Path $workspaceRoot "Common Libraries\javafx-sdk-23.0.2\lib"
if (-not (Test-Path $javaFxLib)) {
    $javaFxLib = Join-Path $workspaceRoot "lib\javafx-sdk-23.0.2\lib"
}

if (-not (Test-Path $javaFxLib)) {
    throw "JavaFX SDK not found at $javaFxLib"
}

if ($Clean -and (Test-Path $classesRoot)) {
    Remove-Item -LiteralPath $classesRoot -Recurse -Force
}

Push-Location $projectRoot
try {
    New-Item -ItemType Directory -Force -Path $classesRoot | Out-Null

    $javaFiles = Get-ChildItem -Path $srcRoot -Recurse -Filter *.java | ForEach-Object { $_.FullName }
    if ($javaFiles.Count -eq 0) {
        throw "No Java source files found under $srcRoot"
    }

    javac `
        --module-path $javaFxLib `
        --add-modules javafx.controls,javafx.fxml,javafx.media `
        -d $classesRoot `
        $javaFiles

    Copy-Item -Path (Join-Path $resourcesRoot "*") -Destination $classesRoot -Recurse -Force

    if ($CompileOnly) {
        return
    }

    java `
        --module-path $javaFxLib `
        --add-modules javafx.controls,javafx.fxml,javafx.media `
        -cp $classesRoot `
        edu.mvcc.jcovey.avoidprojectiles.app.AvoidProjectilesApp
}
finally {
    Pop-Location
}
