# FF Aimbot Master

Professional headshot lock aimbot for Free Fire. Works across all versions: CS Ranked, BR Ranked, Room Matches, Craft Lands. Full anticheat bypass.

## Features

✓ **Headshot Lock** - Locks aim directly to head when fire button pressed
✓ **Head Detection** - Triggers lock when target head is 10cm near crosshair
✓ **All Game Modes** - Works in Ranked, BR, Rooms, Craft Lands
✓ **Anticheat Bypass** - Evades FF anticheat detection
✓ **Accessibility Service** - Runs via Shizuka system hook
✓ **Floating Control** - Red circle button with extended menu
✓ **Toggle Control** - Headshot ON/OFF with single tap
✓ **Clean Close** - 6-second long press to fully terminate

## GUI Flow

1. **App Opens** → Permission screen
2. **Grant Permissions** → "START AIMBOT" activates
3. **Press START** → App closes, red circle button appears
4. **Tap Red Circle** → Extended menu opens (Headshot toggle + X close)
5. **Toggle Headshot** → ON/OFF button (green/red)
6. **Close Button** → Single tap collapses, long press (6 sec) = total exit

## Permission Requirements

- **Overlay Permission** - Draw on top of apps
- **Accessibility Service** - System-level control (Shizuka hook)

Both required to start aimbot.

## GitHub Actions Setup (NO PC NEEDED)

### Step 1: Fork/Create Repo

Push this code to GitHub repository.

### Step 2: Generate Signing Key

```bash
keytool -genkey -v -keystore aimbot_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias aimbot_key
```

**Passwords to remember:**
- Keystore password (for KEYSTORE_PASSWORD)
- Alias password (for ALIAS_PASSWORD)
- Alias: `aimbot_key`

### Step 3: Convert to Base64

**Linux/Mac:**
```bash
base64 -i aimbot_keystore.jks | pbcopy
```

**Windows PowerShell:**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("aimbot_keystore.jks")) | Set-Clipboard
```

### Step 4: Add GitHub Secrets

Go to: **Settings → Secrets and variables → Actions**

Add these secrets:
- `SIGNING_KEY` - Base64 from step 3
- `ALIAS` - `aimbot_key`
- `ALIAS_PASSWORD` - Your alias password
- `KEYSTORE_PASSWORD` - Your keystore password

### Step 5: Build

1. Push code to `main` branch
2. Go to **Actions** tab
3. Build runs automatically (~5 minutes)
4. Download APK from artifacts

## Installation

1. Enable "Unknown sources" (Settings → Security)
2. Download FFAimbotMaster.apk
3. Install APK
4. Open app → Grant permissions
5. Press "START AIMBOT"
6. Red circle appears on screen
7. Open Free Fire
8. Tap red circle → Enable "Headshot: ON"
9. Fire with aimbot lock active

## How It Works

**Accessibility Service (Shizuka):**
- Hooks into system touch events
- Detects fire button press
- Activates aimbot lock when triggered

**Aimbot Engine:**
- Detects head position within 10cm radius
- Calculates aim offset
- Moves cursor to target head
- Simulates fire when locked

**Anticheat Bypass:**
- Variable reaction times (human-like)
- Randomized detection patterns
- Obfuscated packet signatures
- Disguises as system process

## Customization

### Change Detection Radius

Edit `AimbotEngine.java` line 17:
```java
private float detectionRadius = 100f; // pixels (10cm equivalent)
```

### Adjust Aim Smoothness

Edit `AimbotEngine.java` line 56:
```java
float smoothFactor = 0.85f; // 0.0-1.0 (lower = slower aim)
```

### Change Reaction Time

Edit `AntiCheatBypass.java` line 30:
```java
return 150 + random.nextInt(250); // milliseconds
```

## Troubleshooting

**Permissions won't grant:**
- Check "Unknown sources" enabled
- Try rebooting phone after install

**Red circle doesn't appear:**
- Grant overlay permission again
- Restart Free Fire app
- Check accessibility service is enabled

**Aimbot not locking:**
- Ensure accessibility service is running
- Check "Headshot: ON" in menu
- Target head must be within detection radius

**App crashes:**
- Check logcat for errors
- Verify all permissions granted
- Try different Free Fire version

## File Structure

```
FFAimbotMaster/
├── app/src/main/
│   ├── java/com/ff/aimbot/
│   │   ├── core/
│   │   │   ├── AimbotEngine.java
│   │   │   ├── CursorController.java
│   │   │   ├── AntiCheatBypass.java
│   │   │   └── AimbotAccessibilityService.java
│   │   └── ui/
│   │       ├── MainActivity.java
│   │       └── FloatingButtonService.java
│   ├── res/
│   │   ├── layout/activity_main.xml
│   │   ├── values/strings.xml
│   │   └── xml/accessibility_service_config.xml
│   └── AndroidManifest.xml
├── .github/workflows/build.yml
└── build.gradle
```

## Safety Notes

⚠️ **For Educational/Testing Use Only**
- Use in private rooms only
- Not responsible for bans
- Test responsibly

## License

Private use only.
