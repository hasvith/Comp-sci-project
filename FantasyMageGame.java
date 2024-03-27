package fantasymagegame;

import java.util.Random;
import java.util.Scanner;

// Main class to start the Fantasy Mage Game
public class FantasyMageGame {
    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}

// Handles game logic, player interactions, and game flow
class Game {
    private Player player;
    private Enemy enemy;
    private Scanner scanner;
    private Random random;
    private boolean isBossBattle;

    // Initializes game components and settings
    public Game() {
        player = new Player("Novice Mage", 20, new Spell("Fireball", 2, 6, 2));
        random = new Random();
        scanner = new Scanner(System.in);
        isBossBattle = false;
    }

    // Starts and manages the main game loop
    public void startGame() {
        System.out.println("Welcome to the Fantasy Mage Game!");
        while (player.isAlive()) {
            // Chance to encounter a boss
            if(random.nextInt(10) == 0) {
                enemy = new Boss("Dark Sorcerer", 50, 10);
                isBossBattle = true;
            } else {
                enemy = EnemyFactory.createEnemy();
                isBossBattle = false;
            }
            
            System.out.println("You are a " + player.getTitle() + " with " + player.getHitPoints() + " hit points.");
            System.out.println("You encounter a wild " + enemy.getTitle() + "!");
            
            // Combat loop
            while (player.isAlive() && enemy.isAlive()) {
                System.out.println("\nYour HP: " + player.getHitPoints());
                System.out.println(enemy.getTitle() + " HP: " + enemy.getHitPoints());
                System.out.println("Do you want to Cast Spell (c), Flee (f), or Give Up (g)?");

                String input = scanner.nextLine();
                
                // Player chooses to cast a spell
                if ("c".equalsIgnoreCase(input)) {
                    player.castSpell(enemy);
                    if (enemy.isAlive() && player.isAlive()) {
                        enemy.attack(player);
                    }
                } 
                // Player chooses to flee
                else if ("f".equalsIgnoreCase(input)) {
                    if(isBossBattle) {
                        System.out.println("You can't flee from a boss battle!");
                    } else {
                        System.out.println("You managed to flee from the " + enemy.getTitle() + ".");
                        player.gainHitPoints(1); // Player gains a small health boost for fleeing
                        break;
                    }
                }
                // Player chooses to give up
                else if ("g".equalsIgnoreCase(input)) {
                    System.out.println("You've chosen to give up. The game will now end.");
                    player.surrender();
                    break;
                } else {
                    System.out.println("Invalid command. Please choose 'c' to Cast Spell, 'f' to Flee, or 'g' to Give Up.");
                }
            }

            // Player levels up after surviving a battle
            if (player.isAlive()) {
                System.out.println("After a tough battle, you've grown stronger. Preparing for the next challenge...");
                player.levelUp();
            }
        }

        // Player defeated
        if (!player.isAlive()) {
            System.out.println("Alas, you have been defeated in battle.");
        }
        scanner.close();
    }
}

// Generates random enemies
class EnemyFactory {
    private static Random random = new Random();

    // Creates and returns a random enemy
    public static Enemy createEnemy() {
        int enemyType = random.nextInt(3);
        switch (enemyType) {
            case 0: return new Enemy("Goblin", 8, 3);
            case 1: return new Enemy("Troll", 12, 4);
            case 2: return new Enemy("Ogre", 15, 5);
            default: return new Enemy("Goblin", 8, 3);
        }
    }
}

// Base character class for shared attributes and methods
class Character {
    protected String title;
    protected int hitPoints;
    protected boolean alive;
    protected Random random;

    // Sets basic character attributes
    public Character(String title, int hitPoints) {
        this.title = title;
        this.hitPoints = hitPoints;
        this.alive = true;
        this.random = new Random();
    }

    // Checks if the character is still alive
    public boolean isAlive() {
        return alive;
    }

    // Gets the character's title
    public String getTitle() {
        return title;
    }

    // Gets the character's current hit points
    public int getHitPoints() {
        return hitPoints;
    }

    // Applies damage to the character, potentially killing them
    public void receiveDamage(int damage) {
        hitPoints -= damage;
        if (hitPoints <= 0) {
            alive = false;
            System.out.println(title + " has been defeated!");
        }
    }

    // Base attack method to be overridden by specific character types
    public void attack(Character opponent) {
        int attackDamage = random.nextInt(5) + 1;
        System.out.println(title + " deals " + attackDamage + " damage to " + opponent.getTitle());
        opponent.receiveDamage(attackDamage);
    }

    // Base defend method to mitigate damage
    public int defend() {
        return random.nextInt(3) + 1;
    }
}

// Player class with specific abilities like casting spells
class Player extends Character {
    private Spell spell;

    // Initializes player with a title, hit points, and a spell
    public Player(String title, int hitPoints, Spell spell) {
        super(title, hitPoints);
        this.spell = spell;
    }

    // Casts a spell on an enemy, applying damage
    public void castSpell(Enemy enemy) {
        int spellCost = spell.getCost();
        if (spellCost > this.hitPoints) {
            System.out.println("You do not have enough HP to cast the spell!");
            return;
        }

        this.hitPoints -= spellCost;
        System.out.println(title + " casts " + spell.getName() + " at a cost of " + spellCost + " HP");

        int damage = random.nextInt(spell.getMaxDamage() - spell.getMinDamage() + 1) + spell.getMinDamage();
        System.out.println("The spell hits " + enemy.getTitle() + " for " + damage + " damage");
        enemy.receiveDamage(damage);
    }

    // Levels up the player, improving health and potentially damage
    public void levelUp() {
        System.out.println("Leveling up! Hit points and damage potential increased.");
        hitPoints += 10; // Increase player's hit points
        // Could also enhance spell damage or learn new spells here
 
    }

    // Allows the player to gain a small number of hitpoints, useful for fleeing
    public void gainHitPoints(int amount) {
        hitPoints += amount;
        System.out.println("Gained " + amount + " hitpoint(s). Current HP: " + hitPoints);
    }

    // Ends the game if the player chooses to give up
    public void surrender() {
        System.out.println("You've decided to give up. Your journey ends here.");
        alive = false;
    }
}

// Spell class for magic abilities
class Spell {
    private String name;
    private int minDamage;
    private int maxDamage;
    private int cost;

    // Initializes a spell with its properties
    public Spell(String name, int minDamage, int maxDamage, int cost) {
        this.name = name;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.cost = cost;
    }

    // Returns the spell's name
    public String getName() {
        return name;
    }

    // Returns the spell's minimum damage
    public int getMinDamage() {
        return minDamage;
    }

    // Returns the spell's maximum damage
    public int getMaxDamage() {
        return maxDamage;
    }

    // Returns the spell's casting cost
    public int getCost() {
        return cost;
    }
}

// Boss class for challenging enemies with stronger attacks
class Boss extends Enemy {
    // Initializes a boss with increased stats
    public Boss(String title, int hitPoints, int attackPower) {
        super(title, hitPoints, attackPower);
    }

    // Bosses perform stronger attacks than regular enemies
    @Override
    public void attack(Character opponent) {
        int attackDamage = random.nextInt(10) + 5;
        System.out.println(title + " unleashes a powerful attack for " + attackDamage + " damage!");
        opponent.receiveDamage(attackDamage);
    }

    // Bosses have enhanced defense mechanisms
    @Override
    public int defend() {
        return random.nextInt(5) + 3;
    }
}

// Enemy class for standard adversaries
class Enemy extends Character {
    private int attackPower;

    // Initializes an enemy with its attributes
    public Enemy(String title, int hitPoints, int attackPower) {
        super(title, hitPoints);
        this.attackPower = attackPower;
    }

    // Standard enemy attack method
    @Override
    public void attack(Character opponent) {
        int attackDamage = random.nextInt(attackPower) + 1;
        System.out.println(title + " attacks " + opponent.getTitle() + " for " + attackDamage + " damage");
        opponent.receiveDamage(attackDamage);
    }

    // Standard enemy defense method, reducing incoming damage
    @Override
    public int defend() {
        int defense = super.defend();
        System.out.println(title + " defends and mitigates " + defense + " damage");
        return defense;
    }
}
