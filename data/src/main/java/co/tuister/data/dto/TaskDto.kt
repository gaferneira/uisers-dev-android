package co.tuister.data.dto

import co.tuister.domain.entities.Task
import java.io.Serializable

class TaskDto(
    var title: String = "",
    var description: String? = null,
    var dueDate: Long? = null,
    // DO, DOING, DONE
    var status: Int = 0,
    var reminder: Int? = null,
    var materialColor: Int = 1
) : Serializable

fun TaskDto.toEntity(path: String) = Task(path, title, description, dueDate, status, reminder, materialColor)

fun Task.toDTO() = TaskDto(title, description, dueDate, status, reminder, materialColor)
