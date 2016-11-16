package scripts;
 
import java.awt.Color; //to get different colors
import java.awt.Font; //to change font
import java.awt.Graphics; //paint
import java.awt.Graphics2D; //needed for the image
import java.awt.Image; //same as above
import java.io.IOException; //this is needed for the loading of the image
import java.net.URL; //same as above
 
import javax.imageio.ImageIO; //same as above
 
import org.tribot.api.Timing; //to calculate time things
import org.tribot.api2007.Skills; //to get XP/levels
import org.tribot.script.interfaces.Painting; //for onPaint()
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
 
public class TFLYakker extends Script implements Painting {
        private static RSTile YakTile = new RSTile(2326,3801);
        private static final long startTime = System.currentTimeMillis();
    Font font = new Font("Verdana", Font.BOLD, 14);
    private long AttackstartXP = Skills.getXP("Attack");
    private long StrengthstartXP = Skills.getXP("Strength");
    private long DefencestartXP = Skills.getXP("Defence");
        private final int[] Yak = {5476};
        private boolean Running = true;
        private String antiBanString;
        private boolean antiBanScreen = false;
 
        private void KillYak() {
                while(Combat.isUnderAttack() == true || Combat.getAttackingEntities().length > 0 ){
                        antiBan();
                        sleep(175,175);
                }
                RSNPC[] Yaks = NPCs.findNearest(Yak);
                Yaks = NPCs.sortByDistance(Player.getPosition(), Yaks);
                if (Yaks != null && Yaks.length > 0 && Player.getAnimation() == -1){
                for(RSNPC CurrentYak:Yaks){
                        if(CurrentYak.getPosition().distanceTo(Player.getPosition()) > 8){
                                WebWalking.walkTo(CurrentYak);
                        }
                        if(CurrentYak.isInCombat() == false){
                                if(CurrentYak.isOnScreen() == false){
                                        Camera.turnToTile(CurrentYak);
                                        DynamicClicking.clickRSNPC(CurrentYak, "Attack");
                                        sleep(250,250);
                                        break;
                                }
                                else{
                                DynamicClicking.clickRSNPC(CurrentYak, "Attack");
                                sleep(250,250);
                                break;
                                }
                        }
                        else{
                                //Do Nothing
                        }
                }
                sleep(250,250);
                }
        }
 
        @Override
        public void run() {
                onStart();
                while(Running){
                                KillYak();
                        }
                }
       
        private void onStart() {
                Combat.setAutoRetaliate(true);
                //Mouse.setSpeed(135);
               
        }
       
 
        /*ANTI-BAN*/ //Credit to Nova for Antiban
    public void antiBan() {
        antiBanScreen = true;
        int cameraMove = General.random(-100, 100);
        int afkTimer = General.random(1000, 5000);
        switch (General.random(1, 500)) {
            case 100:
                System.out.println("[NOVA ANTI-BAN] - Random camera angle(" + cameraMove + ")");
                antiBanString = "[NOVA ANTI-BAN] - Random camera angle(" + cameraMove + ")";
                Camera.setCameraAngle(cameraMove);
                break;
            case 200:
                System.out.println("[NOVA ANTI-BAN] - Random camera rotation(" + cameraMove + ")");
                antiBanString = "[NOVA ANTI-BAN] - Random camera rotation(" + cameraMove + ")";
                Camera.setCameraRotation(cameraMove);
                break;
            case 300:
                int mouseRandomX = General.random(-500, 500);
                int mouseRandomY = General.random(-500, 500);
                System.out.println("[NOVA ANTI-BAN] - Moving mouse(" + mouseRandomX + ", " + mouseRandomY + ")");
                antiBanString = "[NOVA ANTI-BAN] - Moving mouse(" + mouseRandomX + ", " + mouseRandomY + ")";
                Mouse.move(mouseRandomX, mouseRandomY);
                break;
            case 400:
                System.out.println("[NOVA ANTI-BAN] - AFKing(" + (double) afkTimer / 1000 + "sec)");
                antiBanString = "[NOVA ANTI-BAN] - AFKing(" + (double) afkTimer / 1000 + "sec)";
                sleep(afkTimer);
                break;
            case 500:
                System.out.println("[NOVA ANTI-BAN] - Moving mouse off screen(" + (double) afkTimer / 1000 + "sec)");
                antiBanString = "[NOVA ANTI-BAN] - Moving mouse off screen(" + (double) afkTimer / 1000 + "sec)";
                Mouse.leaveGame();
                sleep(afkTimer);
                break;
        }
    }
        @Override
        public void onPaint(Graphics g) {
 
        long timeRan = System.currentTimeMillis() - startTime;
        long AttackgainedXP = Skills.getXP("Attack") - AttackstartXP;
        long StrengthgainedXP = Skills.getXP("Strength") - StrengthstartXP;
        long DefencegainedXP = Skills.getXP("Defence") - DefencestartXP;
 
        g.setFont(font);
        g.setColor(new Color(44, 44, 44));
        g.drawString("TFL Yakker ", 25, 360);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 275, 360);
        g.drawString(" Attack XP Gained: " + AttackgainedXP, 275, 390);
        g.drawString(" Strength XP Gained: " + StrengthgainedXP, 275, 420);
        g.drawString(" Defence XP Gained: " + DefencegainedXP, 275, 450);
        }
}