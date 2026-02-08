# Backup Tool

A Java-based automated backup tool that creates compressed ZIP archives of directories with support for both one-time and scheduled backups.

## Features

- Create compressed ZIP backups of directories
- One-time backup execution
- Scheduled automatic backups (cron-like)
- Command-line interface
- Easy-to-use startup scripts
- Clean Domain-Driven Design architecture

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Building the Project

```bash
mvn clean package
```

This will create an executable JAR file at `target/backup-tool-1.0-SNAPSHOT.jar`

## Usage

### Option 1: Using Startup Scripts (Recommended)

#### Linux/Mac

```bash
# Make the script executable
chmod +x backup.sh

# One-time backup
./backup.sh /path/to/source /path/to/destination

# Scheduled backup (every 5 minutes for testing)
./backup.sh /path/to/source /path/to/destination 5

# Or edit the script to set default values and run
./backup.sh
```

#### Windows

```cmd
# One-time backup
backup.bat C:\path\to\source C:\path\to\destination

# Scheduled backup (every 5 minutes for testing)
backup.bat C:\path\to\source C:\path\to\destination 5

# Or edit the script to set default values and run
backup.bat
```

### Option 2: Direct JAR Execution

```bash
# One-time backup
java -jar target/backup-tool-1.0-SNAPSHOT.jar /path/to/source /path/to/destination

# Scheduled backup (every 5 minutes)
java -jar target/backup-tool-1.0-SNAPSHOT.jar /path/to/source /path/to/destination 5
```

## Arguments

1. **source** (required) - The directory you want to backup
2. **destination** (required) - The directory where backups will be stored
3. **interval_minutes** (optional) - If provided, runs backup every N minutes. If omitted, runs once and exits.

## Examples

### One-time Backup

```bash
# Backup your Documents folder to a Backups directory
java -jar target/backup-tool-1.0-SNAPSHOT.jar ~/Documents ~/Backups
```

### Scheduled Backup (Testing - Every 5 Minutes)

```bash
# Run backup every 5 minutes (good for testing)
java -jar target/backup-tool-1.0-SNAPSHOT.jar ~/Documents ~/Backups 5
```

### Scheduled Backup (Production - Every Hour)

```bash
# Run backup every 60 minutes
java -jar target/backup-tool-1.0-SNAPSHOT.jar ~/Documents ~/Backups 60
```

### Scheduled Backup (Daily)

```bash
# Run backup every 24 hours (1440 minutes)
java -jar target/backup-tool-1.0-SNAPSHOT.jar ~/Documents ~/Backups 1440
```

## How It Works

1. The tool creates a ZIP archive of the source directory
2. The archive is stored in the destination directory with a timestamp
3. Format: `backup-<timestamp>.zip`
4. For scheduled mode, the process runs in the background at the specified interval
5. Press Ctrl+C to stop the scheduled backup service

## Project Structure

```
backup-tool/
├── src/main/java/io/github/devcavin/
│   ├── App.java                           # Main application entry point
│   ├── application/usecase/               # Use case layer
│   │   ├── CreateBackupJobUseCase.java
        ├── ListArchiveUseCase.java
│   │   ├── RunBackupJobUseCase.java
│   │   └── ListBackupJobsUseCase.java
│   ├── domain/                            # Domain layer
│   │   ├── entity/
│   │   │   ├── BackupJob.java
│   │   │   └── Archive.java
│   │   ├── repository/
│   │   │   ├── BackupJobRepository.java
│   │   │   └── ArchiveRepository.java
│   │   ├── service/
│   │   │   ├── BackupService.java
│   │   │   ├── ArchiveCreator.java
│   │   │   └── ArchiveStorage.java
│   │   ├── valueobject/
│   │   │   ├── BackupName.java
│   │   │   ├── SourcePath.java
│   │   │   └── DestinationPath.java
│   │   └── enums/
│   │       └── BackupStatus.java
│   └── infrastructure/                    # Infrastructure layer
│       ├── archive/local/
│       │   ├── LocalArchiveCreator.java
│       │   └── LocalArchiveStorage.java
│       ├── repository/memory/
│       │   ├── InMemoryBackupJobRepository.java
│       │   └── InMemoryArchiveRepository.java
│       └── scheduler/
│           └── ScheduledBackupService.java
├── backup.sh                              # Linux/Mac startup script
├── backup.bat                             # Windows startup script
├── pom.xml
└── README.md
```

## Customizing Defaults

Edit the startup scripts to set your preferred default values:

**backup.sh** (Linux/Mac):
```bash
DEFAULT_SOURCE="/home/user/documents"
DEFAULT_DESTINATION="/home/user/backups"
DEFAULT_INTERVAL=""  # or set to number for scheduled
```

**backup.bat** (Windows):
```cmd
set DEFAULT_SOURCE=C:\Users\%USERNAME%\Documents
set DEFAULT_DESTINATION=C:\Users\%USERNAME%\Backups
set DEFAULT_INTERVAL=
```

## Running as a Background Service

### Linux (systemd)

Create a service file `/etc/systemd/system/backup-tool.service`:

```ini
[Unit]
Description=Automated Backup Service
After=network.target

[Service]
Type=simple
User=yourusername
WorkingDirectory=/path/to/backup-tool
ExecStart=/usr/bin/java -jar /path/to/backup-tool/target/backup-tool-1.0-SNAPSHOT.jar /source /destination 60
Restart=always

[Install]
WantedBy=multi-user.target
```

Then:
```bash
sudo systemctl daemon-reload
sudo systemctl enable backup-tool
sudo systemctl start backup-tool
```

### Windows (Task Scheduler)

1. Open Task Scheduler
2. Create Basic Task
3. Set trigger to "When the computer starts"
4. Action: Start a program
5. Program: `javaw.exe`
6. Arguments: `-jar C:\path\to\backup-tool.jar C:\source C:\destination 60`

## License

MIT License

## Contributing

Pull requests are welcome!