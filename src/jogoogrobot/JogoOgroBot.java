/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoogrobot;

import org.json.simple.JSONObject;

/**
 *
 * @author wesley
 */
public class JogoOgroBot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String token = "246170147:AAEcAPdK0oDu0ndtzgz1KbM36gGY_pMlVIU";
        System.out.println("[MAIN] Iniciando thread...");
        BotRunner br = new BotRunner();
        br.token = token;
        Thread t = new Thread(br);
        t.run();
        
    }
    
}
