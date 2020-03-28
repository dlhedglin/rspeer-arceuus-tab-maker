import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

@ScriptMeta(name = "Make Tabs",  desc = "Script description", developer = "Developer's Name", category = ScriptCategory.MAGIC)
public class makeTabs extends Script {
    private static Area lect = Area.rectangular(1668, 3769, 1680, 3765);
    private static Area bankArea = Area.rectangular(1635, 3752, 1624, 3742);
    private static String darkEss = "Dark essence block";
    private static String manorTele = "Draynor manor teleport";
    @Override
    public int loop() {
        if(Inventory.getCount(darkEss) > 0)
        {
            if(lect.contains(Players.getLocal()))
            {
                if(Interfaces.isOpen(264))
                {
                    Interfaces.getComponent(264,27).interact("Make All");
                }
                else if(!Interfaces.isOpen(264) && !Players.getLocal().isAnimating())
                {
                    SceneObjects.getNearest("Lectern").interact("Study");
                    Time.sleepUntil(()-> Interfaces.isOpen(264), 3000);
                }
            }
            else
            {
                Movement.walkToRandomized(lect.getCenter());
                toggleRun();
                Time.sleepUntil(()-> !Players.getLocal().isMoving() || lect.contains(Players.getLocal()), Random.low( 1333, 2222));

            }
        }
        else if(Inventory.getCount(darkEss) == 0)
        {
            if(bankArea.contains(Players.getLocal()))
            {
                if(Bank.isOpen())
                {
                    if(Inventory.contains(manorTele))
                    {
                        Bank.depositAll(manorTele);
                        Time.sleepUntil(()-> Inventory.getCount(manorTele) == 0, 3000);
                    }
                    if(Movement.getRunEnergy() < 20)
                    {
                        if(Inventory.getFirst("Strange fruit") != null)
                        {
                            Inventory.getFirst("Strange fruit").interact("Eat");
                        }
                        else
                        {
                            Bank.withdraw("Strange fruit", 1);
                            Time.sleepUntil(()-> Inventory.getCount("Strange fruit") > 0, 3000);
                        }
                    }
                    else if(!Movement.isStaminaEnhancementActive())
                    {
                        if(!haveStaminas())
                        {
                            Bank.getFirst(a-> a.getName().contains("Stamina")).interact("Withdraw-1");
                            Time.sleepUntil(()-> haveStaminas(),3000);
                        }
                        else
                        {
                            Inventory.getFirst(a-> a.getName().contains("Stamina")).interact("Drink");
                        }
                    }
                    else if(haveStaminas() && Movement.isStaminaEnhancementActive())
                    {
                        Bank.deposit(a-> a.getName().contains("Stamina"),1);
                    }
                    else if(Bank.contains(darkEss))
                    {
                        Bank.withdrawAll(darkEss);
                        Time.sleepUntil(()-> Inventory.getCount(darkEss) > 0, 3000);
                    }
                    else
                        return -1;

                }
                else
                {
                    SceneObjects.getNearest("Bank booth").interact("Bank");
                    Time.sleepUntil(()-> Bank.isOpen(), 3000);
                }
            }
            else
            {
                Movement.walkToRandomized(bankArea.getCenter());
                toggleRun();
                Time.sleepUntil(()-> !Players.getLocal().isMoving() || bankArea.contains(Players.getLocal()), Random.low( 1333, 2222));
            }
        }

        return Random.low(555,2222);
    }
    private boolean toggleRun()
    {
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > Random.nextInt(10, 30)) { // If our energy is higher than a random value 10-30
            Movement.toggleRun(true); // Toggle run
            return true;
        }
        return false;
    }
    private boolean haveStaminas()
    {
        if(Inventory.getFirst(a-> a.getName().contains("Stamina")) != null)
        {
            return true;
        }
        else
            return false;
    }
}
