package cop5556fa17;

import java.util.HashMap;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.Declaration;

public class SymbolTable {
    HashMap<String, Declaration> hm = new HashMap<String, Declaration>();
   
    public void insert(String key, Declaration dec){
        if(!hm.containsKey(key)){
            hm.put(key, dec);
        }
    }
   
    Declaration lookupDec(String key){
        Declaration dec = null;
        if(hm.containsKey(key)){
            dec = hm.get(key);
        }
        return dec;
    }
   
    Type lookupType(String key){
        Type tp = null;
        if(hm.containsKey(key)){
            tp = hm.get(key).type;
        }
        return tp;
    }
   
}
