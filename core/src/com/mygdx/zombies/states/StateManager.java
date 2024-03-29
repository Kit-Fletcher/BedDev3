package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.mygdx.zombies.Zombies;

/**
 * Class for managing and switching game states
 */
public class StateManager {

	private static State currentState;
	
	//Enum of state ids, used to identify different types of state
	public static enum StateID {
		MAINMENU, CREDITSMENU, OPTIONSMENU, ENDSCREEN, BRIEFINGSCREEN,
		PLAYERSELECTMENU, STAGE1, STAGE2, STAGE3, TESTSTAGE1, TESTSTAGE2,
		STAGE4, STAGE5, STAGE6, MINI1, MINI2, MINI4, MINI5
	}

	/**
	 * Constructor for the state manager
	 */
	public StateManager() {
		//Load the main menu first, when the game starts
		currentState = new MainMenu();
	}

	/**
	 * Method is run when the game window is resized
	 * @param width - the new window width
	 * @param height - the new window height
	 */
	public void resize(int width, int height) {
		currentState.resize(width, height);
	}

	/**
	 * Load a new state, clearing the memory of the old state
	 * @param newState - the new state to load
	 */
	public static void loadState(State newState) {
		currentState.dispose();
		currentState = newState;
	}
	
	/**
	 * Load the state associated with the given state id
	 * @param stateID - the state id to identify the state to load
	 */
	public static void loadState(StateID stateID) {
		loadState(stateID, -1);
	}
	
	public static void loadStateEntry0(StateID stateID) {
		loadState(stateID, 0);
	}
	/**
	 * Load the state associated with the given state id and pass the entry id
	 * @param stateID - the state id to identify the state to load
	 * @param entryID - the entry id to pass
	 */
	public static void loadState(StateID stateID, int entryID) {
		
		State tempState = null;
		//Switch statement to run unique load code for each state
		switch(stateID) {
			case MAINMENU:
				tempState = new MainMenu();
				break;
			case CREDITSMENU:
				tempState = new CreditsMenu();
				break;
			case OPTIONSMENU:
				tempState = new OptionsMenu();
				break;
			case ENDSCREEN:
				tempState = new EndScreen();
				break;
			case PLAYERSELECTMENU:
				tempState = new PlayerSelectMenu();
				break;
			case BRIEFINGSCREEN:
				tempState = new BriefingScreen();
				break;
			case TESTSTAGE1:
				tempState = new Level("teststage", entryID);
				break;
			case TESTSTAGE2:
				tempState = new Level("teststage2", entryID);
				break;
			case STAGE1:
				tempState = new Level("World_One", entryID);
				break;
			case STAGE2:
				tempState = new Level("World_Two", entryID);
				break;
			case STAGE3:
				tempState = new Level("World_Three", entryID);
				break;
			case STAGE4:
				tempState = new Level("World_Four", entryID);
				break;
			case STAGE5:
				tempState = new Level("World_Five", entryID);
				break;
			case STAGE6:
				tempState = new Level("World_Six", entryID);
				break;
			case MINI1:
				tempState = new Minigame("World_One_Minigame", 100, 2200, 3, StateID.STAGE1);
				break;
			case MINI2:
				tempState = new Minigame("World_Two_Minigame", 100, 2200, 5, StateID.STAGE2);
				break;
			case MINI4:
				tempState = new Minigame("World_Four_Minigame", 1000, 2300, 1, StateID.STAGE4);
				break;
			case MINI5:
				tempState = new Minigame("World_Five_Minigame", 2200, 2200, 10, StateID.STAGE5);
				break;
			default:
				System.err.println("Error: Unrecognised gate destination");
				break;
		}
		
		//Do not load if state has not been set
		if(tempState != null)
			loadState(tempState);
	}

	/**
	 * Update the current state
	 */
	public void gameLoop() {
		currentState.update();
	}

	/**
	 * Render the current state
	 */
	public void render() {
		currentState.render();
		Gdx.graphics.setTitle(Zombies.windowTitle+" ["+Gdx.graphics.getFramesPerSecond() + "]");
	}

	/**
	 * Erase the current state and clean the memory
	 */
	public void dispose() {
		currentState.dispose();
	}
}
