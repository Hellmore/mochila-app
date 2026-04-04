package br.com.mochila.presenter

import br.com.mochila.data.UserRepository

interface MenuView {
    fun showUserName(name: String)
}

class MenuPresenter(private val view: MenuView) {

    fun loadUserName(userId: Int) {
        val user = UserRepository.findById(userId)
        view.showUserName(user?.name ?: "Usuário")
    }
}