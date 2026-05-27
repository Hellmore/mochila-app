package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.model.Subject
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

// Testa detalhe, edicao e exclusao de disciplinas
class SubjectDetailPresenterTest {

    private val view = mockk<SubjectDetailView>(relaxed = true)
    private val presenter = SubjectDetailPresenter(view)

    @Before fun setUp()    { mockkObject(SubjectRepository) }
    @After  fun tearDown() { unmockkAll() }

    private val subject = Subject(id = 2, name = "Física")

    // Carregamento da disciplina
    @Test fun `loadSubject encontrado exibe disciplina`() {
        every { SubjectRepository.findById(2) } returns subject
        presenter.loadSubject(2)
        verify { view.showSubject(subject) }
    }

    @Test fun `loadSubject nao encontrado navega de volta`() {
        every { SubjectRepository.findById(any()) } returns null
        presenter.loadSubject(99)
        verify { view.navigateBack() }
    }

    // Navegacao para edicao
    @Test fun `onEditClicked navega para edicao`() {
        presenter.onEditClicked(subject)
        verify { view.navigateToEdit(subject) }
    }

    // Exclusao de disciplina
    @Test fun `onDeleteConfirmed com sucesso navega para home`() {
        every { SubjectRepository.delete(any(), any()) } returns true
        presenter.onDeleteConfirmed(1, subject)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToHome() }
    }

    @Test fun `onDeleteConfirmed com falha exibe erro`() {
        every { SubjectRepository.delete(any(), any()) } returns false
        presenter.onDeleteConfirmed(1, subject)
        verify { view.showDeleteError() }
    }
}
