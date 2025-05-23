package com.furkan.peertrade.repo

import com.furkan.peertrade.model.Task
import com.furkan.peertrade.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreRepo {
    private val db = FirebaseFirestore.getInstance()

    fun createUser(user: User, onComplete: () -> Unit) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener { onComplete() }
    }


    fun getUser(userId: String?, onResult: (User?) -> Unit) {
        if (userId.isNullOrEmpty()) {
            onResult(null)
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    fun getTotalUserCount(onResult: (Int) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener {
                onResult(it.size())
            }
    }

    fun postTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val taskRef = db.collection("tasks").document()
        val taskWithId = task.copy(id = taskRef.id, involvedUsers = listOf(task.createdBy))
        taskRef.set(taskWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getOpenTasks(onResult: (List<Task>) -> Unit) {
        db.collection("tasks")
            .whereEqualTo("status", "OPEN")
            .get()
            .addOnSuccessListener {
                onResult(it.toObjects(Task::class.java))
            }
    }

    fun acceptTask(taskId: String, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val taskRef = db.collection("tasks").document(taskId)
        taskRef.update(
            mapOf(
                "acceptedBy" to userId,
                "status" to "IN_PROGRESS",
                "acceptedAt" to System.currentTimeMillis(),
                "involvedUsers" to FieldValue.arrayUnion(userId)
            )
        ).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun markTaskFinished(taskId: String, byPoster: Boolean, onComplete: () -> Unit) {
        val taskRef = db.collection("tasks").document(taskId)
        val updateField = if (byPoster) "finishedByPoster" else "finishedByAccepter"
        taskRef.update(updateField, true).addOnSuccessListener {
            taskRef.get().addOnSuccessListener { snapshot ->
                val task = snapshot.toObject(Task::class.java)
                if (task?.finishedByPoster == true && task.finishedByAccepter) {
                    transferCredits(task)
                    taskRef.update("status", "CONFIRMED")
                }
                onComplete()
            }
        }
    }

    fun getUserTasks(userId: String, onResult: (List<Task>) -> Unit) {
        db.collection("tasks")
            .whereArrayContains("involvedUsers", userId)
            .get()
            .addOnSuccessListener {
                onResult(it.toObjects(Task::class.java))
            }
    }
    fun loginUser(userId: String, isFirst5: Boolean, onComplete: (User) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)
            if (user == null) {
                // Create new user
                val newUser = User(id = userId, credits = if (isFirst5) 5 else 0)
                userRef.set(newUser).addOnSuccessListener {
                    onComplete(newUser)
                }
            } else {
                // User already exists
                onComplete(user)
            }
        }
    }
    fun getUserCount(onResult: (Int) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                onResult(result.size())
            }
            .addOnFailureListener {
                onResult(0)
            }
    }

    private fun transferCredits(task: Task) {
        val usersRef = db.collection("users")
        val posterRef = usersRef.document(task.createdBy)
        val completerRef = usersRef.document(task.acceptedBy ?: return)

        db.runTransaction { txn ->
            val poster = txn.get(posterRef).toObject(User::class.java) ?: return@runTransaction
            val completer = txn.get(completerRef).toObject(User::class.java) ?: return@runTransaction
            if (poster.credits >= task.creditAmount) {
                txn.update(posterRef, "credits", poster.credits - task.creditAmount)
                txn.update(completerRef, "credits", completer.credits + task.creditAmount)
            }
        }
    }
}