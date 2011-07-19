import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.w3c.dom.ls.*;
import org.xml.sax.*;
import java.io.*;
import java.util.*;

public class GameUtils {
  
    private final int WIDTH = 40, HEIGHT = 22;
    public int level = 1;
    public int maxLevel, diamondsNeeded;
    public enum Contents {
        EMPTY, DIRT, WALL, ROCK, FALLINGROCK, DIAMOND, FALLINGDIAMOND, AMOEBA, FIREFLY, BUTTERFLY, EXIT, PLAYER;
    }
    private Document doc;
    public Contents[][] field = new Contents[WIDTH][HEIGHT];

    public Contents[][] loadGame() {
      level = 1;
      try{
        readDocument("levels.xml");
      }
      catch(Exception e) {}
      try{
        initLevel(level);
      }
      catch(Exception e) { System.out.println("error reading level");}
      return field;
    }
        
    public Contents[][] increaseLevel(int lvl) {
      try {initLevel(lvl + 1); } catch(Exception e) {}
      level += 1;
      return field;
    }
    
    public int[] getPlayerLocation(Contents[][] field) {
      int[] ret = new int[2];
      for(int x = 0; x < 40; x ++) {
        for(int y = 0; y < 22; y++) {
          if(field[x][y] == Contents.PLAYER) {
            ret[0] = x;
            ret[1] = y;
            return ret;
          }
        }
      }
      return ret;
    }

      /*******************************************************************************************************/
    // Call this method only once at the beginning of your program. It reads the XML level file into a DOM.
    // Do not modify this method. (method provided by professor)
    private void readDocument(String filename) throws Exception {
        File f = new File(filename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(f);
        XPathFactory xpf = XPathFactory.newInstance();
        XPath path = xpf.newXPath();
        maxLevel = Integer.parseInt(path.evaluate("count(levelset/level)", doc));
    }
    
    // Call this method every time you want to start playing the given level. Do not modify this method unless
    // you really need to and are 100% sure of what you are doing. (method provided by professor)
    private void initLevel(int level) throws Exception {
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                field[x][y] = Contents.DIRT;
            }
        }
        
        Map<String, Contents> map = new HashMap<String, Contents>();
        map.put("wall", Contents.WALL);
        map.put("rock", Contents.ROCK);
        map.put("diamond", Contents.DIAMOND);
        map.put("amoeba", Contents.AMOEBA);
        map.put("dirt", Contents.DIRT);
        map.put("empty", Contents.EMPTY);
        map.put("firefly", Contents.FIREFLY);
        map.put("butterfly", Contents.BUTTERFLY);
        map.put("exit", Contents.EXIT);
        map.put("player", Contents.PLAYER);
        
        NodeList nlist = doc.getElementsByTagName("levelset");
        Node n = nlist.item(0);
        nlist = n.getChildNodes();
        int lvl = 0;
        for(int i = 0; lvl < level && i < nlist.getLength(); i++) {
            n = nlist.item(i);
            if(n.getNodeName().equals("level")) { lvl++; }
        }            
        diamondsNeeded = Integer.parseInt(n.getAttributes().getNamedItem("diamonds").getNodeValue());
        nlist = n.getChildNodes();
        for(int i = 0; i < nlist.getLength(); i++) {
            Node e = nlist.item(i);
            String tag = e.getNodeName();
            if(!map.containsKey(tag)) continue;
            NamedNodeMap attr = e.getAttributes();
            int x = Integer.parseInt(attr.getNamedItem("x").getNodeValue());
            int y = Integer.parseInt(attr.getNamedItem("y").getNodeValue());
            
            field[x][y] = map.get(tag);
        }
    }

}