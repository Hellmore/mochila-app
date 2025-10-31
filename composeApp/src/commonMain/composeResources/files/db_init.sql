-- Habilita o uso de chaves estrangeiras
PRAGMA foreign_keys = ON;

------------------------------------------------
-- Tabelas funcionais
------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS aluno (
    id_aluno INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL UNIQUE,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS administrador (
    id_adm INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL UNIQUE,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS evento (
    id_evento INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    descricao TEXT,
    data_evento DATETIME NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('Agendado','Em andamento','Finalizadoo','Cancelado')) DEFAULT 'Agendado',
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS tarefa (
    id_tarefa INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    descricao TEXT NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('Pendente','Em andamento', 'Cancelada', 'Concluida')) DEFAULT 'Pendente',
    blockers TEXT,
    data_limite DATETIME,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS disciplina (
    id_disciplina INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    nome TEXT NOT NULL,
    frequencia_minima INTEGER NOT NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    hora_aula INTEGER NOT NULL,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS falta (
    id_falta INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    id_disciplina INTEGER NOT NULL,
    data_falta DATE NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('Justificada','Nao Justificada','Registrada')) DEFAULT 'Nao Justificada',
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_disciplina) REFERENCES disciplina (id_disciplina)
        ON DELETE CASCADE ON UPDATE CASCADE
);

------------------------------------------------
-- Tabelas de Permissão
------------------------------------------------
CREATE TABLE IF NOT EXISTS modulo (
    id_modulo INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL UNIQUE,
    descricao TEXT,
    preco DECIMAL(10,2) DEFAULT 0.00,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assinatura (
    id_assinatura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    id_modulo INTEGER NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('Ativa','Inativa','Cancelada')) DEFAULT 'Ativa',
    data_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_fim DATETIME,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_modulo) REFERENCES modulo (id_modulo)
        ON DELETE CASCADE ON UPDATE CASCADE
);

------------------------------------------------
-- Tabelas de Comunicação
------------------------------------------------
CREATE TABLE IF NOT EXISTS notificacao (
    id_notificacao INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    mensagem TEXT NOT NULL,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

------------------------------------------------
-- Tabelas de Pagamento
------------------------------------------------
CREATE TABLE IF NOT EXISTS pagamento (
    id_pagamento INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    id_assinatura INTEGER,
    valor DECIMAL(10,2) NOT NULL,
    moeda TEXT DEFAULT 'BRL',
    metodo TEXT NOT NULL CHECK(metodo IN ('Cartao','Pix','Boleto','PayPal','MercadoPago')),
    status TEXT NOT NULL CHECK(status IN ('Pendente','Pago','Cancelado','Falhou')) DEFAULT 'Pendente',
    gateway TEXT,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_assinatura) REFERENCES assinatura (id_assinatura)
        ON DELETE SET NULL ON UPDATE CASCADE
);

------------------------------------------------
-- Tabelas de Log


------------------------------------------------
CREATE TABLE IF NOT EXISTS log_acao (
    id_acao INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    acao TEXT NOT NULL,
    descricao TEXT,
    ip_origem TEXT,
    tabela_afetada TEXT NOT NULL,
    id_registro_afetado INTEGER,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS log_erro (
    id_erro INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER,
    modulo TEXT NOT NULL,
    mensagem TEXT NOT NULL,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS log_pagamento (
    id_log_pagamento INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pagamento INTEGER NOT NULL,
    status_gateway TEXT NOT NULL,
    mensagem TEXT,
    payload TEXT,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pagamento) REFERENCES pagamento (id_pagamento)
        ON DELETE CASCADE ON UPDATE CASCADE
);

------------------------------------------------
-- Triggers de atualização automática
------------------------------------------------
CREATE TRIGGER update_usuario_timestamp
AFTER UPDATE ON usuario
FOR EACH ROW
BEGIN
    UPDATE usuario SET atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = NEW.id_usuario;
END;

CREATE TRIGGER update_aluno_timestamp
AFTER UPDATE ON aluno
FOR EACH ROW
BEGIN
    UPDATE aluno SET atualizado_em = CURRENT_TIMESTAMP WHERE id_aluno = NEW.id_aluno;
END;

CREATE TRIGGER update_administrador_timestamp
AFTER UPDATE ON administrador
FOR EACH ROW
BEGIN
    UPDATE administrador SET atualizado_em = CURRENT_TIMESTAMP WHERE id_adm = NEW.id_adm;
END;

CREATE TRIGGER update_evento_timestamp
AFTER UPDATE ON evento
FOR EACH ROW
BEGIN
    UPDATE evento SET atualizado_em = CURRENT_TIMESTAMP WHERE id_evento = NEW.id_evento;
END;

CREATE TRIGGER update_tarefa_timestamp
AFTER UPDATE ON tarefa
FOR EACH ROW
BEGIN
    UPDATE tarefa SET atualizado_em = CURRENT_TIMESTAMP WHERE id_tarefa = NEW.id_tarefa;
END;

CREATE TRIGGER update_disciplina_timestamp
AFTER UPDATE ON disciplina
FOR EACH ROW
BEGIN
    UPDATE disciplina SET atualizado_em = CURRENT_TIMESTAMP WHERE id_disciplina = NEW.id_disciplina;
END;

CREATE TRIGGER update_falta_timestamp
AFTER UPDATE ON falta
FOR EACH ROW
BEGIN
    UPDATE falta SET atualizado_em = CURRENT_TIMESTAMP WHERE id_falta = NEW.id_falta;
END;

CREATE TRIGGER update_modulo_timestamp
AFTER UPDATE ON modulo
FOR EACH ROW
BEGIN
    UPDATE modulo SET atualizado_em = CURRENT_TIMESTAMP WHERE id_modulo = NEW.id_modulo;
END;

CREATE TRIGGER update_assinatura_timestamp
AFTER UPDATE ON assinatura
FOR EACH ROW
BEGIN
    UPDATE assinatura SET atualizado_em = CURRENT_TIMESTAMP WHERE id_assinatura = NEW.id_assinatura;
END;

CREATE TRIGGER update_notificacao_timestamp
AFTER UPDATE ON notificacao
FOR EACH ROW
BEGIN
    UPDATE notificacao SET atualizado_em = CURRENT_TIMESTAMP WHERE id_notificacao = NEW.id_notificacao;
END;

CREATE TRIGGER update_pagamento_timestamp
AFTER UPDATE ON pagamento
FOR EACH ROW
BEGIN
    UPDATE pagamento SET atualizado_em = CURRENT_TIMESTAMP WHERE id_pagamento = NEW.id_pagamento;
END;

CREATE TRIGGER IF NOT EXISTS trg_pagamento_confirmado
AFTER UPDATE ON pagamento
FOR EACH ROW
WHEN NEW.status = 'Pago' AND (OLD.status IS NULL OR OLD.status != 'Pago')
BEGIN
    UPDATE assinatura
    SET status = 'Ativa',
        atualizado_em = CURRENT_TIMESTAMP
    WHERE id_assinatura = NEW.id_assinatura;
END;

------------------------------------------------
-- Inserts iniciais para módulos free
------------------------------------------------
INSERT OR IGNORE INTO modulo (nome, descricao, preco) VALUES
('Agenda', 'Gerenciamento de eventos e compromissos', 0.00),
('To Do List', 'Lista de tarefas do usuário', 0.00),
('Controle de Faltas', 'Registro de presença e faltas em disciplinas', 0.00);