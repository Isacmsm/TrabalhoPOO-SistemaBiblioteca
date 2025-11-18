//POR FAVOR NÃO DELETAR COMENTARIOS DO CODIGO, EU NAO VOU LEMBRAR ONDE ESTA CADA COISA E O QUE O CODIGO FAZ SE DELETAR.
//POR FAVOR NÃO DELETAR COMENTARIOS DO CODIGO, EU NAO VOU LEMBRAR ONDE ESTA CADA COISA E O QUE O CODIGO FAZ SE DELETAR.
//POR FAVOR NÃO DELETAR COMENTARIOS DO CODIGO, EU NAO VOU LEMBRAR ONDE ESTA CADA COISA E O QUE O CODIGO FAZ SE DELETAR.
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.google.gson.*; //Ta mostrando erro, mas o codigo compila e roda normalmente no terminal, so o ide que ta zuado, não mexer nisso por favor.

class Livro {
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;

    public Livro(String titulo, String autor, String isbn, String categoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public String getCategoria() { return categoria; }

    @Override
    public String toString() {
        return "Título: " + titulo + " | Autor: " + autor + " | ISBN: " + isbn + " | Categoria: " + categoria;
    }
}

class Biblioteca {
    private List<Livro> livros = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public Biblioteca() {
        livros = new ArrayList<>();
        System.out.println("Sistema iniciado com lista de livros vazia.");
        // Descomentar a linha abaixo caso a API esteja indisponível, por favor não tirar, serve pra caso a api caia.
        // gerarLivrosTeste();
    }

    // Aqui carrega livros de várias categorias da API (A categoria historia so tem 217 livros no total, não testei o limite das outras categorias. Deu preguiça de testar tudo, se quiser mais livros que 800 você testa georgio/felipe.)
    public void importarLivrosMultiplasCategorias() {
        livros.clear();
        livros.addAll(carregarLivrosDaAPI("science_fiction", 300));
        livros.addAll(carregarLivrosDaAPI("fantasy", 300));
        livros.addAll(carregarLivrosDaAPI("history", 200));
        System.out.println("Livros carregados da API: " + livros.size());
    }

    // Importa até o limite usando páginas (offsets) de 100 livros por vez (por favor eu não sei como eu fiz funcionar isso direito, mas funciona, ok? Por favor não mexer, se quebrar so deus sabe como concertar!)
    public List<Livro> carregarLivrosDaAPI(String categoriaAPI, int limite) {
        List<Livro> lista = new ArrayList<>();
        int offset = 0;
        int porPagina = 100;
        while (lista.size() < limite) {
            int restante = limite - lista.size();
            int pegar = Math.min(porPagina, restante);
            try {
                URL url = new URL("https://openlibrary.org/subjects/" + categoriaAPI + ".json?limit=" + pegar + "&offset=" + offset);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");
                if (conexao.getResponseCode() == 200) {
                    InputStreamReader reader = new InputStreamReader(conexao.getInputStream());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonArray obras = json.getAsJsonArray("works");
                    if (obras.size() == 0) break; // Sem mais resultados
                    for (JsonElement elem : obras) {
                        JsonObject obj = elem.getAsJsonObject();
                        String titulo = obj.get("title").getAsString();
                        String autor = obj.has("authors") && obj.get("authors").getAsJsonArray().size() > 0
                            ? obj.get("authors").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString()
                            : "Autor desconhecido";
                        String isbn = obj.has("cover_id") ? "OLID" + obj.get("cover_id").getAsString() : "N/A";
                        String categoria = categoriaAPI.substring(0,1).toUpperCase() + categoriaAPI.substring(1).replace("_", " ");
                        lista.add(new Livro(titulo, autor, isbn, categoria));
                        if (lista.size() >= limite) break;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar categoria " + categoriaAPI + ": " + e.getMessage());
                e.printStackTrace();
                break;
            }
            offset += pegar;
        }
        return lista;
    }

    public void gerarLivrosTeste() {
        livros.clear();
        for (int i = 1; i <= 500; i++) {
            String titulo = "Livro " + i;
            String autor = "Autor " + ((i % 30) + 1);
            String isbn = "ISBN" + (100000 + i);
            String categoria = (i % 5 == 0) ? "Ciência" :
                               (i % 5 == 1) ? "Tecnologia" :
                               (i % 5 == 2) ? "Ficção" :
                               (i % 5 == 3) ? "História" : "Arte";
            livros.add(new Livro(titulo, autor, isbn, categoria));
        }
        System.out.println("Lista preenchida com livros de teste: " + livros.size());
    }

    public void cadastrarLivro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Categoria: ");
        String categoria = scanner.nextLine();
        livros.add(new Livro(titulo, autor, isbn, categoria));
        System.out.println("Livro cadastrado com sucesso!");
    }

    public void removerLivro() {
        System.out.print("Informe o ISBN do livro para remover: ");
        String isbn = scanner.nextLine();
        Livro livroRemovido = null;
        for (Livro livro : livros) {
            if (livro.getIsbn().equalsIgnoreCase(isbn)) {
                livroRemovido = livro;
                break;
            }
        }
        if (livroRemovido != null) {
            livros.remove(livroRemovido);
            System.out.println("Livro removido!");
        } else {
            System.out.println("Livro não encontrado.");
        }
    }

    // O menu ta aqui.
    public void buscarPorCampo() {
        while (true) {
            System.out.println("Buscar por: 1-Título, 2-Autor, 3-ISBN, 4-Categoria");
            int opcao = Integer.parseInt(scanner.nextLine());
            System.out.print("Digite o termo de busca: ");
            String termo = scanner.nextLine();

            List<Livro> resultados = new ArrayList<>();

            // Sequencial
            long inicioSeq = System.nanoTime();
            for (Livro livro : livros) {
                switch (opcao) {
                    case 1:
                        if (livro.getTitulo().equalsIgnoreCase(termo)) resultados.add(livro);
                        break;
                    case 2:
                        if (livro.getAutor().equalsIgnoreCase(termo)) resultados.add(livro);
                        break;
                    case 3:
                        if (livro.getIsbn().equalsIgnoreCase(termo)) resultados.add(livro);
                        break;
                    case 4:
                        if (livro.getCategoria().equalsIgnoreCase(termo)) resultados.add(livro);
                        break;
                }
            }
            long fimSeq = System.nanoTime();
            System.out.printf("Busca sequencial encontrou %d livros em %.3f ms\n", resultados.size(), (fimSeq-inicioSeq)/1e6);
            exibirLista(resultados);

            // Binária
            List<Livro> ordenados = new ArrayList<>(livros);
            Comparator<Livro> comp;
            switch (opcao) {
                case 1:
                    comp = Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER);
                    break;
                case 2:
                    comp = Comparator.comparing(Livro::getAutor, String.CASE_INSENSITIVE_ORDER);
                    break;
                case 3:
                    comp = Comparator.comparing(Livro::getIsbn, String.CASE_INSENSITIVE_ORDER);
                    break;
                case 4:
                    comp = Comparator.comparing(Livro::getCategoria, String.CASE_INSENSITIVE_ORDER);
                    break;
                default:
                    comp = null;
            }
            ordenados.sort(comp);

            long inicioBin = System.nanoTime();
            int idx = buscaBinariaPorCampo(ordenados, termo, opcao);
            long fimBin = System.nanoTime();
            if (idx >= 0) {
                System.out.println("Busca binária encontrou 1 livro (primeira ocorrência) em " +
                    String.format("%.3f", (fimBin-inicioBin)/1e6) + " ms");
                System.out.println(ordenados.get(idx));
            } else {
                System.out.println("Busca binária não encontrou nenhum livro. Tempo: " +
                    String.format("%.3f", (fimBin-inicioBin)/1e6) + " ms");
            }

            // Mini menu so para não voltar para o outro menu grande automaticamente.
            System.out.println("1 - Buscar novo livro");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha: ");
            int subopcao;
            try {
                subopcao = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                subopcao = 0;
            }
            if (subopcao == 1) {
                continue; // Repete a busca
            } else {
                break; // Volta ao menu principal
            }
        }
    }

    public int buscaBinariaPorCampo(List<Livro> lista, String termo, int campo) {
        int ini = 0, fim = lista.size()-1;
        while (ini <= fim) {
            int meio = (ini + fim) / 2;
            String valor;
            switch (campo) {
                case 1: valor = lista.get(meio).getTitulo(); break;
                case 2: valor = lista.get(meio).getAutor(); break;
                case 3: valor = lista.get(meio).getIsbn(); break;
                case 4: valor = lista.get(meio).getCategoria(); break;
                default: valor = "";
            }
            int cmp = valor.compareToIgnoreCase(termo);
            if (cmp == 0) return meio;
            if (cmp < 0) ini = meio + 1;
            else fim = meio - 1;
        }
        return -1;
    }

    public void listarLivros() {
        System.out.println("Listagem completa de livros:");
        exibirLista(livros);
    }

    public void filtrarPorCategoria() {
        System.out.print("Informe a categoria: ");
        String categoria = scanner.nextLine();
        List<Livro> filtrados = new ArrayList<>();
        for (Livro livro : livros) {
            if (livro.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(livro);
            }
        }
        exibirLista(filtrados);
    }

    public void relatorioEstatistico() {
        System.out.println("Número total de livros: " + livros.size());
        Map<String, Integer> estatisticas = new HashMap<>();
        for (Livro livro : livros) {
            estatisticas.put(livro.getCategoria(),
                estatisticas.getOrDefault(livro.getCategoria(), 0) + 1);
        }
        for (String categoria : estatisticas.keySet()) {
            System.out.println("Livros na categoria '" + categoria + "': " + estatisticas.get(categoria));
        }
    }

    private void exibirLista(List<Livro> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
        } else {
            for (Livro livro : lista) {
                System.out.println(livro);
            }
        }
    }

    public void limparLista() {
        livros.clear();
        System.out.println("Lista de livros esvaziada!");
    }

    public void importarLivrosAPI() {
        importarLivrosMultiplasCategorias();
    }

    public void menu() {
        while (true) {
            System.out.println("\nSistema Biblioteca...");
            System.out.println("1 - Cadastrar Livro");
            System.out.println("2 - Remover Livro");
            System.out.println("3 - Buscar Livro por Campo");
            System.out.println("4 - Listar Todos os Livros");
            System.out.println("5 - Filtrar Livros por Categoria");
            System.out.println("6 - Relatório Estatístico");
            System.out.println("7 - Importar Livros da API");
            System.out.println("8 - Limpar Lista de Livros");
            System.out.println("9 - Gerar Livros de Teste");
            System.out.println("0 - Sair");
            System.out.print("Escolha a opção: ");

            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1: cadastrarLivro(); break;
                case 2: removerLivro(); break;
                case 3: buscarPorCampo(); break;
                case 4: listarLivros(); break;
                case 5: filtrarPorCategoria(); break;
                case 6: relatorioEstatistico(); break;
                case 7: importarLivrosAPI(); break;
                case 8: limparLista(); break;
                case 9: gerarLivrosTeste(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.menu();
    }
}
