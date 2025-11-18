# Trabalho POO - Sistema Biblioteca

Sistema orientado a objetos para gerenciar livros de uma biblioteca via console. Permite cadastro, busca com comparação de métodos (sequencial e binário), importação de livros de API, estatísticas, filtragem e outras funções.

## Equipe

- **LÍDER:** João Felipe - 01817666
- **PROGRAMADOR I:** Isac Manoel - 01797008
- **PROGRAMADOR II:** Georgio William - 01805461
- **REDATOR:** Mário Sérgio - 01819340
- **AUXILIAR:** Eduardo Pereira - 01809366

## Estrutura do Projeto

BIBLIOTECA/
├── .vscode/        # (Configurações do VS Code, opcional)
├── bin/            # (Arquivos compilados .class, gerados automaticamente)
├── lib/            # (Dependências externas, por exemplo gson-2.10.1.jar)
├── src/            # (Código fonte Java)
│    └── Main.java
└── README.md       # Documentação do projeto

## Execução

### Com Visual Studio Code (VS Code)
- Abra a pasta principal no VS Code.
- Abra o arquivo `Main.java`.
- Pressione **Ctrl+F5** para rodar o sistema (Java Extension Pack recomendado).
- Não é necessário passar argumentos ou comandos extras; dependências já estão incluídas na pasta `lib`.

### Pelo terminal (Windows)

javac -cp ".;lib\gson-2.10.1.jar" -d bin src\Main.java
java -cp ".;lib\gson-2.10.1.jar;bin" Main

### Pelo terminal (Linux/Mac)

javac -cp ".:lib/gson-2.10.1.jar" -d bin src/Main.java
java -cp ".:lib/gson-2.10.1.jar:bin" Main

## Funcionalidades

- **Cadastrar, remover e listar livros**
- **Filtrar por categoria**
- **Relatório estatístico**
- **Busca por campo** (título, autor, ISBN, categoria)
    - Exibe tempo de busca sequencial e binária
    - Mini-menu após busca: possibilidade de repetir várias vezes
- **Importar livros reais da API OpenLibrary**  
    - Até 800 livros: 300 "Science Fiction", 300 "Fantasy", até 200 "History"
    - Lista inicia sempre vazia, importar pelo menu
- **Gerar livros de teste** offline
- **Limpar toda a lista**

## Conceitos de Programação Orientada a Objetos (POO) Utilizados

O código utiliza diversos fundamentos de POO, incluindo:

- **Classes e Objetos:**  
    - Uso das classes `Livro`, `Biblioteca` e `Main`. Cada livro é um objeto da classe `Livro`.
- **Encapsulamento:**  
    - Os atributos do livro (`titulo`, `autor`, `isbn`, `categoria`) são privados e acessados via métodos públicos (`getTitulo()`, etc).
- **Métodos:**  
    - Vários métodos para operações da biblioteca: adicionar, remover, buscar, importar, limpar, relatar estatísticas.
- **Construtor:**  
    - Classe `Livro` possui construtor para definir os dados ao instanciar novos livros.
- **Coleções (ArrayList):**  
    - Utiliza `ArrayList` para modelar a lista dinâmica de livros.
- **Composição:**  
    - A classe `Biblioteca` contém diversos objetos do tipo `Livro` em sua estrutura interna.
- **Legibilidade e Modularização:**  
    - Funções separadas para cada operação, tornando o código mais reutilizável e compreensível.
- **Fluxo de controle:**  
    - Estruturas de repetição e de decisão para controle do menu e da lógica das buscas.

## Observações

- Para rodar, é necessário manter o arquivo `gson-2.10.1.jar` na pasta `lib`.
- Caso algum editor (VS Code) mostre avisos sobre dependências, basta seguir a execução conforme acima.
- A busca na API pode retornar menos livros em algumas categorias, dependendo dos dados disponíveis.

---

