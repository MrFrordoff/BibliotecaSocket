package com.trabalho;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClienteBiblioteca {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Escolha uma operação: listar, alugar, devolver, cadastrar, sair");
                String operacao = scanner.nextLine();

                if (operacao.equalsIgnoreCase("sair")) {
                    break;
                }

                out.writeObject(operacao);
                switch (operacao.toLowerCase()) {
                    case "listar":
                        List<Livro> livros = (List<Livro>) in.readObject();
                        if (livros != null) {
                            livros.forEach(System.out::println);
                        } else {
                            System.out.println("Nenhum livro encontrado.");
                        }
                        break;
                    case "alugar":
                        System.out.println("Digite o título do livro para alugar:");
                        String tituloParaAlugar = scanner.nextLine();
                        out.writeObject(tituloParaAlugar);
                        String respostaAlugar = (String) in.readObject();
                        System.out.println(respostaAlugar);
                        break;
                    case "devolver":
                        System.out.println("Digite o título do livro para devolver:");
                        String tituloParaDevolver = scanner.nextLine();
                        out.writeObject(tituloParaDevolver);
                        String respostaDevolver = (String) in.readObject();
                        System.out.println(respostaDevolver);
                        break;
                    case "cadastrar":
                        System.out.println("Digite o título do novo livro:");
                        String titulo = scanner.nextLine();
                        System.out.println("Digite o autor do novo livro:");
                        String autor = scanner.nextLine();
                        System.out.println("Digite o gênero do novo livro:");
                        String genero = scanner.nextLine();
                        System.out.println("Digite o número de exemplares do novo livro:");
                        int exemplares = Integer.parseInt(scanner.nextLine());

                        Livro novoLivro = new Livro(titulo, autor, genero, exemplares);
                        out.writeObject(novoLivro);
                        String respostaCadastrar = (String) in.readObject();
                        System.out.println(respostaCadastrar);
                        break;
                    default:
                        System.out.println("Operação inválida.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
