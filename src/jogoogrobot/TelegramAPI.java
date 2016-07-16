/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoogrobot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author wesley
 */
public class TelegramAPI {
    //URL
        public static final String URL = "https://api.telegram.org/bot";
    //General
        public static final String ID = "id";
    
        
    //Result
        public static final String RESULT = "result";
    //Update
        public static final String UPDATE_ID = "update_id";
        public static final String MESSAGE = "message";
        public static final String INLINE_QUERY = "inline_query";
        public static final String CHOSEN_INLINE_RESULT = "chosen_inline_result";
        public static final String CALLBACK_QUERY = "callback_query";
    //Message
        public static final String CHAT = "chat";
        public static final String MESSAGE_ID = "message_id";
        public static final String FROM = "from";
        public static final String TEXT = "text";
        public static final String ENTITIES = "entities";
        public static final String DATE = "date";
        
        //Entities
            public static final String OFFSET = "offset";
            public static final String LENGTH = "length";
            public static final String TYPE = "type";
            
            //type
            public static final String BOT_COMMAND = "bot_command";
        
        
    //User
        public static final String FIRST_NAME = "first_name";
        
        
        
    
    
    
    public static JSONObject getMe(String token){
        String connection_url = URL+token+"/getMe";
        JSONObject jResult = null;
        
        try{
            String sResult = Jsoup.connect(connection_url).ignoreContentType(true).execute().body();
            JSONParser parser = new JSONParser();
            jResult = (JSONObject) parser.parse(sResult);
        }catch(Exception e){
            System.out.println("[TelegramAPI] Erro no comando getMe() -> ("+e.getCause()+")");
            
        }
        
        return jResult;
    }
    
    public static JSONObject getUpdates(String token){
        String connection_url = URL+token+"/getUpdates";
        
        JSONObject jResult = null;
        
        try{
            String sResult = Jsoup.connect(connection_url).ignoreContentType(true).execute().body();
            JSONParser parser = new JSONParser();
            jResult = (JSONObject) parser.parse(sResult);
        }catch(Exception e){
            System.out.println("[TelegramAPI] Erro no comando getUpdates() -> ("+e.getCause()+")");
        }
        
        return jResult;
    }
    
    public static JSONObject getUpdates(String token, long offset){
        String connection_url = URL+token+"/getUpdates?offset="+offset;
        
        JSONObject jResult = null;
        
        try{
            String sResult = Jsoup.connect(connection_url).ignoreContentType(true).execute().body();
            JSONParser parser = new JSONParser();
            jResult = (JSONObject) parser.parse(sResult);
        }catch(Exception e){
            System.out.println("[TelegramAPI] Erro no comando getUpdates() -> ("+e.getCause()+")");
        }
        
        return jResult;
    }
    
    
    public static JSONObject sendMessage(String token, String chat_id, String text){
        String connection_url = URL+token+"/sendmessage?chat_id="+chat_id+"&text="+text;
        JSONObject jResult = null;
        
        try{
            String sResult = Jsoup.connect(connection_url).ignoreContentType(true).execute().body();
            JSONParser parser = new JSONParser();
            jResult = (JSONObject) parser.parse(sResult);
        }catch(Exception e){
            System.out.println("[TelegramAPI] Erro no comando sendMessage() -> ("+e.getCause()+")");
        }
        
        return jResult;
    }
    
    public static boolean isCommand(JSONObject message){
        boolean is_command = false;
        
        if(message.containsKey(ENTITIES)){
            JSONArray entities = (JSONArray) message.get(ENTITIES);
            
            JSONObject entity = (JSONObject) entities.get(0);
            
            if(entity.get(TYPE).toString().equals(BOT_COMMAND)){
                is_command = true;
            }
        }
        
        return is_command;
    }
    
    public static void keyboard(String token, String chat_id){
       
        
        String keyboard = "{\"keyboard\":[[\"option1\"],[\"option2\"]]}";
        
        String connection_url = URL+token+"/sendmessage?chat_id="+chat_id+"&reply_markup="+keyboard;
        JSONObject jResult = null;
        
        try{
            String sResult = Jsoup.connect(connection_url).ignoreContentType(true).execute().body();
            JSONParser parser = new JSONParser();
            jResult = (JSONObject) parser.parse(sResult);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //return jResult;      
       
    }
 
    
}
