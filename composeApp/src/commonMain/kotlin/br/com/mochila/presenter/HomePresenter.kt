package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Subject
import br.com.mochila.model.Task

interface HomeView {
    fun showSubjects(subjects: List<Subject>)
    fun showEmptyState()
    fun navigateToSubjectDetail(subjectId: Int)
    fun showPendingTasks(tasks: List<Task>)
}

class HomePresenter(private val view: HomeView) {

    fun loadSubjects(userId: Int) {
        val subjects = SubjectRepository.listByUser(userId)
        if (subjects.isEmpty()) {
            view.showEmptyState()
        } else {
            view.showSubjects(subjects)
        }
    }
    fun loadPendingTasks(userId: Int) {
        val pending = TaskRepository.listByUser(userId).filter { it.status == "Pendente" }
        view.showPendingTasks(pending)
    }

    fun onSubjectClicked(subjectId: Int) {
        view.navigateToSubjectDetail(subjectId)
    }

    fun filterSubjects(
        subjects: List<Subject>,
        selectedSemester: String,
        searchText: String
    ): List<Subject> {
        return subjects.filter { subject ->
            val matchesSemester =
                selectedSemester == "Todos" ||
                        subject.semester.equals(selectedSemester, ignoreCase = false)
            val matchesName = subject.name.contains(searchText, ignoreCase = true)
            matchesSemester && matchesName
        }
    }

    fun getSemesters(subjects: List<Subject>): List<String> {
        return subjects
            .mapNotNull { it.semester.takeIf { s -> s.isNotBlank() } }
            .distinct()
            .sorted()
    }
}