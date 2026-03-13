# To-Do Android Application

<p align="center">
  <strong>Version 1.0 - First Release</strong>
</p>

A native Android application for managing personal tasks with scheduled reminder notifications.

## Features

- **Create Todos**: Add new tasks with title, description, scheduled date/time, and tags
- **Edit Todos**: Modify existing task details
- **Delete Todos**: Remove unwanted tasks
- **Mark Todos Complete**: Track task completion status
- **Tags**: Create colored tags to organize and categorize tasks
- **Statistics View**: View todos grouped by tag with collapsible sections
- **Scheduled Notifications**: Receive reminders at the specified date and time
- **Recurring Tasks**: Set tasks to repeat hourly, daily, weekly, monthly, or yearly
- **Local Storage**: Tasks persist locally using Android's file storage
- **Navigation Drawer**: Easy access to all app sections (Tasks, Tags, Statistics, Settings)
- **Language Support**: Full French and English localization with in-app language switching
- **Clear Data**: Option to delete all tasks and tags from settings

## Project Structure

```
app/src/main/java/fr/onyxleroy/to_do/
├── Todo.java                      # Data model for todo items
├── Tag.java                       # Data model for tags
├── MainActivity.java              # Main activity with navigation and views
├── LocaleHelper.java              # Language localization helper
├── adapters/
│   ├── TodoAdapter.java           # RecyclerView adapter for displaying todos
│   ├── TagAdapter.java            # RecyclerView adapter for displaying tags
│   └── StatisticsAdapter.java    # Adapter for todos grouped by tag
├── dialogs/
│   ├── AddTodoDialog.java         # Dialog for adding/editing todos
│   └── AddTagDialog.java          # Dialog for adding/editing tags
├── receivers/
│   ├── NotificationReceiver.java  # BroadcastReceiver for handling notifications
│   └── BootReceiver.java          # BroadcastReceiver for rescheduling alarms after reboot
└── utils/
    ├── NotificationHelper.java    # Utility for scheduling/canceling notifications
    ├── TodoStorageManager.java    # Local file storage management for todos
    ├── TagStorageManager.java     # Local file storage management for tags
    └── FoldedTagsManager.java     # Manages collapsed/expanded state of tag groups
```

## Technical Details

### Dependencies

- **AndroidX**: Core Android compatibility libraries
- **Material Components**: Material Design UI components
- **RecyclerView**: Efficient list display for todo items
- **CardView**: Material card styling for todo items
- **AlarmManager**: System-level alarm scheduling for notifications

### Data Model

The `Todo` class contains:
- `id`: Unique UUID identifier
- `title`: Task title (required)
- `description`: Task description (optional)
- `dateTimeMillis`: Scheduled reminder time in milliseconds
- `completed`: Completion status flag
- `repeatType`: Recurrence type (NONE, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY)
- `tags`: A list of tags assigned to the task

The `Tag` class contains:
- `id`: Unique UUID identifier
- `name`: Tag name (required)
- `color`: Tag color (integer value)

### Storage

Tasks are serialized and saved locally using `ObjectOutputStream` to a file named `todos.dat`. Data persists across app restarts.

### Notifications

- Uses `AlarmManager` with `setExactAndAllowWhileIdle` for precise timing
- Requires `SCHEDULE_EXACT_ALARM` permission on Android 12+
- Creates a notification channel on Android 8.0+
- Notifications open the app when tapped
- Automatically reschedules after device reboot via `BootReceiver`

### Recurring Tasks

When a recurring task's notification fires:
1. The notification is displayed
2. The next occurrence is calculated based on the repeat type
3. The todo's date is updated in local storage
4. The next notification is scheduled automatically

Repeat options:
- **None**: Task fires once at the scheduled time
- **Hourly**: Repeats every hour
- **Daily**: Repeats every day at the same time
- **Weekly**: Repeats every week at the same time
- **Monthly**: Repeats every month at the same time
- **Yearly**: Repeats every year at the same time

### Navigation & Views

The app uses a navigation drawer with four main sections:

1. **Tasks** (Home): View all todos sorted by scheduled date/time
2. **Tags**: Manage tags (create, edit, delete) with custom colors
3. **By Tag**: View todos grouped by tag with collapsible sections
4. **Settings**: Language selection and clear all data option

## Installation

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Arctic Fox or newer recommended)
- Java Development Kit (JDK) 17 or 21 (JDK 25+ has compatibility issues)
- Android SDK 34 (download via Android Studio SDK Manager)

### Build Commands

**Option 1 - Using Gradle directly:**
```bash
./gradlew assembleDebug
```

**Option 2 - Using a downloaded Gradle (if ./gradlew fails):**
```bash
# Download Gradle 8.13+ manually, then:
/path/to/gradle-8.13/bin/gradle assembleDebug
```

The debug APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### Steps to Install

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd todo-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select `File > Open`
   - Navigate to the `todo-app` folder and select it
   - Click `OK`

3. **Wait for Gradle sync**
   - Android Studio will automatically detect the Gradle wrapper
   - If prompted, click "OK" to download the required Gradle version
   - Wait for dependencies to resolve (this may take a few minutes on first run)

4. **Build the debug APK**
   - In the menu, go to `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - Or press `Ctrl+F9` (Windows/Linux) / `Cmd+F9` (Mac)
   - Wait for the build to complete
   - The APK will be at `app/build/outputs/apk/debug/app-debug.apk`

5. **Install on device/emulator**
   
   **Option A - Run directly from Android Studio:**
   - Connect your Android device via USB (enable USB debugging in Developer Options)
   - Or start an Android emulator (AVD)
   - Click the green "Run" button in the toolbar
   - Select your device/emulator when prompted
   
   **Option B - Install APK manually:**
   - The built APK is located at: `app/build/outputs/apk/debug/app-debug.apk`
   - Transfer the APK to your Android device
   - Open a file manager on the device and tap the APK to install
   - If prompted, enable "Install from unknown sources" in settings

### Troubleshooting

- **Gradle sync fails**: Ensure you have the correct JDK installed (JDK 17 or 21 recommended, NOT JDK 25+)
- **Build errors**: Make sure Android SDK is properly configured in Android Studio (File > Project Structure)
- **Device not detected**: Enable USB debugging on your device and accept the debugging authorization
- **Permission denied**: Check that the app has necessary permissions granted
- **Using ./gradlew fails**: Download Gradle 8.13+ manually and use it directly to build

## Permissions Required

- `POST_NOTIFICATIONS`: For displaying notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: For precise alarm scheduling (Android 12+)
- `USE_EXACT_ALARM`: Legacy exact alarm permission
- `RECEIVE_BOOT_COMPLETED`: For rescheduling alarms after device reboot
- `VIBRATE`: For notification vibration

## License

MIT License - See LICENSE file for details.

## Author

Onyx Leroy
