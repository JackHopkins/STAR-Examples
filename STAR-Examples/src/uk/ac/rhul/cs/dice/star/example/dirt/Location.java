package uk.ac.rhul.cs.dice.star.example.dirt;

public class Location {

	private int x;
	private int y;
	private String agent;
	private String agentDirection;
	private String dirt;
	private String user;
	
	public Location(String agent, String agentDirection, String dirt, int x, int y) {
		this.x = x;
		this.y = y;
		this.agent = agent;
		this.agentDirection = agentDirection;
		this.dirt = dirt;
		user = "";
	}
	public Location(int x, int y) {
		this.x = x;
		this.y = y; 
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getAgentDirection() {
		return agentDirection;
	}

	public void setAgentDirection(String agentDirection) {
		this.agentDirection = agentDirection;
	}

	public String getDirt() {
		return dirt;
	}

	public void setDirt(String dirt) {
		this.dirt = dirt;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}