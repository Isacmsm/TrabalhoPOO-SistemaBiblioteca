import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.google.gson.*;

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
        try {
            importarLivrosMultiplasCategorias();
            System.out.println("Livros carregados da API: " + livros.size());
        } catch (Exception e) {
            System.out.println("Erro ao importar livros da API: " + e.getMessage());
            e.printStackTrace();
        }
        if (livros.isEmpty()) {
            System.out.println("API ou conexão não retornou livros. Gerando livros de teste...");
            gerarLivrosTeste();
            System.out.println("Usando livros de teste: " + livros.size());
        }
    }

    // Carrega até 100 livros de cada categoria
    public void importarLivrosMultiplasCategorias() {
        livros.clear();
        livros.addAll(carregarLivrosDaAPI("science_fiction"));
        livros.addAll(carregarLivrosDaAPI("fantasy"));
        livros.addAll(carregarLivrosDaAPI("history"));
    }

    public List<Livro> carregarLivrosDaAPI(String categoriaAPI) {
        List<Livro> lista = new ArrayList<>();
        try {
            URL url = new URL("https://openlibrary.org/subjects/" + categoriaAPI + ".json?limit=100");
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            if (conexao.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conexao.getInputStream());
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray obras = json.getAsJsonArray("works");
                for (JsonElement elem : obras) {
                    JsonObject obj = elem.getAsJsonObject();
                    String titulo = obj.get("title").getAsString();
                    String autor = obj.has("authors") && obj.get("authors").getAsJsonArray().size() > 0
                       ? obj.get("authors").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString()
                       : "Autor desconhecido";
                    String isbn = obj.has("cover_id") ? "OLID" + obj.get("cover_id").getAsString() : "N/A";
                    String categoria = categoriaAPI.substring(0,1).toUpperCase() + categoriaAPI.substring(1).replace("_"," ");
                    lista.add(new Livro(titulo, autor, isbn, categoria));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar categoria " + categoriaAPI + ": " + e.getMessage());
            e.printStackTrace();
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

    public void buscarPorCampo() {
        System.out.println("Buscar por: 1-Título, 2-Autor, 3-ISBN, 4-Categoria");
        int opcao = Integer.parseInt(scanner.nextLine());
        System.out.print("Digite o termo de busca: ");
        String termo = scanner.nextLine();

        List<Livro> resultados = new ArrayList<>();

        // Busca sequencial
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

        // Busca binária (por campo)
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
    }

    // Busca binária para qualquer campo (case insensitive)
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

    // Menu: opção para importar livros da API novamente
    public void importarLivrosAPI() {
        importarLivrosMultiplasCategorias();
        System.out.println("Livros carregados da API: " + livros.size());
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
