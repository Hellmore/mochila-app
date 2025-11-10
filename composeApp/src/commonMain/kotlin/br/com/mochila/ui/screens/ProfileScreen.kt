package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UsuarioRepository
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileScreen(
    userId: Int, // ✅ PARÂMETRO ADICIONADO
    onBack: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarNovaSenha by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    // Carrega os dados do usuário ao iniciar a tela
    LaunchedEffect(userId) {
        val usuario = UsuarioRepository.getUsuarioById(userId)
        if (usuario != null) {
            nome = usuario.nome
            email = usuario.email
        }
    }

    // Implementa a lógica de salvar
    fun salvarAlteracoes() {
        if (novaSenha != confirmarNovaSenha) {
            message = "As senhas não coincidem."
            return
        }

        val operacaoBemSucedida = UsuarioRepository.updateUsuario(userId, nome, email, novaSenha)
        message = if (operacaoBemSucedida) {
            "Perfil atualizado com sucesso!"
        } else {
            "Erro ao atualizar o perfil."
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBack)
            }

            Text("Minha Conta", color = RoxoEscuro, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.user), // Placeholder
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier.size(140.dp).clip(CircleShape).background(RoxoClaro).border(2.dp, VerdeLima, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RoxoEscuro)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                        .clickable { /* TODO: Lógica de upload */ }
                ) {
                    // Ícone de edição aqui
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome de Usuário") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = novaSenha,
                onValueChange = { novaSenha = it },
                label = { Text("Nova Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmarNovaSenha,
                onValueChange = { confirmarNovaSenha = it },
                label = { Text("Confirmar Nova Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            message?.let {
                Text(it, color = if (it.contains("sucesso")) Color(0xFF00C853) else Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { salvarAlteracoes() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillMaxWidth(0.9f).height(45.dp)
            ) {
                Text("Salvar Alterações", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
