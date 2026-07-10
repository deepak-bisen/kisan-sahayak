# =============================================================================
# load-env.ps1
# Helper script to load .env into the current PowerShell session
# Usage:  .\load-env.ps1
# =============================================================================

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Error ".env file not found at $envFile. Copy .env.example to .env first and fill in values."
    exit 1
}

Write-Host "Loading environment variables from .env ..." -ForegroundColor Cyan

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#") -and $line -match "^\s*([^=]+)=(.*)$") {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        # Remove surrounding quotes if present
        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [System.Environment]::SetEnvironmentVariable($name, $value, 'Process')
        Write-Host "  $name loaded" -ForegroundColor Green
    }
}

Write-Host "`nDone. These variables are now available in the current PowerShell session." -ForegroundColor Cyan
Write-Host "You can now run the services or launch IntelliJ from this same window." -ForegroundColor Yellow

# Show loaded values (without showing secrets fully)
Write-Host "`nCurrently loaded (for verification):"
Write-Host "  JWT_SECRET length: $(([System.Environment]::GetEnvironmentVariable('JWT_SECRET')).Length) chars"
Write-Host "  DB_USERNAME: $([System.Environment]::GetEnvironmentVariable('DB_USERNAME'))"
Write-Host "  DB_PASSWORD: $(if ([System.Environment]::GetEnvironmentVariable('DB_PASSWORD')) { '****' } else { '(not set)' })"