package scripts;

import java.awt.*;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.Walking;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;




@SuppressWarnings("unused")
@ScriptManifest(authors = { "JewishBotter", "5ubz3r0" }, category = "Woodcutting", name = "TFLMagicCutter")
public class TFLMagicCutter extends Script implements Painting{

	//Instances
	
	private final long startTime = System.currentTimeMillis();
	private final int startXP = Skills.getXP(SKILLS.WOODCUTTING);
	private int currentXP = startXP;
    private int startLvl = Skills.getActualLevel(SKILLS.WOODCUTTING);
    private int currentLvl= startLvl;
    
    private final String axes[] = {
			"Bronze axe",
			"Iron axe",
			"Steel axe",
			"Black axe",
			"Mithril axe",
			"Adamant axe",
			"Rune axe",
			"Dragon axe"
		};
    
    private final int TREE_ID = 2110;
    private final int LOG_ID = 1513;
    private final int CUTTING_ANIMATION = 875;
    private final int BANKER_ID = 28;
	private final int BANK_BOOTH_ID = 25808;
	
	private final RSTile CHOPPING_TILE1 = new RSTile(2693, 3426);
	private final RSTile CHOPPING_TILE2 = new RSTile(2696, 3425);
	private final RSTile BANK_TILE = new RSTile(2724, 3493);
	private RSTile[] WALK_TO_MAGICS = { BANK_TILE, new RSTile(2724, 3481), new RSTile(2724, 3474), new RSTile(2721, 3462), new RSTile(2715, 3451), 
			new RSTile(2712, 3439), new RSTile(2706, 3429), new RSTile(2699, 3424), CHOPPING_TILE1};
	private RSTile[] WALK_TO_BANK = { CHOPPING_TILE1, new RSTile(2708, 3435), new RSTile(2714, 3449), new RSTile(2726, 3461), new RSTile(2727, 3469),
			new RSTile(2726, 3484), BANK_TILE};
	private RSTile START_TILE;
	private RSObject[] magics;
	private RSObject nearestMagic;
	private RSTile currentMagic = new RSTile(0,0);

	
	
	@Override
	public void onPaint(Graphics g) {
		// TODO Auto-generated method stub
		//STATISTICS
		long timeRan = System.currentTimeMillis() - startTime;
		int xpGained = currentXP - startXP;
		int xpPerHour = (int) (xpGained / (timeRan / 3600000D));
		int lvlsGained = currentLvl-startLvl;
	
		g.setColor(Color.WHITE);
		g.drawString("TFLMagicCutter", 10, 50);
		g.drawString("Running for: " + Timing.msToString(timeRan), 10, 70);
		g.drawString("Gained " + xpGained + " xp (" + xpPerHour + " xp/h)", 10, 110);
		g.drawString("WC level: " + currentLvl + " (+" + lvlsGained + ")", 10, 130);
	}
	
	private boolean checkForNests(){ //picks up nest if there is room is inventory
		RSGroundItem[] nests = GroundItems.find(new int[] {5073, 5074, 5075});
		if (nests.length > 0 && !Inventory.isFull()){
			nests[0].click("Take");
			println("Congratulations! You found a nest!");
			sleep(150,200);
			return true;
		}
		return false;
		
	}

	public void ent() {
        RSNPC[] ent = NPCs.findNearest("Magic");
        if (ent.length > 0) {
                if (ent[0].getPosition().distanceTo(currentMagic) < 2) {
                        println("Ent, escaping");
                        WebWalking.walkTo(CHOPPING_TILE1);
                        sleep(1000,1200);
                }
        }
}
	
	private void startScript() {
		Mouse.setSpeed(170);
		Camera.setCameraAngle(180);
		START_TILE = Player.getPosition();
	}
	
	private void walkToBank() {
		WebWalking.walkToBank();
		//mouseAntiBan();
		sleep(250,100);
	}
	
	private void bank(){
		if (Banking.openBank()){
			Banking.depositAllExcept(axes);
			Banking.close();
		}
	}
	
	private void walkingToMagics(){
		
		WebWalking.walkTo(CHOPPING_TILE1);
		sleep(200,150);
		
	}
	
	//CREDIT: Thanks JJ for the dynamic sleep
	
	public void sleep() {
       
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2000)) {
                if (Player.isMoving()) {
                        t = System.currentTimeMillis();
                } else {
                        break;
                }
                sleep(50, 150);
        }
}
	
	//CREDITS: Thank you to mhdjml for this great clicking method
	
	private boolean checkActions(RSObject object, String action) {
		if (object != null) {
			for (String s : object.getDefinition().getActions())
				return s.contains(action);
		}
		return false;
	}

	public void clickObject(int distance, String objects, String option) {
	    RSObject[] object = Objects.findNearest(100, objects);
        if (object != null) {
            int x = (int) object[0].getModel().getEnclosedArea().getBounds()
                    .getCenterX();
            int y = (int) object[0].getModel().getEnclosedArea().getBounds()
                    .getCenterY();
            Point p = new Point(x + General.random(0, 16), y
                    + General.random(0, 4));
            if (object[0].getPosition().isOnScreen()) {
                Mouse.move(p);
                
				if (Game.getUptext().contains(option)
                        && (checkActions(object[0], option))) {
                    Mouse.click(1);
                }
                if (!Game.getUptext().contains(option)) {
                    Mouse.click(3);
					
					if (ChooseOption.isOpen()
                            && ChooseOption.isOptionValid(option))
                        ChooseOption.select(option);
                    if (ChooseOption.isOpen()
                            && !ChooseOption.isOptionValid(option))
                        Camera.turnToTile(object[0].getPosition());
                }
            } else {
                if (Player.getPosition().distanceTo(object[0].getPosition()) > 4)
                    WebWalking.walkTo(object[0].getPosition());
                if (!object[0].getPosition().isOnScreen())
                    Camera.turnToTile(object[0].getPosition());
                while (Player.isMoving()) {
                    sleep(50, 80);
                }
            }
        }
    }
	
	private void chopMagics(){
		//We are finding the nearest Tree with the ID given
		magics = Objects.findNearest(20, "Magic tree");
		if (magics.length > 0 && magics[0] != null){
			if (magics[0].isOnScreen()){
				currentMagic = magics[0].getPosition();
				clickObject(20,"Magic tree", "Chop down");
				System.out.println("chopping tree");
			}else {
				if (Player.getPosition().distanceTo(magics[0].getPosition()) >= 1)
					WebWalking.walkTo(magics[0].getPosition());
					System.out.println("walking to magics position");
			}
			if (Player.getPosition().distanceTo(magics[0].getPosition()) <= 5 && !magics[0].isOnScreen()){
				Camera.turnToTile(magics[0].getPosition());
				sleep(200,150);
				if (!magics[0].isOnScreen()){
					WebWalking.walkTo(magics[0].getPosition());
					if (Player.isMoving())
						sleep();
				}
			}
			
		}
		
	}
	

	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startScript();
		println("Script Starting, make sure to have axe equipt or in inventory and start in Seers"
				+ "Village Bank!");
		while (true){
			sleep(50,25);
			checkForNests();
			ent();
			currentXP = Skills.getXP(SKILLS.WOODCUTTING);
            currentLvl = Skills.getActualLevel(SKILLS.WOODCUTTING);
			RSObject[] Tree = Objects.findNearest(10, "Magic tree");
			if (Tree.length > 0 
					&& !Inventory.isFull() 
					&& Player.getAnimation() == -1 
					&& Player.getPosition().distanceTo(CHOPPING_TILE1) < 10){
				chopMagics();
			}
			if (Inventory.isFull()){
				if (Player.getPosition().distanceTo(BANK_TILE) < 5){
					bank();
					sleep(150,100);
				} else {
					walkToBank();
				}
			}
			if (Player.getPosition().distanceTo(BANK_TILE) < 5 
					&& !Inventory.isFull()) {
				walkingToMagics();
			} else if (Player.getPosition().distanceTo(BANK_TILE) > 5 
					&& Player.getPosition().distanceTo(CHOPPING_TILE1) > 10 
					&& !Inventory.isFull()){
				walkingToMagics();
			}
			
			
		}
	}

}
