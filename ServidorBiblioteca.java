package com.trabalho;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServidorBiblioteca {
    private static final int PORT = 12345;
    private List<Livro> livros;
    private File jsonFile = new File("livros.json");

    public ServidorBiblioteca() {
        carregarLivros();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    String operacao = (String) in.readObject();
                    System.out.println("Operação recebida: " + operacao);

                    switch (operacao) {
                        case "listar":
                            out.writeObject(livros);
                            break;
                        case "alugar":
                            String tituloParaAlugar = (String) in.readObject();
                            alugarLivro(tituloParaAlugar);
                            out.writeObject("Livro alugado com sucesso.");
                            break;
                        case "devolver":
                            String tituloParaDevolver = (String) in.readObject();
                            devolverLivro(tituloParaDevolver);
                            out.writeObject("Livro devolvido com sucesso.");
                            break;
                        case "cadastrar":
                            Livro novoLivro = (Livro) in.readObject();
                            cadastrarLivro(novoLivro);
                            out.writeObject("Livro cadastrado com sucesso.");
                            break;
                        default:
                            out.writeObject("Operação inválida.");
                            break;
                    }
                    salvarLivros();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarLivros() {
        try {
            if (!jsonFile.exists() || jsonFile.length() == 0) {
                System.out.println("Arquivo livros.json não encontrado ou está vazio.");
                livros = new ArrayList<>();
                return;
            }
            
            String content = new String(Files.readAllBytes(Paths.get("livros.json")));
            JSONObject jsonObject = new JSONObject(new JSONTokener(content));
            JSONArray jsonArray = jsonObject.getJSONArray("livros");

            livros = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Livro livro = new Livro(
                        obj.getString("titulo"),
                        obj.getString("autor"),
                        obj.getString("genero"),
                        obj.getInt("exemplares")
                );
                livros.add(livro);
            }
            System.out.println("Livros carregados: " + livros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarLivros() {
        try (PrintWriter out = new PrintWriter(jsonFile)) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (Livro livro : livros) {
                JSONObject obj = new JSONObject();
                obj.put("titulo", livro.getTitulo());
                obj.put("autor", livro.getAutor());
                obj.put("genero", livro.getGenero());
                obj.put("exemplares", livro.getExemplares());
                jsonArray.put(obj);
            }

            jsonObject.put("livros", jsonArray);
            out.write(jsonObject.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void alugarLivro(String titulo) {
        for (Livro livro : livros) {
            if (livro.getTitulo().equalsIgnoreCase(titulo) && livro.getExemplares() > 0) {
                livro.setExemplares(livro.getExemplares() - 1);
                break;
            }
        }
    }

    private void devolverLivro(String titulo) {
        for (Livro livro : livros) {
            if (livro.getTitulo().equalsIgnoreCase(titulo)) {
                livro.setExemplares(livro.getExemplares() + 1);
                break;
            }
        }
    }

    private void cadastrarLivro(Livro novoLivro) {
        livros.add(novoLivro);
    }

    public static void main(String[] args) {
        ServidorBiblioteca servidor = new ServidorBiblioteca();
        servidor.iniciar();
    }
}
