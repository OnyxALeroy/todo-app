# To-Do Android Application

A native Android application for managing personal tasks with scheduled reminder notifications.

## Features

- **Create Todos**: Add new tasks with title, description, and scheduled date/time
- **Edit Todos**: Modify existing task details
- **Delete Todos**: Remove unwanted tasks
- **Scheduled Notifications**: Receive reminders at the specified date and time
- **Local Storage**: Tasks persist locally using Android's file storage
- **French Localization**: Full French language support (fr-FR)

## Project Structure

```
app/src/main/java/fr/onyxleroy/to_do/
├── Todo.java                      # Data model for todo items
├── adapters/
│   └── TodoAdapter.java           # RecyclerView adapter for displaying todos
├── dialogs/
│   └── AddTodoDialog.java         # Dialog for adding/editing todos
├── receivers/
│   └── NotificationReceiver.java  # BroadcastReceiver for handling notifications
└── utils/
    ├── NotificationHelper.java    # Utility for scheduling/canceling notifications
    └── TodoStorageManager.java    # Local file storage management
```

## Technical Details

### Dependencies

- **AndroidX**: Core Android compatibility libraries
- **Material Components**: Material Design UI components
- **Lombok**: Annotation processing for getter/setter generation
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

### Storage

Tasks are serialized and saved locally using `ObjectOutputStream` to a file named `todos.dat`. Data persists across app restarts.

### Notifications

- Uses `AlarmManager` with `setExactAndAllowWhileIdle` for precise timing
- Requires `SCHEDULE_EXACT_ALARM` permission on Android 12+
- Creates a notification channel on Android 8.0+
- Notifications open the app when tapped

### UI Components

- **MainActivity**: Main screen with RecyclerView list (implementation required)
- **AddTodoDialog**: Modal dialog with title, description, and date/time picker
- **item_todo.xml**: Card layout for individual todo items

## Strings (French)

| Key | Value |
|-----|-------|
| app_name | To Do |
| add_todo | Rajouter un Todo |
| no_todos | Appuyez sur '+' pour ajouter un Todo ! |
| todo_title | Titre |
| todo_description | Description (optionnelle) |
| select_date_time | Choisir une date & Heure |
| save | Enregistrer |
| cancel | Annuler |
| delete | Supprimer |
| edit | Modifier |

## Requirements

- Android Studio Arctic Fox or newer
- Android SDK 21+ (minimum)
- Android SDK 34 (target)
- Gradle 8.0+

## Setup

1. Clone the repository
2. Open in Android Studio
3. Ensure build.gradle files are configured
4. Build and run on device/emulator

## Permissions Required

- `POST_NOTIFICATIONS`: For displaying notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: For precise alarm scheduling (Android 12+)
- `USE_EXACT_ALARM`: Legacy exact alarm permission
- `RECEIVE_BOOT_COMPLETED`: For rescheduling alarms after device reboot
- `VIBRATE`: For notification vibration

## Build Configuration

Add the following to your `build.gradle` (app level):

```groovy
dependencies {
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'
}
```

## License

MIT License - See LICENSE file for details.

## Author

Onyx Leroy
