package org.decembrist.exceptions

class TaskNotFoundException(taskName: String): Exception("$taskName not found")