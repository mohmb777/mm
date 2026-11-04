# Enable Remote Desktop safely on a Windows host you own.
# Run from an elevated PowerShell session.
$ErrorActionPreference = 'Stop'

$principal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    throw 'Open PowerShell using Run as administrator, then run this script again.'
}

$edition = (Get-ComputerInfo).WindowsProductName
Write-Host "Detected Windows edition: $edition"
if ($edition -match 'Home') {
    Write-Warning 'Windows Home does not officially support incoming Microsoft Remote Desktop host sessions.'
}

# Enable RDP and require Network Level Authentication.
Set-ItemProperty -Path 'HKLM:\SYSTEM\CurrentControlSet\Control\Terminal Server' -Name fDenyTSConnections -Value 0
Set-ItemProperty -Path 'HKLM:\SYSTEM\CurrentControlSet\Control\Terminal Server\WinStations\RDP-Tcp' -Name UserAuthentication -Value 1

# Enable the built-in firewall rules. Do NOT create router port-forwarding rules.
Enable-NetFirewallRule -DisplayGroup 'Remote Desktop'

Write-Host ''
Write-Host 'RDP host settings enabled with Network Level Authentication.'
Write-Host 'Keep TCP 3389 closed to the public internet. Use a private mesh VPN such as Tailscale.'
Write-Host 'Next: install Tailscale on this PC and your Android phone, then connect using the PC Tailscale IP.'
