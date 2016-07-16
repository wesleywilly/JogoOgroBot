/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoogrobot;

import java.util.Objects;
import java.util.Random;
import org.json.simple.JSONObject;

/**
 *
 * @author wesley
 */
public class Ogro {
    private String token;
    private AdminJogadores adm;
    private int esmola = 5;
    
    public Ogro(String token){
        this.token = token;
        adm = new AdminJogadores();
    }
    
    public void responder(JSONObject update){
        JSONObject message = (JSONObject) update.get(TelegramAPI.MESSAGE);
        JSONObject chat = (JSONObject) message.get(TelegramAPI.CHAT);
        JSONObject user = (JSONObject) message.get(TelegramAPI.FROM);
        JSONObject inlineQuery = (JSONObject) update.get(TelegramAPI.INLINE_QUERY);

        String chat_id = chat.get(TelegramAPI.ID).toString();
        String message_text = message.get(TelegramAPI.TEXT).toString();
        String user_name = user.get(TelegramAPI.FIRST_NAME).toString();
        //System.out.println(user.toString());
        //String answer_text;

        System.out.println("[OGRO] Telegram de: " + user_name);
        
        JSONObject jogador = adm.getJogador(chat_id, user_name);
        
        
        
        if (TelegramAPI.isCommand(message)) {
            System.out.println("[OGRO] Tipo: Comando: ["+message_text+"]");
            
            //COMANDOS
            if(message_text.equals(Comando.START)){
                bemvindo(jogador);
            }else if(message_text.equals(Comando.APOSTAR)){
                apostar(jogador);  
            }else if(message_text.equals(Comando.CANCELAR)){
                cancelar(jogador);
            }else if(message_text.equals(Comando.ESMOLAR)){
                esmolar(jogador);
            }else{
                TelegramAPI.sendMessage(token, chat_id, "O QUE VOCÊ DISSE? ESCREVE DIREITO SEU TROXA!!!");
            }

        } else {
            System.out.println("[OGRO] Tipo: Mensagem.");
            
            String estado = (String) jogador.get(Jogador.ESTADO);
            int numero;
            
            try{
                numero = Integer.parseInt(message_text);
            }catch(Exception e){
                System.out.println("[OGRO] "+user_name+": "+message_text);
                numero = -1;
            }
            if(numero>0){
               if(estado.equals(Estado.APOSTANDO)){
                   long dinheiro = (long) jogador.get(Jogador.DINHEIRO);
                   if(numero <= dinheiro){
                       jogador.replace(Jogador.APOSTA, numero);
                       jogador.replace(Jogador.ESTADO, Estado.ESCOLHENDO);
                       adm.setJogador(jogador);
                       TelegramAPI.sendMessage(token, chat_id, "AGORA ESCOLHA UM NÚMERO ENTRE 1 E 6, SEU ORELHA SECA!");
                       System.out.println("[OGRO] "+user_name+" apostou "+numero);
                   }else{
                       TelegramAPI.sendMessage(token, chat_id, "TÁ ACHANDO QUE AQUI TEM OTÁRIO? EU SEI QUE VOCÊ NÃO TEM ESSA GRANA!!! PALHAÇO!!!");
                       System.out.println("[OGRO] "+user_name+" recebeu um novo adjetivo.");
                   }
                }else if(estado.equals(Estado.ESCOLHENDO)){
                    if(numero<=6){
                        System.out.println("[OGRO] Jogando o dado para "+user_name+"...");
                         jogarDado(jogador, numero);
                    }else{
                        TelegramAPI.sendMessage(token, chat_id, "VAI TOMA NO MEIO DA PUPILA DO OLHO DO SEU CÚ E ME MANDA O NÚMERO CERTO, PORRA!!!");
                        System.out.println("[OGRO] "+user_name+" recebeu uma sujestão.");
                    }
                }else{
                    TelegramAPI.sendMessage(token, chat_id, "FODA-SE!!!");
                    System.out.println("[OGRO] "+user_name+" foi ignorado.");
                } 
            }else{
                
                if(estado.equals(Estado.APOSTANDO) || estado.equals(Estado.ESCOLHENDO)){
                    TelegramAPI.sendMessage(token, chat_id, "NÃO SABE DIGITAR UM NÚMERO CERTO, SUA ANTA?");
                    System.out.println("[OGRO] "+user_name+" recebeu uma motivação.");
                }else{
                    TelegramAPI.sendMessage(token, chat_id, "NA BOA?   TÔ CAGANDO....");
                    System.out.println("[OGRO] "+user_name+" foi ignorado.");
                }
            }
            
        }
      
        System.out.println("[OGRO] Resposta concluída.");
    }
    
    private void bemvindo(JSONObject jogador){
        TelegramAPI.sendMessage(token,getId(jogador), "CHEGA MAIS, SOU UM ORGRO COM O DADO MAIS HONESTO DESSE MUNDO! QUER APOSTAR?");
    }
    
    private void apostar(JSONObject jogador){
        
        jogador.replace(Jogador.ESTADO, Estado.APOSTANDO);
        String s_dinheiro = Objects.toString(getDinheiro(jogador),null);
        TelegramAPI.sendMessage(token, getId(jogador), "DINHEIRO = "+s_dinheiro);
        TelegramAPI.sendMessage(token, getId(jogador), "QUANTO VOCÊ QUER APOSTAR? OTÁRIO...");
        
        adm.setJogador(jogador);
    }
    
    private void cancelar(JSONObject jogador){
        jogador.replace(Jogador.ESTADO, Estado.PARADO);
        TelegramAPI.sendMessage(token, getId(jogador), "SABIA QUE ERA UM COVARDE DESDE O INÍCIO!!!");
        adm.setJogador(jogador);
    }
    
    private void esmolar(JSONObject jogador){
        long dinheiro = getDinheiro(jogador);
        if(dinheiro<1){
            dinheiro += esmola;
            jogador.replace(Jogador.DINHEIRO, dinheiro);
            TelegramAPI.sendMessage(token, getId(jogador), "TOMA "+esmola+" PILA, SEU BOSTA! SÓ NÃO VAI GASTAR COM PINGA!!!");
            adm.setJogador(jogador);
            System.out.println("[OGRO] Esmola concedida.");
        }else{
            TelegramAPI.sendMessage(token, getId(jogador), "SEU FILHO DE UMA DESCABAÇADA, TEM DINHEIRO E AINDA QUER O MEU???");
            System.out.println("[OGRO] Esmola negada.");
        }
    }
   
    
    private void jogarDado(JSONObject jogador, int escolha){
        int aposta = (int) jogador.get(Jogador.APOSTA);
        long dinheiro = getDinheiro(jogador) - aposta;
        
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int dado = r.nextInt(5)+1;
        
        TelegramAPI.sendMessage(token, getId(jogador), "NÚMERO ESCOLHIDO = "+escolha);
        TelegramAPI.sendMessage(token, getId(jogador), "DADO = "+dado);
        System.out.println("E=["+escolha+"] D=["+dado+"]");
        if(dado == escolha){
            dinheiro += aposta * 6;
            TelegramAPI.sendMessage(token, getId(jogador), "PUTA QUE PARIU!  VOCÊ GANHOU!!!!  É UM LARGO MESMO");
            TelegramAPI.sendMessage(token, getId(jogador), "GANHOU "+ aposta * 6+ " PILA.");
            System.out.println("Ganhou "+aposta*6);
            
        }else{
            TelegramAPI.sendMessage(token, getId(jogador), "PFFF... PERDEU, CUZÃO!!! Ó.... GANHEI "+aposta+" PILA!!!");
            System.out.println("Perdeu "+aposta);
           
        }
        TelegramAPI.sendMessage(token, getId(jogador), "DINHEIRO = "+ dinheiro);
        System.out.println("Saldo = "+dinheiro);
        jogador.replace(Jogador.DINHEIRO, dinheiro);
        jogador.replace(Jogador.ESTADO, Estado.PARADO);
        adm.setJogador(jogador);
        
    }
    
    
    private String getId(JSONObject jogador){
        return (String) jogador.get(TelegramAPI.ID);
    }
    
    private long getDinheiro(JSONObject jogador){
        long dinheiro = (long) jogador.get(Jogador.DINHEIRO);
   
        return dinheiro;
    }
}
