package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Subject
import br.com.mochila.model.Task

// Contrato da tela inicial com disciplinas e tarefas pendentes
interface HomeView {
    fun showSubjects(subjects: List<Subject>)
    fun showEmptyState()
    fun navigateToSubjectDetail(subjectId: Int)
    fun showPendingTasks(tasks: List<Task>)
}

// Carrega disciplinas, tarefas e aplica filtros da home
class HomePresenter(private val view: HomeView) {

    fun loadSubjects(userId: Int) {
        // Busca disciplinas do usuario
        val subjects = SubjectRepository.listByUser(userId)
        if (subjects.isEmpty()) {
            view.showEmptyState()
        } else {
            view.showSubjects(subjects)
        }
    }

    fun loadPendingTasks(userId: Int) {
        // Lista tarefas com status pendente
        val pending = TaskRepository.listByUser(userId).filter { it.status == "Pendente" }
        view.showPendingTasks(pending)
    }

    fun onSubjectClicked(subjectId: Int) {
        view.navigateToSubjectDetail(subjectId)
    }

    // Filtra disciplinas por semestre e texto de busca
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

    // Extrai semestres distintos das disciplinas
    fun getSemesters(subjects: List<Subject>): List<String> {
        return subjects
            .mapNotNull { it.semester.takeIf { s -> s.isNotBlank() } }
            .distinct()
            .sorted()
    }
}
