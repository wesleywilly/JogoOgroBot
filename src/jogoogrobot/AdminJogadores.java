/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoogrobot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author wesley
 */
public class AdminJogadores {

    private final String ARQUIVO = "jogadores.json";
    private final String JOGADORES = "jogadores";
    

    private int dinheiro_inicial = 50;

    private JSONArray jogadores;
    private int pos;

    public AdminJogadores() {
        carregaJogadores();
    }
    
    public void setJogador(JSONObject jogador){
        String id = (String) jogador.get(TelegramAPI.ID);
        if(existe(id)){
            jogadores.set(pos, jogador);
            System.out.println("[AdminJogadores] Jogador atualizado!");
            salvaJogadores();
        }else{
            System.out.println("[AdminJogadores] Uso errado desse método! O mesmo serve somente para jogadores já cadastrados.");
        }
    }

    public JSONObject getJogador(String id, String nome) {
        JSONObject jogador;
        if (existe(id)) {
            jogador = (JSONObject) jogadores.get(pos);
        } else {
            jogador = novoJogador(id, nome, dinheiro_inicial);
        }

        return jogador;
    }

    private boolean existe(String id) {
        boolean encontrado = false;
        for (int i = 0; i < jogadores.size() && !encontrado; i++) {
            JSONObject jogador = (JSONObject) jogadores.get(i);
            String jid = jogador.get(TelegramAPI.ID).toString();
            if (id.equals(jid)) {
                pos = i;
                encontrado = true;
            }
        }
        return encontrado;
    }

    private JSONObject novoJogador(String id, String nome, int dinheiro) {
        JSONObject jogador = new JSONObject();
        jogador.put(TelegramAPI.ID, id);
        jogador.put(TelegramAPI.FIRST_NAME, nome);
        jogador.put(Jogador.DINHEIRO, dinheiro);
        jogador.put(Jogador.APOSTA, 0);
        jogador.put(Jogador.ESTADO, Estado.PARADO);

        System.out.println("[AdminJogadores] Novo jogador cadastrado ("+nome+")");
        
        addJogador(jogador);

        return jogador;
    }

    private void carregaJogadores() {

        if (FileUtilities.fileExist("/Users/wesley/",ARQUIVO)) {
            try {
                JSONObject arquivo = (JSONObject) FileUtilities.load(ARQUIVO);
                jogadores = (JSONArray) arquivo.get(JOGADORES);
            } catch (Exception e) {
                System.out.println("[AdminJogadores] Nenhum arquivo de jogadores encontrado.");
            }

        } else {

            System.out.println("[AdminJogadores] Criando um arquivo de jogadores");
            jogadores = new JSONArray();
            salvaJogadores();
        }

    }

    private void salvaJogadores() {
        JSONObject arquivo = new JSONObject();
        arquivo.put(JOGADORES, jogadores);
        FileUtilities.save(arquivo, ARQUIVO);
        System.out.println("[AdminJogadores] Jogadores salvos em arquivo.");
    }

    private void addJogador(JSONObject jogador) {
        jogadores.add(jogador);
        salvaJogadores();
    }

}
