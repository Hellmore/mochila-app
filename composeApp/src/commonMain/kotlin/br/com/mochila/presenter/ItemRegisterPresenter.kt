package br.com.mochila.presenter

import br.com.mochila.data.UserRepository

interface ItemRegisterView {
    fun showUserName(name: String)
}

class ItemRegisterPresenter(private val view: ItemRegisterView) {

    fun loadUserName(userId: Int) {
        val user = UserRepository.findById(userId)
        view.showUserName(user?.name ?: "Usuário")
    }
}