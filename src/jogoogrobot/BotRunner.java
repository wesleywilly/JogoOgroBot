/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoogrobot;

import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author wesley
 */
public class BotRunner implements Runnable {

    private static final String ARQUIVO = "config.json";
    private static final String CONFIG = "config";

    private JSONObject config;
    public String token;
    JSONObject me;
    long time = 1000;

    @Override
    public void run() {
        System.out.println("[BotRunner] Configurando... ");
        JSONObject result = TelegramAPI.getMe(token);
        me = (JSONObject) result.get("result");
        getConfig();
        Ogro ogro = new Ogro(token);
        
        System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Conectando ao Telegram...");
        
        JSONArray updates = (JSONArray) TelegramAPI.getUpdates(token,getLastUpdate()).get(TelegramAPI.RESULT);
        
        JSONObject update;
        
        
        long last_update = 0;
        //long last_date = 0;
        

        if (updates.size() > 0) {
            update = (JSONObject) updates.get(updates.size() - 1);
            last_update = getUpdate(update);
            //last_date = getDate(update);
        }
        
        
        while (true) {
            try {
                System.out.print("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Buscando Atualizações...\r");
                updates = (JSONArray) TelegramAPI.getUpdates(token,getLastUpdate()).get(TelegramAPI.RESULT);
                boolean wait_more = false;
                if (updates.size() > 0) {
                    
                    for (int i = 0; i < updates.size(); i++) {
                        update = (JSONObject) updates.get(i);

                        
                        long current_update = getUpdate(update);

                        
                        if (getLastUpdate() < current_update) {
                            JSONObject message = (JSONObject) update.get(TelegramAPI.MESSAGE);
                            System.out.println("==================================\n[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Telegram Recebido!");

                            ogro.responder(update);
                            time = 1000;
                            

                            last_update = current_update;
                            setUpdate(current_update);
                        } else {

                            wait_more = true;

                        }

                    }

                }
                if (wait_more && time < 5000) {
                    time += 1;
                    wait_more = false;
                }

                System.out.print("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Esperando " + time/1000+"s                    \r");
                Thread.sleep(time);

            } catch (InterruptedException ex) {
                System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Conection Error!");
                //Thread.sleep(5000);
                Logger.getLogger(BotRunner.class.getName()).log(Level.SEVERE, null, ex);

            } catch (Exception e) {
                System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Conection Error!");
                //Thread.sleep(5000);
                e.printStackTrace();

            }
            updates = new JSONArray();
        }

    }

    private long getUpdate(JSONObject update) {
        return (long) update.get(TelegramAPI.UPDATE_ID);
    }

    private long getDate(JSONObject update) {
        JSONObject message = (JSONObject) update.get(TelegramAPI.MESSAGE);
        return (long) message.get(TelegramAPI.DATE);
    }

    private boolean getConfig() {
        boolean novo = false;
        if (FileUtilities.fileExist("/Users/wesley/", ARQUIVO)) {
            try {
               
                config = (JSONObject) FileUtilities.load(ARQUIVO);
                 System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Arquivo config encontrado.");
            } catch (Exception e) {
                System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Nenhum arquivo config encontrado.");
            }

        } else {

            System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Criando um arquivo config");
            config = new JSONObject();
            saveConfig();
            novo = true;
        }
            return novo;
    }


    private void saveConfig() {
        if (FileUtilities.save(config, ARQUIVO)) {
            System.out.println("[" + me.get(TelegramAPI.FIRST_NAME).toString() + "] Configurações salvas.\n");
        }
    }

    private void restartUpdate(JSONArray updates) {
        long maior_update = 0;
        if (updates.size() > 0) {
            for (int i = 0; i < updates.size(); i++) {
                JSONObject update = (JSONObject) updates.get(i);
                long current_update = getUpdate(update);
                if (current_update > maior_update) {
                    maior_update = current_update;
                }
            }
        }
        if (config.containsKey(TelegramAPI.UPDATE_ID)) {
            config.replace(TelegramAPI.UPDATE_ID, maior_update);
        } else {
            config.put(TelegramAPI.UPDATE_ID, maior_update);
        }
        saveConfig();
    }

    private void setUpdate(long current_update) {
        config.replace(TelegramAPI.UPDATE_ID, current_update);
        saveConfig();
    }

    private long getLastUpdate() {
        return (long) config.get(TelegramAPI.UPDATE_ID);
    }

}
