#!/usr/bin/env bash
# ==============================================================================
# Enhanced Backup Tool Launcher
# ==============================================================================
set -euo pipefail

# -------- Configuration -----
DEFAULT_SOURCE="/home/use/documents"
DEFAULT_DESTINATION="/home/backups"
DEFAULT_INTERVAL=""
JAR_FILE="target/backup-tool-1.0-SNAPSHOT.jar"

# -------- Colors ------------
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
GRAY='\033[0;90m'
YELLOW='\033[0;33m'
NC='\033[0m'

# -------- Args --------------
SOURCE="${1:-$DEFAULT_SOURCE}"
DESTINATION="${2:-$DEFAULT_DESTINATION}"
INTERVAL="${3:-$DEFAULT_INTERVAL}"

# -------- Cleanup -----------
# Ensures cursor is restored even if the user exits mid-animation
cleanup() {
  tput cnorm
  echo -e "${NC}"
}
trap cleanup EXIT

# -------- Helpers -----------
die() {
  echo -e "${RED}[ERROR] $1${NC}"
  exit 1
}

draw_logo() {
  echo -e "${BLUE}"
  cat <<'EOF'
  ____             _
 |  _ \           | |
 | |_) | __ _  ___| | ___   _ _ __
 |  _ < / _` |/ __| |/ / | | | '_ \
 | |_) | (_| | (__|   <| |_| | |_) |
 |____/ \__,_|\___|_|\_\\__,_| .__/
                              | |
                              |_|
          Backup Tool
EOF
  echo -e "${NC}"
}

# -------- Animation Engine -----------
penguin_run() {
  local pid=$1
  # shellcheck disable=SC2155
  local cols=$(tput cols)
  local width=12
  local pos=0
  local dir=1
  local height=7 # Number of lines the animation occupies

  tput civis # Hide cursor

  # Print placeholder lines so we can move up into them
  # shellcheck disable=SC2034
  for i in $(seq 1 $height); do echo ""; done

  while kill -0 "$pid" 2>/dev/null; do
    # Move cursor back up to the top of the animation block
    printf "\033[%dA" $height

    # Waddling logic: Frame 1 vs Frame 2
    if (( pos % 2 == 0 )); then
      printf "\r\033[K%*s   ${GRAY}_~_${NC}\n" "$pos" ""
      printf "\r\033[K%*s  ${GRAY}(o o)${NC}\n" "$pos" ""
      printf "\r\033[K%*s ${BLUE}/  V  \\ ${NC}\n" "$pos" ""
      printf "\r\033[K%*s${BLUE}/|     |${NC}\n" "$pos" ""
      printf "\r\033[K%*s ${GRAY} /  /  ${NC}\n" "$pos" ""
    else
      printf "\r\033[K%*s   ${GRAY}_~_${NC}\n" "$pos" ""
      printf "\r\033[K%*s  ${GRAY}(o o)${NC}\n" "$pos" ""
      printf "\r\033[K%*s  ${BLUE}\\ V / ${NC}\n" "$pos" ""
      printf "\r\033[K%*s ${BLUE}/|   | ${NC}\n" "$pos" ""
      printf "\r\033[K%*s  ${GRAY}\\  \\  ${NC}\n" "$pos" ""
    fi

    # Status Line with Spinner
    local spinChars="/-\|"
    local frame=$((pos % 4))
    printf "\r\033[K\n" # Spacer line
    # shellcheck disable=SC2059
    printf "\r\033[K  ${YELLOW}${spinChars:$frame:1}${NC} ${BLUE}Backup in progress...${NC}\n"

    sleep 0.1

    # Movement physics
    (( pos += dir ))
    if (( pos >= (cols - width) )); then dir=-1; elif (( pos <= 0 )); then dir=1; fi
  done
}

penguin_success() {
  echo -e "${GREEN}"
  cat <<'EOF'
     _~_
    (o o)
    / V \
     | |
    /_|_|_

  SUCCESS: Backup completed.
EOF
  echo -e "${NC}"
}

penguin_fail() {
  echo -e "${RED}"
  cat <<'EOF'
      _
     ( )
    / | \
      |
     / \

  FAILED: Check logs for details.
EOF
  echo -e "${NC}"
}

# -------- Main Execution -----------
clear
draw_logo

# Pre-flight Checks
[[ -f "$JAR_FILE" ]] || die "JAR not found: $JAR_FILE"
[[ -d "$SOURCE" ]] || die "Source directory does not exist: $SOURCE"
mkdir -p "$DESTINATION" || die "Failed to create destination"
[[ -z "$(ls -A "$SOURCE")" ]] && die "Source directory is empty"

# Config Summary
echo -e "${GRAY}Settings:${NC}"
echo "  From: $SOURCE"
echo "  To:   $DESTINATION"
[[ -n "$INTERVAL" ]] && echo "  Freq: Every $INTERVAL mins" || echo "  Mode: Single Run"
echo -e "\n--- Starting Process ---"

# Start Background Process
set +e
if [[ -z "$INTERVAL" ]]; then
  java -jar "$JAR_FILE" "$SOURCE" "$DESTINATION" > /dev/null 2>&1 &
else
  java -jar "$JAR_FILE" "$SOURCE" "$DESTINATION" "$INTERVAL" > /dev/null 2>&1 &
fi

JAVA_PID=$!
penguin_run "$JAVA_PID"
wait "$JAVA_PID"
EXIT_CODE=$?
set -e

# Final Result
echo -e "\n"
if [[ $EXIT_CODE -eq 0 ]]; then
  penguin_success
else
  penguin_fail
  exit "$EXIT_CODE"
fi