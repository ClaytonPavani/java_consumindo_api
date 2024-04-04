package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.excecao.ErroDeConversaoDeAnoException;
import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalComBusca {
    public static void main (String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        String busca = "";
        List<Titulo> titulos = new ArrayList<>();

        // Utilizando um builder que irá fazer um padrão de nomenclatura
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) {


            System.out.println("Digite um filme para busca: ");
            busca = leitura.nextLine();

            if (busca.equalsIgnoreCase("sair")){
                break;
            }

            // Criando uma variável que combina o endereço URI + o que foi buscado na variável leitura + API Key
            String endereco = "http://www.omdbapi.com/?t=" + busca.replace(" ", "+") + "&apikey=5d03b634";

            try {
                // Agora o endereço da URI será dinâmico, ele irá alterar de acordo com a busca
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endereco))
                        .build();

                // Vamos precisar de um HTTP response, pois precisamos de uma resposta:
                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());
                // vamos armazenar em uma variável esse reponse body e então vamos exibi-lo println

                String json = response.body();
                System.out.println(json);

                TituloOmdb meuTituloOmdb = gson.fromJson(json, TituloOmdb.class);
                System.out.println(meuTituloOmdb);

                // Vamos criar um tratamento de exception - Try / Catch:

                // Vamos remover o "try" dessa linha e inserir mais acima do código {
                Titulo meuTitulo = new Titulo(meuTituloOmdb);
                // Ele tentando o Try, vamos conseguir exibir o conteúdo da variável meuTitulo:
                System.out.println("Titulo já convertido: ");
                System.out.println(meuTitulo);

                // Aqui vamos fazer o ADD, a adição, àquela lista que criamos vazia no inicio do código
                titulos.add(meuTitulo);

            } catch (NumberFormatException e) {
                System.out.println("Aconteceu um erro: ");
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Algum erro de argumento na busca, verifique o endereço");
            } catch (ErroDeConversaoDeAnoException e) {
                System.out.println(e.getMessage());
            }

        }

        // Imprimindo a Saída da lista com os títulos armazenados
        System.out.println(titulos);

        // Salvando com FileWriter - Em JSON
        FileWriter escrita = new FileWriter("filmes.json");

        // Vamos usar a biblioteca GSON para converter a STRING; To JSON, e passamos a lista como parâmetro
        escrita.write(gson.toJson(titulos));
        escrita.close();

        System.out.println("O programa finalizou corretamente!");


    }
}