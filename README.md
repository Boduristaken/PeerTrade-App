# PeerTrade App

PeerTrade is a Kotlin-based Android app built with Jetpack Compose and Firebase. It allows users to post and accept tasks in exchange for credits within a campus environment.

## 🔧 Features

- **Login Options**: Anonymous and Email authentication via Firebase
- **Task Posting**: Users can post tasks by spending credits
- **Task Browsing**: Users browse and accept tasks posted by others
- **Credit System**: LoopCoin-based zero-sum credit economy
- **Confirmation Flow**: Both parties must confirm task completion for credit transfer
- **User Separation**: Users cannot accept their own tasks
- **Logout Support**: Basic logout for email accounts

---

## 📁 Key Files & Structure

| Path | Purpose |
|------|---------|
| `app/src/main/java/com/furkan/peertrade/MainActivity.kt` | Entry point, sets up login routing and screens |
| `app/src/main/java/com/furkan/peertrade/ui/LoginScreen.kt` | Handles email + anonymous login UI and logic |
| `app/src/main/java/com/furkan/peertrade/ui/MainScreen.kt` | App scaffold, navigation bar, tab-based routing |
| `app/src/main/java/com/furkan/peertrade/ui/PostTaskScreen.kt` | Screen for posting a task |
| `app/src/main/java/com/furkan/peertrade/ui/OpenTasksScreen.kt` | Task feed: browsing and accepting others' tasks |
| `app/src/main/java/com/furkan/peertrade/ui/MyTasksScreen.kt` | Shows tasks user has posted and accepted |
| `app/src/main/java/com/furkan/peertrade/repo/FirestoreRepo.kt` | All Firestore-related operations: users, tasks, credits |
| `app/src/main/java/com/furkan/peertrade/model/User.kt` | `User` data class with credits and auth ID |
| `app/src/main/java/com/furkan/peertrade/model/Task.kt` | `Task` data class with metadata and status |
| `app/src/main/java/com/furkan/peertrade/viewmodel/UserViewModel.kt` | Shared state of the currently logged-in user |

---

## 🔐 Firebase Setup

To run this project:

1. Add your `google-services.json` file to:  
   `app/google-services.json`

2. Enable the following in Firebase Console:
   - **Authentication > Sign-in Method**:
     - ✅ Email/Password
     - ✅ Anonymous

3. Firestore Rules:

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /tasks/{taskId} {
      allow read, write: if request.auth != null;
    }
  }
}
